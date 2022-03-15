package GraphSimilarity;

import java.text.DecimalFormat;

import CandicateFamily.FamilyWeightScore;
import GraphSimilarity.KM.KM;

/*
 *     The input is the source subgraph set file and the destination subgraph set file, 
 *     and the corresponding family weight list
 *     The output is the similarity between sets of subgraphs
 *     Originally used to calculate the similarity between two APKs, 
 *     but due to the large influence of the benign part, the calculation effect is poor
 * 
 */
public class SubGraphSetSimilarity {
	private SubGraphSet srcSubGraphSet;
	private SubGraphSet dstSubGraphSet;
	private int srcSize = 0;
	private int dstSize = 0;
	private double simMatrix[][];
	private FamilyWeightScore weightScore;
	private double FinalSimScore = -1.0D;
	private double srcTotalScore = 0.0D;
	private double dstTotalScore = 0.0D;

	public SubGraphSetSimilarity(String srcSubGraphFilePath, String dstSubGraphFilePath, String familyWeightScoreFile) {
		srcSubGraphSet = new SubGraphSet(srcSubGraphFilePath);
		dstSubGraphSet = new SubGraphSet(dstSubGraphFilePath);
		srcSize = srcSubGraphSet.getSize();
		dstSize = dstSubGraphSet.getSize();
		simMatrix = new double[srcSize][dstSize];
		weightScore = new FamilyWeightScore(familyWeightScoreFile);
		calculateSimMatrx();
		iniTotalScore();
		// MaxSim();
	}

	public void iniTotalScore() {
		double src = 0.0D;
		for (int i = 0; i < srcSize; i++) {
			src += srcSubGraphSet.getGraphList().get(i).getSensitiveScore();
		}
		this.srcTotalScore = src;
		double dst = 0.0D;
		for (int i = 0; i < dstSize; i++) {
			dst += dstSubGraphSet.getGraphList().get(i).getSensitiveScore();
		}
		this.dstTotalScore = dst;
	}

	public void calculateSimMatrx() {
		for (int i = 0; i < srcSize; i++) {
			for (int j = i; j < dstSize; j++) {
				CommunitySubGraph srcSubGraph = new CommunitySubGraph();
				CommunitySubGraph dstSubGraph = new CommunitySubGraph();
				srcSubGraph = srcSubGraphSet.getGraphList().get(i);
				dstSubGraph = dstSubGraphSet.getGraphList().get(j);
				SubGraphSimilarity similarity = new SubGraphSimilarity(srcSubGraph.getGraph(),
						dstSubGraph.getGraph(), weightScore);
				simMatrix[i][j] = similarity.getSimScore();
				DecimalFormat df = new DecimalFormat("######0.000");
				String value = df.format(simMatrix[i][j]);
				simMatrix[i][j] = Double.valueOf(value);
			}
		}
	}

	public void showMatrix() {
		for (int i = 0; i < srcSize; i++) {
			System.out.println();
			for (int j = 0; j < dstSize; j++) {
				System.out.print(simMatrix[i][j] + ", ");
			}
		}
	}

	public void showInfo() {
		System.out.println("SrcGraphWeightScore: " + srcTotalScore);
		System.out.println("DstGraphWeightScore: " + dstTotalScore);
		System.out.println("TotalWeightScore: " + (srcTotalScore + dstTotalScore));
	}

	public void MaxSim() {

		int maxNum = getMax(srcSize, dstSize);
		int edge[][] = new int[maxNum][maxNum];
		for (int i = 0; i < maxNum; i++) {
			for (int j = 0; j < maxNum; j++) {
				if (i < srcSize && j < dstSize) {
					double srcSubGraphScore = srcSubGraphSet.getGraphList().get(i).getSensitiveScore();
					double dstSubGraphScore = dstSubGraphSet.getGraphList().get(j).getSensitiveScore();
					double subGraphWeightScore = (srcSubGraphScore + dstSubGraphScore)
							/ (srcTotalScore + dstTotalScore);
					edge[i][j] = (int) (simMatrix[i][j] * 1000);
				} else {
					edge[i][j] = 0;
				}
			}
		}
		KM calMax = new KM(edge);
		calMax.km();
		System.out.println("Flag:");
		for (int i = 0; i < calMax.flag.length; i++) {
			if (calMax.flag[i] < srcSize && i < dstSize) {
				String srcName = srcSubGraphSet.getGraphList().get(calMax.flag[i]).getFileName();
				String dstName = dstSubGraphSet.getGraphList().get(i).getFileName();
				System.out.println(srcName + "#" + dstName + ": " + edge[calMax.flag[i]][i]);
			}
		}
		int total = 0;
		for (int i = 0; i < edge.length; i++) {
			total += edge[calMax.flag[i]][i];
		}
		this.FinalSimScore = Double.valueOf(total) / 1000;
	}

	public int getMax(int k1, int k2) {
		if (k1 >= k2) {
			return k1;
		} else
			return k2;
	}

	public double getFinalSimScore() {
		return FinalSimScore;
	}

	public void setFinalSimScore(double finalSimScore) {
		FinalSimScore = finalSimScore;
	}

}
