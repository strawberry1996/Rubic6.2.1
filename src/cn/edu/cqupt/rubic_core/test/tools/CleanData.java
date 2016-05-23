package cn.edu.cqupt.rubic_core.test.tools;

import cn.edu.cqupt.rubic_framework.model.DataSet;
import cn.edu.cqupt.rubic_framework.model.Example;


public class CleanData {

	/**
	 * @param args
	 * @author LiuYa
	 * 主要是对数据清洗，实现数据归一化等功能
	 */
	private static boolean FLAG=false;
	/**数据归一化
	 * */
	public static DataSet NormaliztionData(DataSet dataSet) {
		DataSet newDataSet =new DataSet(dataSet.getAttributeLabels());
		int attrNum=dataSet.getExamples().get(0).getAttribute().length;
		double[][] minmax=new double[2][attrNum];//存放每列属性的最大最小值，第一行为最小值，第二行为最大值
		for (int i = 0; i < dataSet.getSize(); i++) {
			double[] attrsdouble=dataSet.getExamples().get(i).getAttributeInDouble();
			for (int j = 0; j < attrNum; j++) {
				if (i==0) {
					minmax[0][j]=attrsdouble[j];
					minmax[1][j]=attrsdouble[j];
				} else {
					if (attrsdouble[j]>minmax[1][j]) {
						minmax[1][j]=attrsdouble[j];
					} 
					if (attrsdouble[j]<minmax[0][j]) {
						minmax[0][j]=attrsdouble[j];
					}
				}
			}
		}
		MyPrient.prient(minmax,FLAG);	
		//更换数据，进行归一化
		for (int i = 0; i < dataSet.getSize(); i++) {
			Example newExample=new Example();
			Example oldExample=dataSet.getExamples().get(i);
			newExample.setLabel(oldExample.getLabel());
			double[] attrsdouble=oldExample.getAttributeInDouble();
			for (int j = 0; j < attrsdouble.length; j++) {
				double attr=((attrsdouble[j]-minmax[0][j])/(minmax[1][j]-minmax[0][j]));
				newExample.addAttribute(attr+"");
			}
			newDataSet.addExample(newExample);
		}

		return newDataSet;
	}
	/**
	 * 数据处理为0和1
	 * */
	public static DataSet Change0and1Data(DataSet dataSet) {
		DataSet newDataSet =new DataSet(dataSet.getAttributeLabels());
		for (int i = 0; i < dataSet.getSize(); i++) {
			Example newExample=new Example();
			Example oldExample=dataSet.getExamples().get(i);
			newExample.setLabel(oldExample.getLabel());
			double[] attrsdouble=oldExample.getAttributeInDouble();
			for (int j = 0; j < attrsdouble.length; j++) {
				double attr=0;
				if (attrsdouble[j]>0.5) {
					attr=1;
				} 
				newExample.addAttribute(attr+"");
			}
			newDataSet.addExample(newExample);
		}

		return newDataSet;
	}
	public static void main(String[] args) {
//		String path = "dataset/iris.data";
//		DataFactory factory = new FileDataFactory(path);
//		DataSet dataSet = factory.getData("iris");
//		CleanData cleanData=new CleanData();
//		DataSet newDataSet=cleanData.NormaliztionData(dataSet);
//		System.out.println(newDataSet.getExamples().get(0).toString());

	}

}
