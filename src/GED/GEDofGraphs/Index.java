package GED.GEDofGraphs;

import java.util.HashMap;
import java.util.Map;


public class Index {
	private Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	public Map<Integer, Integer> hungary(int[][] edge) {
		int size=edge[0].length;
		int [][]chi=new int[size][size];
		for(int i=0;i<size;i++){
			for(int j=0;j<size;j++){
				chi[i][j]=-edge[i][j];
			}
		}
		
        KM temp = new KM(chi);
        System.out.println("Hungry������");
        temp.km();
		System.out.println("Hungry�������");
//        System.out.println("Flag:");
        for(int i=0;i<temp.flag.length;i++){
        	map.put(i, temp.flag[i]);
//        	System.out.println(i+":");
//        	System.out.println(temp.flag[i]);
        }
        long total=0;
        for(int i=0;i<chi.length;i++){
        	total +=chi[temp.flag[i]][i];
        }
        System.out.println(-total);
        return map;
    }
}
