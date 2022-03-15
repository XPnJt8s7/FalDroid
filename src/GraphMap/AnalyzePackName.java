package GraphMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;


import APKData.SmaliGraph.MethodNode;
import ConstantVar.ConstantValue;

public class AnalyzePackName {
   public Set<String> ClassNameSet=new HashSet<>();
   public Map<Integer, PackName> packNameMap=new HashMap<>();
   public String resultLine="";
   public AnalyzePackName(){
	   
   }
   public AnalyzePackName(Set<MethodNode> pNodeSet){
	   iniClassNameSet(pNodeSet);
	   iniPackNameMap();
	   getPackNameMapString();
   }
   
   public void iniClassNameSet(Set<MethodNode> pNodeSet){
	   Iterator<MethodNode> nodeIterator=pNodeSet.iterator();
	   while(nodeIterator.hasNext()){
		   MethodNode node=nodeIterator.next();
		   String nodeName=node.getCommonString();
		   int k=nodeName.indexOf(":");
		   String packName=nodeName.substring(0, k);
		   if(!isSystem(packName)){
			   ClassNameSet.add(packName);
		   }
	   }
   }
   public void iniPackNameMap(){
	   int start=ConstantValue.getVar().PackMinDepth;
	   int end=ConstantValue.getVar().PackMaxDepth;
	   for(int i=start;i<end;i++){
		   PackName packName=new PackName(i, ClassNameSet);
		   packNameMap.put(i, packName);
	   }
   }
   public boolean isSystem(String line){
	   boolean isSys=false;
	   if(line.startsWith("android")){
		   isSys=true;
	   }
	   if(line.startsWith("java")){
		   isSys=true;
	   }
	   return isSys;
   }
   public String getPackNameMapString(){  
	   resultLine += "PackNameMap:";
	   Iterator<Integer> mapIterator=packNameMap.keySet().iterator();
	   while(mapIterator.hasNext()){
		   resultLine +="\n";
		   int index=mapIterator.next();
		   resultLine +=" "+index+":\n";
		   Map<String, Integer> tmpMap=new HashMap<>();
		   tmpMap=packNameMap.get(index).packNameMap;
		   Iterator<String> nameIterator=tmpMap.keySet().iterator();
		   while(nameIterator.hasNext()){
			   String packName=nameIterator.next();
			   int t=tmpMap.get(packName);
			   resultLine +="   "+packName+"--"+t+"\n";
		   }
	   }
	   return resultLine;
   }
}
