package cn.edu.cqupt.rubic_business.util;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cn.edu.cqupt.rubic_business.Model.po.ModelPo;
import cn.edu.cqupt.rubic_business.dao.ModelAPIDao;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.xpath.XPath;

import cn.edu.cqupt.rubic_business.Model.po.AlgorithmPo;
import cn.edu.cqupt.rubic_business.Model.po.AttributePo;
import cn.edu.cqupt.rubic_business.Model.po.ParameterPo;
import cn.edu.cqupt.rubic_core.exception.DataException;
import cn.edu.cqupt.rubic_framework.model.DataSet;
import cn.edu.cqupt.rubic_framework.model.Example;
import cn.edu.cqupt.rubic_framework.model.MyStruct;
import cn.edu.cqupt.rubic_framework.model.TextDataSet;
import cn.edu.cqupt.rubic_framework.model.TextExample;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Resource;

/**
 * 
 * @description 处理Model的工具类
 * @author LiuMian
 * @date 2015-12-6 下午3:35:32
 * @version 1.0
 * 
 */
public class ModelUtils {
	@Autowired
	private static ModelAPIDao modelAPIDao;

	public static Map<String, Object> readXML(ByteArrayInputStream input)
			throws JDOMException, IOException {
//		Path filePath = Paths.get("c:","rubic","modelInfo.xml");
//		File xmlFile  = new File(filePath.toUri());
//		OutputStream os = new FileOutputStream(xmlFile);
//
//		int len = -1;
//		byte[] bytes = new byte[512];
//		while((len = input.read(bytes))!= -1){
//			os.write(bytes);
//		}
//		os.close();

		//获得文件对象
		SAXBuilder builder = new SAXBuilder();
		Document doc = builder.build(input);
		Element root = doc.getRootElement();
		
		//保存信息
		Map<String, Object> modelInfo = new HashMap<String, Object>();

		// 获得userId
		int userId = Integer.parseInt(root.getChildText("userId"));
		modelInfo.put("userId", userId);

		// 获得数据Id
		Element datasetIdNode = (Element) XPath.selectSingleNode(root,
				"/model/dataSets/dataSet/dataSetId");
		int dataSetId = Integer.parseInt(datasetIdNode.getText());
		modelInfo.put("dataSetId", dataSetId);
		
		// 获得数据类型
		Element datasetTypeNode = (Element) XPath.selectSingleNode(root,
				"/model/dataSets/dataSet/dataSetType");
		int datasetType = Integer.parseInt(datasetTypeNode.getText());
		modelInfo.put("datasetType", datasetType);

		// 获得算法信息,处理当算法 "/model/algorithms/*"
		modelInfo.put("algorithm",getAlgorithmList(root, "/model/algorithms/*"));

		// 添加数据属性信息 /model/dataSets/dataSet/attributes/*
		modelInfo.put("attributes",getAttributes(root, "/model/dataSets/dataSet/attributes/*"));

		// 获得参数信息 "/model/parameters/*"
		modelInfo.put("parameters", getparameterList(root, "/model/parameters/*"));

		return modelInfo;
	}

