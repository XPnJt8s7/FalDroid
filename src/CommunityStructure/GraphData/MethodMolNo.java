package CommunityStructure.GraphData;

import java.util.Map.Entry;

public class MethodMolNo {
		private String methodName="";
		private String molNo="";
		
		public MethodMolNo(){
			
		}
		public MethodMolNo(String input){
			try {
				int k=input.lastIndexOf(",");
				String name=input.substring(0, k);
				String molNo=input.substring(k+1);
				this.methodName=name;
				this.molNo=molNo;
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		public String getCommonString(){
			return this.methodName+","+molNo;
		}
		public String getMethodName() {
			return methodName;
		}
		public void setMethodName(String methodName) {
			this.methodName = methodName;
		}
		public String getMolNo() {
			return molNo;
		}
		public void setMolNo(String molNo) {
			this.molNo = molNo;
		}
		
}
