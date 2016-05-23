package cn.edu.cqupt.rubic_core.test.k_means;

import java.util.HashMap;
import java.util.Map.Entry;
import java.util.Set;

public class K_Cluster {

	/**
	 * @param args
	 * @author LiuYa Kmeans 每个聚类形成的类对象
	 */
	int clusterLabel;
	//String label;
	HashMap<String, Integer> labelnum;
	public HashMap<String, Integer> getLabelnum() {
		return labelnum;
	}
	public void setLabelnum(HashMap<String, Integer> labelnum) {
		this.labelnum = labelnum;
	}
	public void newLabelnum() {
		HashMap<String, Integer> labelnum1=new HashMap<String, Integer>();
		setLabelnum(labelnum1);
	}
	public void addLabel(String label){
		HashMap<String, Integer> labelnum1=getLabelnum();
		if (labelnum1.isEmpty()) {
			labelnum1=new HashMap<String, Integer>();
		}
		if (labelnum1.containsKey(label)) {
			int num=labelnum1.get(label)+1;
			labelnum1.put(label, num);
		} else {
			labelnum1.put(label, 1);
		}
		setLabelnum(labelnum1);
		
	}
	/**
	 * 返回该类型数据的大多数标签为该类标签
	 * */
	public String getLabel() {
		String label=null;
		int num=0;
		HashMap<String, Integer> labelnum1=getLabelnum();
		for (Entry<String,Integer> e : labelnum1.entrySet()) {
			if (num<e.getValue()) {
				label=e.getKey();
			    num=e.getValue();
			}
		}
		return label;
	}


	int clusterCount;
	double[] allAttr;
	double[] averageAttr;
/**
 * 返回该类的平均位置为新的质点位置
 * 
 * */
	public double[] getAverageAttr() {
		 double[] average=new double[allAttr.length];
		for (int i = 0; i < allAttr.length; i++) {
			average[i] = allAttr[i] / (double) clusterCount;
		}
		return average;
	}

	public void setAverageAttr(double[] averageAttr) {
		this.averageAttr = averageAttr;
	}

	public K_Cluster(int name, double[] attr) {
		this.clusterLabel = name;
		double[] attrs=new double[attr.length];
		newLabelnum();
		setAllAttr(attrs);
	}

	public void addOne(double[] attrs) {
		clusterCount++;
		double[] all=new double[attrs.length];
		for (int i = 0; i < attrs.length; i++) {
			all[i] = getAllAttr()[i] + attrs[i];
		}
		setAllAttr(all);

	}

	public int getClusterName() {
		return clusterLabel;
	}

	public void setClusterName(int clusterName) {
		this.clusterLabel = clusterName;
	}

	public int getClusterCount() {
		return clusterCount;
	}

	public void setClusterCount(int clusterCount) {
		this.clusterCount = clusterCount;
	}

	public double[] getAllAttr() {
		return allAttr;
	}

	public void setAllAttr(double[] allAttr) {
		this.allAttr = allAttr;
	}

	public static void main(String[] args) {

		double[] Attr = { 1, 2, 3, 4, 5 };
		double[] Attr1 = { 2, 2, 3, 4, 5 };
		double[] Attr2 = { 1, 2, 3, 4, 5 };
		double[] Attr3 = { 1, 3, 3, 4, 5 };
		K_Cluster k_Cluster = new K_Cluster(1, Attr);
		k_Cluster.addOne(Attr);
		k_Cluster.addOne(Attr1);
		k_Cluster.addOne(Attr3);
		k_Cluster.addLabel("qq");
		k_Cluster.addLabel("qq");
		k_Cluster.addLabel("qq");
		k_Cluster.addLabel("qq1");
		k_Cluster.addLabel("qq1");
		k_Cluster.addLabel("qq1");
		k_Cluster.addLabel("qq1");
		k_Cluster.addLabel("qq");
		k_Cluster.addLabel("qq");
		System.out.println(k_Cluster.getLabel());
		// System.out.println(k_Cluster.getClusterCount());
		//System.out.println(k_Cluster.getAverageAttr()[0]);
	//MyPrient.prient(k_Cluster.getAverageAttr(),true);

	}

}
