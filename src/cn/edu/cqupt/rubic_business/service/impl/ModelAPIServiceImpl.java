package cn.edu.cqupt.rubic_business.service.impl;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import cn.edu.cqupt.net.handle.TransFileHandle;
import cn.edu.cqupt.rubic_business.Model.po.UserPo;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.cqupt.rubic_business.Model.po.AlgorithmPo;
import cn.edu.cqupt.rubic_business.Model.po.AttributePo;
import cn.edu.cqupt.rubic_business.Model.po.ModelPo;
import cn.edu.cqupt.rubic_business.dao.ModelAPIDao;
import cn.edu.cqupt.rubic_business.service.ModelAPIService;
import cn.edu.cqupt.rubic_business.util.APIException;
import cn.edu.cqupt.rubic_business.util.ModelUtils;
import cn.edu.cqupt.rubic_core.config.Configuration;
import cn.edu.cqupt.rubic_core.execute.OperationListener;
import cn.edu.cqupt.rubic_core.execute.Process;

import cn.edu.cqupt.rubic_framework.model.MyStruct;
import cn.edu.cqupt.rubic_framework.model.ResultDataSet;
import cn.edu.cqupt.rubic_framework.model.ResultExample;
import cn.edu.cqupt.rubic_framework.model.ResultTextDataSet;
import cn.edu.cqupt.rubic_framework.model.ResultTextExample;

import com.alibaba.fastjson.JSONObject;

/**
 * modelAPI服务类
 * Created by Vigo on 2016/05/03.
 */

@Service("modelAPIService")
public class ModelAPIServiceImpl implements ModelAPIService {

	private ModelAPIDao modelAPIDao;
	@Autowired
	private ModelServiceImpl modelService;

	@Resource(name="modelAPIDao")
	public void setModelAPIDao(ModelAPIDao modelAPIDao) {
		this.modelAPIDao = modelAPIDao;
	}

	@SuppressWarnings("unchecked")
	@Override
	public JSONObject callModel(int modelId, Map<String, String> parameters, int userid) throws APIException,Exception {

		ModelPo model = modelAPIDao.findModelByAPIId(modelId);
		
		//model信息
		Map<String,Object> modelInfo = getModelInfo(model);
//		Iterator<String> iter = modelInfo.keySet().iterator();
//		while (iter.hasNext()) {
//		Object key = iter.next();
//		Object val = modelInfo.get(key);
//		System.out.println(key + ", " + val);
//		}
		List<AttributePo> attrs = (List<AttributePo>) modelInfo.get("attributes");
		
		//校验参数，抛出APIException
		verifiParameters(attrs, parameters);

		//暂时写死。。。。。获得算法
		List<AlgorithmPo>algorithmList =(List<AlgorithmPo>) modelInfo.get("algorithm");
		AlgorithmPo algorithm = algorithmList.get(0);

		/**
		 * 从文件系统中取算法jar
		 */
//		int userId = (Integer) modelInfo.get("userId");
		UserPo userPo = new UserPo();
		userPo.setUser_id(userid);
		Configuration config = new Configuration(userPo);

		String jarFile = getJarPath(algorithm, config);

		String runClass = algorithm.getPackage_name();

		Process process = new Process(jarFile, runClass);
		//添加监听器
		process.addActionListener(new OperationListener());

		//使用模型或训练模型
		byte[] bytes = model.getObject();
		Object object = modelService.getObjectFromByte(bytes);
		//直接抛出异常信息，让controller统一处理

		//根据数据类型构造数据
		int dataType = Integer.parseInt(modelInfo.get("datasetType").toString());

		MyStruct[] results = process.usingModel(ModelUtils.creatMyStruct(parameters,attrs, dataType),object);

		
		Map<String,String> map = parseResult(results, attrs, dataType);
		
		JSONObject resultMsg = new JSONObject();
		resultMsg.putAll(map);

        // 删除从文件系统中取得的文件
        deleteAlgorithmFile(config.getTMP_PATH());
//        System.out.println("tmpPath:----->" + config.getTMP_PATH());

		return resultMsg;
	}

