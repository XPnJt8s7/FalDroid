package FlowModel;

public class TestModel {
	public static TrainModel model;
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TrainModel model=new TrainModel();
		String testFilePath="/home/fan/lab/Family/small-size/exp4/test-1/kmin/2df648d93addf72cbc5099fd250c3f2f.apk";
		model.processOneAPK(testFilePath);
		
	}
    public static void iniModel(){
    	try {
			model=new TrainModel();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
    }
  //  public static void 
}
