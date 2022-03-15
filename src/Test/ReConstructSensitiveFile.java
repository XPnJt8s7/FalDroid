package Test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;

import org.omg.CORBA.PUBLIC_MEMBER;

public class ReConstructSensitiveFile {

	public static String source="/home/fan/lab/Family/file/SourcesSinks/Ouput_CatSinks_v0_9.txt";
	public static String source_bak="/home/fan/lab/Family/file/SourcesSinks/SinkNoCategory.txt";
	public static ArrayList<String> sourceList=new ArrayList<>();
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		readFile();
		writeFile();
	}
	public static void readFile(){
		try {
			File file=new File(source);
			FileReader fReader=new FileReader(file);
			BufferedReader bReader=new BufferedReader(fReader);
			String line="";
			while((line=bReader.readLine())!=null){
				if(!line.contains("NO_CATEGORY")){
					sourceList.add(line);
				}
			}
			bReader.close();
			fReader.close();
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	public static void writeFile(){
		try {
			File file=new File(source_bak);
			FileWriter fWriter=new FileWriter(file);
			BufferedWriter bWriter=new BufferedWriter(fWriter);
			for(int i=0;i<sourceList.size();i++){
				bWriter.write(sourceList.get(i)+"\n");
			}
			
			bWriter.close();
			fWriter.close();
			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	

}
