package Journal.APKSim;

import java.util.ArrayList;

import com.sun.org.apache.xerces.internal.util.NamespaceContextWrapper;

public class testCosSim {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		ArrayList<Double> srcList=new ArrayList<>();
		srcList.add(0.3);
		srcList.add(0.4);
		ArrayList<Double> dstList=new ArrayList<>();
		dstList.add(0.6);
		dstList.add(8.0);
		
		APKObj src=new APKObj();
		APKObj dst=new APKObj();
		
		double sim=src.calCosSim(srcList, dstList);
		System.out.println(sim);
	}

}
