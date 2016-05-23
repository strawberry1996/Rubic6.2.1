package cn.edu.cqupt.rubic_hadoop.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.edu.cqupt.rubic_framework.service_interface.Builder;

/**
 * 运行Mahout算法配置文件类
 * Created by Vigo on 15/11/30.
 */
public class HadoopConfiguration extends HashMap<String, Object> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private final String dataSetPath;
	
	private final String subPath;
	
	private final double threshold;
	
	private final int numIterators;
	
	private final double[] parameters;
	
	private final int labelSequence;
	
	private final List<Attribute> attributes;
	
	private HadoopConfiguration(HadoopConfigurationBuilder builder) {
		this.dataSetPath = builder.dataSetPath;
		this.subPath = builder.subPath;
		this.threshold = builder.threshold;
		this.numIterators = builder.numIterators;
		this.parameters = builder.parameters;
		this.labelSequence = builder.labelSequence;
		this.attributes = builder.attributes;
	}
	
	public static class HadoopConfigurationBuilder implements Builder<HadoopConfiguration> {

		private final String dataSetPath;
		
		private final String subPath;
		
		private double threshold = 0D;
		
		private int numIterators = 0;
		
		private double[] parameters = null;
		
		private int labelSequence = -1;
		
		private List<Attribute> attributes = new ArrayList<Attribute>();
		
		public HadoopConfigurationBuilder(String dataSetPath, String subPath) {
			this.dataSetPath = dataSetPath;
			this.subPath = subPath;
		}
		
		public HadoopConfigurationBuilder threshold(double threshold) {
			this.threshold = threshold;
			return this;
		}
		
		public HadoopConfigurationBuilder numIterators(int numIterators) {
			this.numIterators = numIterators;
			return this;
		}
		
		public HadoopConfigurationBuilder parameters(double[] parameters) {
			this.parameters = parameters;
			return this;
		}
		
		public HadoopConfigurationBuilder label(int label) {
			this.labelSequence = label;
			return this;
		}
		
		public HadoopConfigurationBuilder attributes(List<Attribute> attributes) {
			this.attributes = attributes;
			return this;
		}
		
		@Override
		public HadoopConfiguration build() {
			return new HadoopConfiguration(this);
		}
		
	}
	
	public String getDataSetPath() {
		return this.dataSetPath;
	}
	
	public int getLabelSequence() {
		return this.labelSequence;
	}
	
	public String getSubPath() {
		return this.subPath;
	}
	
	public double getThreshold() {
		return this.threshold;
	}
	
	public int getNumIterators() {
		return this.numIterators;
	}
	
	public double[] getParameters() {
		return this.parameters;
	}
	
	public List<Attribute> getAttributes() {
		return attributes;
	}
	

	public static void main(String[] args) {
		String dataPath = "hdfs://";
		String subPath = "Rubic/26/mahout";
		
		double[] parameters = new double[]{};
		
		HadoopConfiguration configuration = new HadoopConfigurationBuilder(dataPath, subPath).
				threshold(0.75).numIterators(20).parameters(parameters).build();	
		
		System.out.println(configuration.getDataSetPath() + " " + configuration.getParameters());
		
	}
}
