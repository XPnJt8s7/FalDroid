package GED.GEDofGraphs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes.Name;


public class Node {

	private int Num;
	private String Name;
	private ArrayList<Node> Parent = new ArrayList<Node>();
	private ArrayList<Node> Child = new ArrayList<Node>();
	private Map<Integer, Integer> inMap = new HashMap<Integer, Integer>();
	private Map<Integer, Integer> outMap = new HashMap<Integer, Integer>();
	
	public int getNum(){
		return this.Num;
	}
	
	public void setNum(int Num){
		this.Num=Num;
	}
	
	public String getName(){
		return this.Name;
	}
	
	public void setName(String Name){
		this.Name=Name;
	}
	
	public ArrayList<Node> getParent(){
		return this.Parent;
	}
	
	public void setParent(Node Parent){
		this.Parent.add(Parent);
	}
	
	public ArrayList<Node> getChild(){
		return this.Child;
	}
	
	public void setChild(Node Child){
		this.Child.add(Child);
	}
	
	public Map<Integer, Integer> getinMap(){
		return this.inMap;
	}
	
	public void setinMap(int in, int weight){
		this.inMap.put(in, weight);
	}
	
	public Map<Integer, Integer> getoutMap(){
		return this.outMap;
	}
	
	public void setoutMap(int out, int weight){
		this.outMap.put(out, weight);
	}
}
