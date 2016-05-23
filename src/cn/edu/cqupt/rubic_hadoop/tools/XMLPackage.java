package cn.edu.cqupt.rubic_hadoop.tools;

import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Text;

import cn.edu.cqupt.rubic_business.Model.po.AttributePo;


/**
 * XML配置文件构造类
 * Created by Vigo on 15/11/30.
 * Update by He Guangqin
 */
public class XMLPackage {

    //构造XML
	/**
	 * 构造XML对象
	 * @param dataSetSource 源数据集路径
	 * @param subPathDir 工作路径
	 * @param parameters 算法所需参数
	 * @param thresHold 收敛阀值
	 * @param numIterators 迭代次数
	 * @return  Document对象
	 */
	public static Document packageToXML(String dataSetSource,String subPathDir,
			double[] parameters,String platform_string,Double thresHold,
			Integer numIterators,Integer label,List<AttributePo> attrList)
	{
		Element process = new Element("process");//根节点process
		Document configDoc = new Document(process); //构建Document对象
		
//		Element type = new Element("type"); //传输方式，request或者response
//		Text type_text = new Text(type_string);
//		type.addContent(type_text);
//		process.addContent(type);
		
		Element protocol = new Element("protocol");//构造协议
		
//		Element protocol_id = new Element("protocol_id"); //协议编号
//		Text protocol_id_text = new Text(protocol_id_string);
//		protocol_id.addContent(protocol_id_text);
//		protocol.addContent(protocol_id);
		
		Element platform = new Element("platform");  //运行平台hadoop或者java
		Text platform_text = new Text(platform_string);
		platform.addContent(platform_text);
		protocol.addContent(platform);
		
		Element config = new Element("config"); //配置参数
		
		Element path = new Element("path");  //配置路径相关参数
		Element dataSetPath = new Element("dataSetPath");  //源数据路径
		Text t2 = new Text(dataSetSource);
		dataSetPath.addContent(t2);
		
		Element subPath = new Element("subPath");  //工作路径
		Text t1 = new Text(subPathDir);
		subPath.addContent(t1);
		path.addContent(dataSetPath);
		path.addContent(subPath);
		config.addContent(path);
		
		
		Element params = new Element("params");  //参数
		for (double d : parameters) {
			Element param = new Element("param");
			Text td = new Text(""+d);
			param.addContent(td);
			params.addContent(param);
		}
		config.addContent(params);
		
		Element labelElement = new Element("label");
		Text labelText = new Text(String.valueOf(label));
		labelElement.addContent(labelText);
		config.addContent(labelElement);
		
		Element threshold = new Element("threshold");
		Text tt = null;
		if(thresHold!=null){
			tt = new Text(""+thresHold);
		}else{
			tt = new Text("");
		}
		threshold.addContent(tt);
		config.addContent(threshold);
		
		Element numIterators_E = new Element("numIterators");
		Text tn = null;
		if(numIterators!=null){  
			tn = new Text(""+numIterators);
//			numIterators_E.setText(""+numIterators);
		}else{
			tn = new Text(""+10);
//			numIterators_E.setText("");
		}
		//--------------输出构造的XML到文件
		//----------------------
		numIterators_E.addContent(tn);
		config.addContent(numIterators_E);
		protocol.addContent(config);
		
		Element dataSetAttributes = new Element("dataSetAttributes");
		for (AttributePo attributePo : attrList) {
			Element attribute = new Element("attribute");
			Element name = new Element("name");
			Text attr_text = new Text(attributePo.getAttribute_name());
			name.addContent(attr_text);
			Element seq = new Element("sequence");
			Text seq_text = new Text(String.valueOf(attributePo.getAttribute_sequence()));
			seq.addContent(seq_text);
			Element type = new Element("type");
			Text type_text = new Text(attributePo.getAttribute_type());
			type.addContent(type_text);
			Element isLabel = new Element("isLabel");
			Text isLabel_text = new Text(String.valueOf(attributePo.getAttribute_label()));
			isLabel.addContent(isLabel_text);
			attribute.addContent(name);
			attribute.addContent(type);
			attribute.addContent(seq);
			attribute.addContent(isLabel);
			dataSetAttributes.addContent(attribute);
		}
		protocol.addContent(dataSetAttributes);
		
		process.addContent(protocol);
		
//		Format format = Format.getCompactFormat();
//		format.setIndent("");
//		format.setEncoding("UTF-8");
		
//		org.jdom.output.XMLOutputter outputer = new org.jdom.output.XMLOutputter(format);
//		
//		try {
//			outputer.output(configDoc, new FileOutputStream(new File("D://hadoopconfig1.xml")));
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
		
		return configDoc;
	}
	
	
	public static void main(String[] args)  {
		List<AttributePo> attrList = new ArrayList<AttributePo>();
		AttributePo attr = new AttributePo();
		attr.setAttribute_name("x");
		attr.setAttribute_type("num");
		attr.setAttribute_sequence(1);
		attr.setAttribute_label(0);
		attrList.add(attr);
		AttributePo attr1 = new AttributePo();
		attr1.setAttribute_name("y");
		attr1.setAttribute_type("num");
		attr1.setAttribute_sequence(2);
		attr1.setAttribute_label(0);
		attrList.add(attr1);
		AttributePo attr2 = new AttributePo();
		attr2.setAttribute_name("shape");
		attr2.setAttribute_type("num");
		attr2.setAttribute_sequence(2);
		attr2.setAttribute_label(0);
		attrList.add(attr2);
		Document doc= XMLPackage.packageToXML("/Rubic/62/iris", "/Rubic/result/221", new double[]{3.0},"hadoop", 0.1, Integer.valueOf(100),Integer.valueOf(3),attrList);
		XMLParser.parseXML(doc);
		System.out.println("ok ");
		
	}

}