	public void verifiParameters(List<AttributePo> attrs, Map<String, String> parameters) throws Exception {
		
		if(parameters.containsKey("apiid"))
			parameters.remove("apiid");
		if(parameters.containsKey("rettype"))
			parameters.remove("rettype");
		if(parameters.containsKey("apikey"))
			parameters.remove("apikey");
		if(parameters.containsKey("userid"))
			parameters.remove("userid");

		//比较个数
		if(attrs.size() != parameters.size())
			throw new APIException("参数个数不匹配");
		
		//比对名称
		for(AttributePo attr:attrs){
			if(!parameters.containsKey(attr.getAttribute_name()))
				throw new APIException("参数名称不匹配");
		}
	}

	/**
	 * 分析运行结果,甲类协议只有一条数据
	 * @param result
	 * @param attrList
	 * @return
	 */
	private Map<String,String> parseResult(MyStruct[] results,List<AttributePo>attrs, int type){
		Map<String,String>resultMap=new HashMap<String, String>();
		//!!!!!!!!!!!!!!!!!此处暂时写死，results不止一个!!!!!!!!!!!!!!
		if (type == 0){
			//数值型
			ResultDataSet resultDateSet = (ResultDataSet) results[0];
			ResultExample resultExample = (ResultExample) resultDateSet.getExamples().get(0);
			if (resultExample != null) {
				String newLabel = resultExample.getNewLabel();
				for (int i = 0; i < resultExample.size(); i++) {
					if (resultExample.get(i) != null) {
						resultMap.put(attrs.get(i).getAttribute_name(), resultExample.get(i));
			//			System.out.println("resultExample: " + resultExample.get(i));
					}
				}
				resultMap.put("result", newLabel);
			}else {
				resultMap.put("errmsg", "no reslut data");
			}

		} else if (type == 1){
			//文本型
			ResultTextDataSet resultTextDataSet = (ResultTextDataSet) results[0];
		//	System.out.println("resultTextDataSet: " + resultTextDataSet.toString());
			ResultTextExample resultTextExample = (ResultTextExample) resultTextDataSet.getResult().get(0);
			if (resultTextExample != null) {
				String newLabel = resultTextExample.getNewLabel();
		//		System.out.println("newLabel: " + newLabel);
				for (int i = 0; i < attrs.size(); i++) {
					//!!!!!!!!!!!!!!!!!!!!!
					if (attrs.get(i).getAttribute_label() == 1) {
						resultMap.put(attrs.get(i).getAttribute_name(), resultTextExample.getLabel());
			//			System.out.println("attrList.get(1): " + attrs.get(i).getAttribute_name() + resultTextExample.getLabel());
					}else {
						resultMap.put(attrs.get(i).getAttribute_name(), resultTextExample.getContent());
			//			System.out.println("attrList.get(1): " + attrs.get(i).getAttribute_name() + resultTextExample.getContent());
					}
				}
				resultMap.put("result", newLabel);
			} else {
				resultMap.put("errmsg", "no reslut data");
			}
		}
		
//		Iterator<String> iter = resultMap.keySet().iterator();
//		while (iter.hasNext()) {
//		Object key = iter.next();
//		Object val = resultMap.get(key);
//		System.out.println(key + ", " + val);
//		}
//		
		return resultMap;
	}

	/**
	 * @description:从model中获取xml，然后解析xml中的数据
	 * @author hey
	 * @param model
	 * @return
	 * @throws IOException
	 * @throws JDOMException
	 */
	public Map<String, Object> getModelInfo(ModelPo model) throws JDOMException,IOException {
		ByteArrayInputStream input = new ByteArrayInputStream(model.getXmlInfo());	
		return ModelUtils.readXML(input);

	}

	/**
	 * 获取jar包路径
	 * @param algorithmPo
	 * @return
	 */
	public String getJarPath(AlgorithmPo algorithmPo, Configuration config){
		TransFileHandle transFileHandle = new TransFileHandle();
		String jarPath = transFileHandle.getFileFromFileSystem(algorithmPo.getFile_path(), config);
		return jarPath;
	}

    private void deleteAlgorithmFile(String tmp_path){
        //删除从文件系统中取得的文件
        TransFileHandle transFileHandle = new TransFileHandle();
        transFileHandle.deleteTmpFile(tmp_path);
 //       System.out.println("tmpPath:----->" + tmp_path);
    }

}
