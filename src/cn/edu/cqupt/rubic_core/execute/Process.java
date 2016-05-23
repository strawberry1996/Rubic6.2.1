package cn.edu.cqupt.rubic_core.execute;

import cn.edu.cqupt.rubic_core.config.Configuration;
import cn.edu.cqupt.rubic_core.runtime.RubicClassLoader;
import cn.edu.cqupt.rubic_framework.algorithm_interface.BasicAdapter;
import cn.edu.cqupt.rubic_framework.algorithm_interface.ErrorInputFormatException;
import cn.edu.cqupt.rubic_framework.algorithm_interface.OperationalData;
import cn.edu.cqupt.rubic_framework.model.MyStruct;
import cn.edu.cqupt.rubic_framework.service_interface.EventListener;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.net.MalformedURLException;

/**
 * <p>
 * Title: Process
 * <p>
 * Description:运行处理类
 * </p>
 * 
 * @author Hey,Liw
 * @data 2015-11-13
 */
public class Process implements BasicAdapter{

	private static final Log LOG = LogFactory.getLog(Process.class);

	private OperationalData operationalData;

	private RubicClassLoader loader;

	/**
	 * @param jarFile
	 *            jar文件路径
	 * @param runClass
	 *            运行主类
	 * @throws MalformedURLException
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	public Process(String jarFile, String runClass)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		System.out.println(jarFile+"  "+runClass);
		loader = new RubicClassLoader(jarFile);
		this.operationalData = (OperationalData) loader.getInstance(runClass);
	}

	/**
	 * @description:运行算法
	 * @author hey
	 * @param data
	 *            运行数据集
	 * @param config
	 *            配置信息
	 * @param parameters
	 *            参数
	 * @return 运算以后的结果集
	 * @throws ErrorInputFormatException
	 * @throws OperationProcessException
	 */
	public MyStruct[] execute(MyStruct[] data, Configuration config,
			double[] parameters) throws ErrorInputFormatException,
			OperationProcessException {
		MyStruct[] resultDataSets = null;

		try {
			resultDataSets = operationalData.run(data, config, parameters);
		} catch (Exception e) {
			throw new OperationProcessException(e);
		}

		//---释放jar资源
		loader.release();

		return resultDataSets;
	}



	/**
	 * @description:生成运行模型
	 * @author hey
	 * @return 运行模型
	 */
	public Object createModel() {

		return this.operationalData.creatModel();
	}

	/**
	 * @description:训练模型
	 * @author hey
	 * @param input
	 *            训练的数据集
	 * @param object
	 *            运行模型
	 * @return 训练以后的模型
	 */
	public Object trainingModel(MyStruct[] input, Object object) {

		return operationalData.trainingModel(input, object);
	}

	/**
	 * @description:使用模型
	 * @author hey
	 * @param input
	 *            输入数据集
	 * @param object
	 *            运行模型
	 * @return 运算以后的结果集
	 */
	public MyStruct[] usingModel(MyStruct[] input, Object object) throws ErrorInputFormatException,
			OperationProcessException{

		MyStruct[] result = null;
		result = this.operationalData.usingModel(input, object);

		//---释放jar资源
		loader.release();

		return result;
	}

	@Override
	public void addActionListener(EventListener listener) {
		this.operationalData.addActionListener(listener);
		
	}

}
