package Test;

import GraphSimilarity.KM.KM;

public class testKM {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		int[][] edge = { {-3,-5,8,4,1,3 }, 
								{-8,-2,0,2,1,0}, 
								{-3,-4,6,1,0,-8}, 
								{0,3,2,0,7,1},
								{1,4,2,3,3,2},
								{-3,2,1,5,-2,-2}};
KM temp = new KM(edge);
temp.km();
System.out.println("Flag:");
for(int i=0;i<temp.flag.length;i++){
	System.out.println(temp.flag[i] +"#"+i+": "+edge[temp.flag[i]][i]);
}
int total=0;
for(int i=0;i<edge.length;i++){
	total +=edge[temp.flag[i]][i];
}
System.out.println(total);
		
	}

}
