package cn.edu.cqupt.rubic_core.test.k_means;

import java.util.HashMap;

import cn.edu.cqupt.rubic_core.test.tools.CleanData;
import cn.edu.cqupt.rubic_core.test.tools.Distance;
import cn.edu.cqupt.rubic_core.test.tools.VisulEvent;
import cn.edu.cqupt.rubic_framework.algorithm_interface.BasicAdapter;
import cn.edu.cqupt.rubic_framework.algorithm_interface.ErrorInputFormatException;
import cn.edu.cqupt.rubic_framework.algorithm_interface.OperationalData;
import cn.edu.cqupt.rubic_framework.model.DataSet;
import cn.edu.cqupt.rubic_framework.model.MyStruct;
import cn.edu.cqupt.rubic_framework.model.ResultDataSet;
import cn.edu.cqupt.rubic_framework.model.ResultExample;
import cn.edu.cqupt.rubic_framework.service_interface.EventListener;

/**
 * @author LiuYa
 * Kmeans聚类算法
 * */
public class K_means implements OperationalData, BasicAdapter {
	
	private static boolean FLAG=true;
	EventListener listener;
	private int K=3;
	
	public int getK() {
		return K;
	}


	public void setK(int k) {
		this.K = k;
	}


	public void addActionListener(EventListener listener) {
		this.listener=listener;

	}
	
	public MyStruct[] run(MyStruct[] input, Object configuration,
			double... parameters) throws ErrorInputFormatException {
		
		DataSet dataSet = CleanData.NormaliztionData((DataSet) input[0]);
		
		
		String[] labelsNew=new String[dataSet.getSize()]; 
		String[] labelsOld=new String[dataSet.getSize()];
	//	int n=dataSet.getExamples().get(0).getAttributeInDouble().length;//属性个数
		//setK(K);//分为多少类，即初始化K
		//初始化中心点,中心点的标签
		HashMap<Integer, double[]> centerPoint=new HashMap<Integer, double[]>();
		HashMap<Integer, String> centerLabel=new HashMap<Integer, String>();
		for (int i = 0; i < K; i++) {
			centerPoint.put(i, dataSet.getExamples().get(i)
					.getAttributeInDouble());
			centerLabel.put(i, dataSet.getExamples().get(i).getLabel());
		}
		
		HashMap<Integer, Integer> listOldCluster=new HashMap<Integer, Integer>();//k为数据样本的编号，v为分为类型的编号
		HashMap<Integer, Integer> listNewCluster=new HashMap<Integer, Integer>();
		boolean flag=true;
		int xuhuan=0;
		while (flag) {
			flag=false;
			// 初始化簇
			HashMap<Integer, K_Cluster> clusters = new HashMap<Integer, K_Cluster>();
			for (int i = 0; i < K; i++) {
				K_Cluster cluster = new K_Cluster(i, dataSet.getExamples()
						.get(i).getAttributeInDouble());
				clusters.put(i, cluster);
			}
			for (int i = 0; i < dataSet.getSize(); i++) {
				if (xuhuan==0) {
					labelsOld[i]=dataSet.getExamples().get(i).getLabel();
				}
				//计算与每个质点的距离
				int labelNum=minDistanceLable(centerPoint,dataSet.getExamples()
						.get(i).getAttributeInDouble());
				labelsNew[i]=centerLabel.get(labelNum);//中心点的标签为新划分标签
			//	labelsNew[i]=labelNum+"";
				listNewCluster.put(i,labelNum );
				//比较簇聚类是否发生改变
				if (listNewCluster.get(i)!=listOldCluster.get(i)) {
					flag=true;
				}
				//改变listOldCluster的值
				listOldCluster.put(i, labelNum);
				//添加簇成员
				K_Cluster cluster=clusters.get(labelNum);
				cluster.addOne(dataSet.getExamples().get(i).getAttributeInDouble());
				//添加新成员标签
				cluster.addLabel(dataSet.getExamples().get(i).getLabel());
				clusters.put(labelNum, cluster);
			}
			
			xuhuan++;
			
			//跟新质心
			for (int i = 0; i < K; i++) {
				centerPoint.put(i, clusters.get(i).getAverageAttr());//跟新中心点位置
				centerLabel.put(i, clusters.get(i).getLabel());//跟新中心点标签
			}
		}
		
		VisulEvent e = new VisulEvent();
		e.setContent("OK K_means");
		e.print();
		listener.stuteChanged(e);
		
		ResultDataSet resultDataSet=new ResultDataSet(dataSet.getAttributeLabels());
		for (int i = 0; i < dataSet.getSize(); i++) {
			ResultExample resultExample=new ResultExample();
			resultExample.addAllAttributes(dataSet.getExamples().get(i).getAttribute());
			resultExample.setLabel(dataSet.getExamples().get(i).getLabel());
			resultExample.setNewLabel(labelsNew[i]);
			resultDataSet.addExample(resultExample);
		}
		MyStruct[] resultMyStructs=new MyStruct[1];
		resultMyStructs[0]=(MyStruct)resultDataSet;
	
		return resultMyStructs;
	}

	@Override
	public Object creatModel() {
		return null;
	}

	@Override
	public Object trainingModel(MyStruct[] input, Object object) {
		return null;
	}

	@Override
	public MyStruct[] usingModel(MyStruct[] input, Object object) {
		return new MyStruct[0];
	}

	public int minDistanceLable(HashMap<Integer, double[]> centerPoint,double[] attr){
		int label=0;
		double mindis=Distance.euclideamDistance(centerPoint.get(0), attr);
		for (int i = 1; i < K; i++) {
			if (mindis>Distance.euclideamDistance(centerPoint.get(i), attr)) {
				label=i;
			}
		}
		return label;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {


	}

}