	/**
	 * @description:获得算法信息
	 * @author hey
	 * @param node
	 * @param xPath
	 * @return
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public static List<AlgorithmPo> getAlgorithmList(Element node, String xPath)
			throws JDOMException {
		List<Element> algorithmNodePoList = XPath.selectNodes(node, xPath);
		List<AlgorithmPo> algorithmList = new ArrayList<AlgorithmPo>();
		AlgorithmPo algorithm = null;

		for (Element ele : algorithmNodePoList) {
			algorithm = new AlgorithmPo();
			algorithm.setAlgorithm_id(Integer.parseInt(ele.getChildText("algorithmId")));
			algorithm.setPlatform(ele.getChildText("platform"));
			algorithm.setFile_path(ele.getChildText("jarFile"));
			algorithm.setPackage_name(ele.getChildText("runClass"));
			algorithm.setDescription(ele.getChildText("description"));
			algorithmList.add(algorithm);
		}
		return algorithmList;
	}

	/**
	 * @description:获得数据集属性信息
	 * @author hey
	 * @param node
	 * @param xPath
	 * @return
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public static List<AttributePo> getAttributes(Element node, String xPath)
			throws JDOMException {
		// 获得数据属性信息
		List<Element> attrNodeList = XPath.selectNodes(node, xPath);
		List<AttributePo> attrList = new ArrayList<AttributePo>();
		AttributePo attr = null;

		for (Element ele : attrNodeList) {
			attr = new AttributePo();
			attr.setAttribute_name(ele.getChildText("name"));
			attr.setAttribute_type(ele.getChildText("type"));
			attr.setAttribute_sequence(Integer.parseInt(ele.getChildText("order")));
			// 结果列
			String label = ele.getChildText("label");
			if (label != null) {
				Integer labelSequence = Integer.parseInt(label.trim());
				attr.setAttribute_label(labelSequence);
			}

			attrList.add(attr);
		}
		return attrList;
	}
	
	/**
	 * @description:获取参数信息
	 * @author hey
	 * @param node
	 * @param xPath
	 * @return
	 * @throws JDOMException
	 */
	@SuppressWarnings("unchecked")
	public static List<ParameterPo> getparameterList(Element node, String xPath)
			throws JDOMException {
		List<Element> parameterNodeList = XPath.selectNodes(node, xPath);
		List<ParameterPo> parameterList = null;
		if (parameterNodeList.size() != 0) {

			parameterList = new ArrayList<ParameterPo>();

			for (Element ele : parameterNodeList) {

				ParameterPo parameter = new ParameterPo();
				parameter.setParameter_value(ele.getText());
				parameterList.add(parameter);
			}
			return parameterList;
		}
		return null;
	}

	/**
	 * 对前台传来的attributes的值进行排序
	 * 
	 * @param attributes
	 * @return
	 */
	public static List<String> sortAttributes(Map<String, String> attrsUnsort,
			List<AttributePo> attrsTemplate) {

		List<String> sorted = new ArrayList<String>();
		for (AttributePo attr : attrsTemplate) {
			sorted.add(attrsUnsort.get(attr.getAttribute_name()));
		}
		return sorted;
	}

	/**
	 * @description:构造数据集
	 * @author hey(Vigo修改)
	 * @param data
	 * @param attributes
	 * @return
	 */
	public static MyStruct[] creatMyStruct(Map<String, String> data,
			List<AttributePo> attributes, int dataType) {
		
		List<String> attributeNames = new ArrayList<String>();
		for(int i = 0; i < attributes.size(); ++i){
			attributeNames.add(attributes.get(i).getAttribute_name());
		}
		// 先处理甲协议
		MyStruct[] myStructs = new MyStruct[1];
		
		if (dataType == 0) {
			
			DataSet dataSet = new DataSet(attributeNames.toArray(new String[attributeNames.size()]));
			Example example = new Example();
			AttributePo attributePo = null;
			for(int i = 0; i < attributes.size(); ++i){
				attributePo = attributes.get(i); 
				String value = data.get(attributePo.getAttribute_name());
				if (value == null)
					throw new DataException("Your Data is Wrong,Please Check");
				if (attributePo.getAttribute_label() == 1) {
					example.setLabel(value);
				} else {
					example.addAttribute(value);
				}
				dataSet.addExample(example);
			}			
			myStructs[0] = dataSet;
		} else if (dataType == 1){
			
			TextDataSet textDataSet = new TextDataSet(attributeNames.toArray(new String[attributeNames.size()]));
			AttributePo attributePo = null;
			String content = null;
			String label = null;
			for(int i = 0; i < attributes.size(); ++i){
				attributePo = attributes.get(i);
				//!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
				if (attributePo.getAttribute_label() == 1) {
					label = data.get(attributePo.getAttribute_name());
				}else {
					content = data.get(attributePo.getAttribute_name());
				}
			}
				TextExample textExample = new TextExample(content, label);
				textDataSet.addTextExample(textExample);
				System.out.println("textDataSet: " + textDataSet.toString());
				myStructs[0] = textDataSet;
			}

		return myStructs;
	}

	public static List<String> getAttributeNames(List<AttributePo> attributes) {
		List<String> attrNames = new ArrayList<String>();
		for (AttributePo attr : attributes) {
			attrNames.add(attr.getAttribute_name());
		}
		return attrNames;
	}

}
