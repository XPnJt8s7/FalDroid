package Exp.HMRF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/*
 *     隐马尔科夫模型的上层相似图模型
 */

public class TestSimGraph {
			private Set<APKObject> nodeSet=new HashSet<>();
			private Set<APKEdge> edgeSet=new HashSet<>();
			
			public TestSimGraph(){
				
			}
			public void addNode(APKObject apkObject){
				this.nodeSet.add(apkObject);
			}
			public void addEdge(APKEdge edge){
				this.edgeSet.add(edge);
			}
			public void writeSimGraphFile(String filePath){
				try {
					File file=new File(filePath);
					FileWriter fWriter=new FileWriter(file);
					BufferedWriter bWriter=new BufferedWriter(fWriter);
					String lineString="<?xml version='1.0' encoding='UTF-8'?>\n"+
									"<gexf xmlns='qianniao918@qq.com' version='1.2' xmlns:viz='http://www.gexf.net/1.2draft/viz'> "
									+ "xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' "
									+ "xsi:schemaLocation='http://www.gexf.net/1.2draft http://www.gexf.net/1.2draft/gexf.xsd'>\n"
									+ "		<meta lastmodifieddate='2014-9-21'>\n"
									+"			<creator>FanMing</creator>\n"
									+"			<description>An Android APK Method SCN!</description>\n"
									+"			</meta>\n"
									+"			<graph mode='static' defaultedgettype='undirected'>\n"
									+"				<nodes>\n";
					bWriter.write(lineString);
					Iterator<APKObject> nodeIterator=this.nodeSet.iterator();
					while(nodeIterator.hasNext()){
						APKObject apkObject=new APKObject();
						apkObject=nodeIterator.next();
						String apkName=apkObject.getApkName();
						String actureFalName=apkObject.getActualFalName();
						String preFalName=apkObject.getPredictFalNameString();
						String id=apkName+"--"+actureFalName+"--"+preFalName;
						String nodeString="				<node id='"+id+"' label='"+id+"'>\n";
						int r=0; int g=0; int b=0;
						if(apkObject.getNodeType().equals("Test")){  // 节点类型为测试节点
							if(!apkObject.isResult()){    //测试节点分类标签错误
								r=255; g=0; b=0;   //红色
							}
							else{              //  测试节点分类标签正确
								r=0; g=255; b=0;	//绿色
							}
						}
						else if(apkObject.getNodeType().equals("Train")){  // 节点类型为训练节点
							if(!apkObject.isConsistency()){   //训练节点与邻居节点类型不一致
								r=255; g=255; b=0;    //黄色
							}
							else{          //训练节点与邻居节点类型一致
								r=0; g=0; b=255;   //蓝色
							}
						}
						nodeString+="						<attvalues></attvalues><viz:color r='"+r+"' g='"+g
								+"' b='"+b+"'></viz:color>";
						nodeString +="			</node>\n";
						bWriter.write(nodeString);
					}
					lineString="			</nodes>\n";
					bWriter.write(lineString);
					lineString="			<edges>\n";
					bWriter.write(lineString);
					Iterator<APKEdge> edgeIterator=this.edgeSet.iterator();
					int index=0;
					while(edgeIterator.hasNext()){
						APKEdge edge=new APKEdge();
						edge=edgeIterator.next();
						String srcId=edge.getSrcNode().getApkName()+"--"+edge.getSrcNode().getActualFalName()
								+"--"+edge.getSrcNode().getPredictFalNameString();
						String dstId=edge.getDstNode().getApkName()+"--"+edge.getDstNode().getActualFalName()
								+"--"+edge.getDstNode().getPredictFalNameString();
						String weight=String.valueOf(edge.getWeight());
						lineString="<edge id='"+index+"' source='"+srcId+"' target='"+dstId+"' weight='"+"1"+"'/>\n";
						bWriter.write(lineString);
						index++;
					}
					lineString="		</edges>\n";
					lineString +="		</graph>\n";
					lineString +="</gexf>";
					bWriter.write(lineString);
					bWriter.close();
					fWriter.close();
				} catch (Exception e) {
					// TODO: handle exception
					e.printStackTrace();
				}
			}
}
