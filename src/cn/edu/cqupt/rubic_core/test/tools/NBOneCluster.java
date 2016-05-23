package cn.edu.cqupt.rubic_core.test.tools;

public class NBOneCluster {

	/**
	 * @param args
	 * @author LiuYa
	 * 每个类的统计数据
	 */
	String label;//类别
	int labelCount;//该类别的个数
	int[] attrCount;//改类别属性的统计个数
	public NBOneCluster(int num,String label){
		this.label=label;
		this.attrCount=new int[2*num];
	}
	public int[] addAttrCount(int index){//累加index位置的统计数
		attrCount[index]++;
		return attrCount;
	}
	public int addLabelCount(){
		this.labelCount++;
		return labelCount;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	public int getLabelCount() {
		return labelCount;
	}
	public void setLabelCount(int labelCount) {
		this.labelCount = labelCount;
	}
	public int[] getAttrCount() {
		return attrCount;
	}
	public void setAttrCount(int[] attrCount) {
		this.attrCount = attrCount;
	}
	public String toString(){
		String string="[ "+label+" , "+labelCount+" , ";
		for (int i = 0; i < attrCount.length-1; i++) {
			string=string+attrCount[i]+" , ";
		}
		string=string+attrCount[attrCount.length-1]+" ]";
		return string;
	}
	public static void main(String[] args) {
		NBOneCluster oneCluster=new NBOneCluster(2,"ded");
		oneCluster.setLabel("dd");
		int[] attrCount={1,3,2,4};
		oneCluster.setAttrCount(attrCount);
		oneCluster.addAttrCount(0);
		oneCluster.addAttrCount(0);
		oneCluster.addAttrCount(3);
		System.out.println(oneCluster.getAttrCount()[0]);
		System.out.println(oneCluster.getAttrCount()[3]);

	}

}
