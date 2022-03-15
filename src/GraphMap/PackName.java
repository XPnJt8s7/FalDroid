package GraphMap;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class PackName {
    public int depth;
  //  public Set<String> packNameSet=new HashSet<>();
    public Map<String, Integer> packNameMap=new HashMap<>();
    
	public PackName(int depth, Set<String> ClassNameSet){
		this.depth=depth;
		iniPackName(ClassNameSet);
	}
	public void iniPackName(Set<String> ClassNameSet){
		Iterator<String> iterator=ClassNameSet.iterator();
		while(iterator.hasNext()){
			String ClassName=iterator.next();
			String [] packs=ClassName.split("/");
			if(packs.length>depth){
				String line="";
				for(int i=0;i<depth;i++){
					line+=packs[i]+"/";
				}
				if(packNameMap.containsKey(line)){
					int k=packNameMap.get(line);
					k++;
					packNameMap.remove(line);
					packNameMap.put(line, k);
				}
				else{
					packNameMap.put(line, 1);
				}
			}
		}
	}
}
