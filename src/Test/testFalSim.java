package Test;

import SubgraphsInDifferentFamily.sigsubgraphFalSimilarity;
import SubgraphsInDifferentFamily.sigsubgraphInOneFal;

public class testFalSim {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
				String srcFalPath="/home/fan/lab/FamilyClassification/data/AnserverBot/FamilyInfo/Im--0.5/";
				String dstFalPath="/home/fan/lab/FamilyClassification/data/BaseBridge/FamilyInfo/Im--0.5/";
				
				sigsubgraphInOneFal geinimi=new sigsubgraphInOneFal(srcFalPath);
				sigsubgraphInOneFal adrd=new sigsubgraphInOneFal(dstFalPath);
				
				sigsubgraphFalSimilarity similarity=new sigsubgraphFalSimilarity(geinimi, adrd);
				
				System.out.println(similarity.getLog());
	}

}
