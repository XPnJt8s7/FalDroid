package Exp.HMRF;

/*
 *     相似图模型中的边对象，复写了hashcode函数以及equal函数
 */
public class APKEdge {
			private APKObject srcNode=new APKObject();
			private APKObject dstNode=new APKObject();
			private double weight=0.0D;
			public APKEdge(){
				
			}
			@Override
			public int hashCode() {
				// TODO Auto-generated method stub
				int srcHash=srcNode.getApkName().hashCode();
				int dstHash=dstNode.getApkName().hashCode();
				return (srcHash+dstHash);
			}

			@Override
			public boolean equals(Object obj) {
				// TODO Auto-generated method stub
				APKEdge dstEdge=new APKEdge();
				dstEdge=(APKEdge)obj;
				int k1=this.hashCode();
				int k2=dstEdge.hashCode();
				return (k1==k2);
			}

			public APKObject getSrcNode() {
				return srcNode;
			}

			public void setSrcNode(APKObject srcNode) {
				this.srcNode = srcNode;
			}

			public APKObject getDstNode() {
				return dstNode;
			}

			public void setDstNode(APKObject dstNode) {
				this.dstNode = dstNode;
			}
			public double getWeight() {
				return weight;
			}
			public void setWeight(double weight) {
				this.weight = weight;
			}
			
}
