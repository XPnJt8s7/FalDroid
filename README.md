# FalDroid

## Requirements

Requeriments:

- Install Java JDK 8
- Install `apktool`
- If using OpenJDK, copy the `rt.jar` to  the `lib` directory of the project
- Install `igraph` and `networkx` Python 2 packages:

```bash
pip2 install python-igraph==0.8.3 networkx==1.9 numpy
```

- Create a `tmpfs` file in your home directory:

```bash
mkdir ~/tmpfs
sudo mount -t tmpfs -o size=2G,uid=1000,gid=1000 tmpfs ~/tmpfs
```

All binaries are available in the `bin` directory.

(Optional) If necessary, compile all the files:

```bash
javac -cp "./src:./lib/*" -d bin $(find . -name '*.java')
```

---

## Variables configuration file

First, the `ConstantVar.ConstantValue` has several configuraiton variables to change according to implementation of the project. For example, the variable `FAMILIESDIRPATH_STRING` is used as the default family directory, in this case called `Families`, and it must contain at least all the subdirectories of the different families, even if another family directory is used.

---

## Families analysis with APKTool

Analyze the `./Families` directory. This directory is composed of several sub-folders for each family, and inside each sub-folder there are `.apk` files.

```text
Families
├── fam01
│   ├── apk01.apk
│   ├── apk02.apk
│   ├── apk03.apk
│   ├── apk04.apk
│   ...
├── fam02
│   ├── app_a.apk
│   ├── app_b.apk
│   ├── app_c.apk
│   ├── app_d.apk
│   ...
...
```

This correspond to seciton *A Preprocessing* part in the FalDroid article.

```bash
java -cp "lib/*:bin" Util.FamilyStatistic.DirAnalysis -tmpfs ~/tmpfs
```

It will output, for each family, a `$family/apktool` directory for each application, where `$family` is the a family name. Each application directory contains a `SICG` directory containing the following files:

- `Information.txt`
- `ReducedGraph.dot`
- `ReducedGraph.gexf`
- `Sink.txt`
- `SourceGraph.gexf`
- `Source.txt`

---

## Sensitive API call weight assignment

Then, the *sensitive API-call related graph* (SARG) as the `ReducedGraph` is used to generate the *frequent graphs* (fregraphs) later on. The following class calculates the weight of each API call for the families.

This correspond to section *II.A.1 Weight Assignment of Sensitive API call* in the article.

```bash
java -cp "lib/*:bin" Util.FamilyStatistic.CalculateFamilySensitvieMethodWeight
```

This class will output a `$family/FamilyInfo` directory for each family containing two files:

- `MethodWeight.txt`
- `MethodNumber.txt`

---

## SARG to sensitive subgraphs

Now the SARG is divided into sub-graphs. The class uses a community detection algorithm to calculte the possible divisions of the graph. Then the class divides it according to the result.

This correspond to the section *II.B.1 Community Detection* of the article.

```bash
java -cp "lib/*:bin" Util.FamilyStatistic.CommunityDetection
```

This class will output the sub-graphs found by the community algorithm *infomap*. The output can be found in the `$family/apktool/$apk/SICG/Community` directory of every application in the family.

---

## Subgraph matching

After that, a clustering algorithm is used to produce the fregraphs. It uses the similarity metrics proposed in section *II.B.2 Subgraph Matching*.

This correpond to section *II.B.3 Subgraph Matching* of the article.

```bash
java -cp "lib/*:bin" Util.FamilyStatistic.GetFamilyClusterResult
```

---

## Feature Space Construction

Then, generate a the feature vector from a fregraph vector. A fregraph contains a list of paths to the subgraphs, and a weight assigned to it (from the subgraphs).

The feature vector, then, is a list of samples that has a weight vector and a family label associated to it. The weight vector is created from the similarity of the sample's subgraph and a given fregraph (each weight for all found fregraphs).

This correspond to section *II.C Feature Construction* of the article.

```bash
java -cp "lib/*:bin" Util.BasicStatisticExperiment.GenerateFeatFile
```

---

## Train/Test Files Generation

Once the `.model` files are created, training and test sets can be generated.
The script will output a `*.arff` file for each dataset.
The feature vectors directory must be specified with the `-feature` option.
Each dataset directory must contain the same structure (same families) as the family directory used so far (default: `Families`).

For a training dataset, the `-train` option is used:

```bash
java -cp "lib/*:bin" Util.BasicStatisticExperiment.GenerateTrainTestFile -t ~/tmpfs -family ./Families -feature results/FamilyModel/featureSpace -train /path/to/training_dataset
```

For a test dataset, the `-test` option is used:

```bash
java -cp "lib/*:bin" Util.BasicStatisticExperiment.GenerateTrainTestFile -t ~/tmpfs -family ./Families -feature results/FamilyModel/featureSpace -test /path/to/test_dataset
```
