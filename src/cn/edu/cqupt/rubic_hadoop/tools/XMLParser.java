package cn.edu.cqupt.rubic_hadoop.tools;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;

import cn.edu.cqupt.rubic_hadoop.config.Attribute;
import cn.edu.cqupt.rubic_hadoop.config.HadoopConfiguration;

/**
 * XML文件解析类
 * Created by Vigo on 15/12/3.
 * 
 */
public class XMLParser {
    //解析XML
	
	public static HadoopConfiguration parseXML(Document document)
	{
		
		Element process = document.getRootElement();
		
		Element protocol = process.getChild("protocol");
		
		
		Element platform = protocol.getChild("platform");
		@SuppressWarnings("unused")
		String platform_string = platform.getValue();
		
		Element config = protocol.getChild("config");
		
		Element path = config.getChild("path");
		
		Element dataSetPath = path.getChild("dataSetPath");
		String dataSetPathString = dataSetPath.getValue();
		
		Element subPath = path.getChild("subPath");
		String subPathString = subPath.getValue();
		
		Element params = config.getChild("params");
		@SuppressWarnings("unchecked")
		List<Element> list = params.getChildren();
		double[] parameters = new double[list.size()];
		int i=0;
		for (Element element : list) {
			String value = element.getValue();
			double param = Double.parseDouble(value);
			parameters[i] = param;
			System.out.println(parameters[i]);
			i++;
		}
		
		Element labelElement = config.getChild("label");
		Integer label = Integer.valueOf(labelElement.getValue());
		
		
		Element threshold = config.getChild("threshold");
		String thresholdString = threshold.getValue();
		double d;
		if(thresholdString!=""&&thresholdString.length()>=1){
			d = Double.parseDouble(thresholdString);
		}else{
			d = 0.0;
		}
		
		Element numIterators = config.getChild("numIterators");
		String numIteratorsString = numIterators.getValue();
		int num;
		if(thresholdString!=""&&thresholdString.length()>=1){
			num = Integer.parseInt(numIteratorsString);
		}else{
			num = 10;
		}
		
		
		Element dataSetAttributes = protocol.getChild("dataSetAttributes");
//		List<Map<String, String>> attributes = new ArrayList<Map<String,String>>();
		List<Attribute> attributes = new ArrayList<Attribute>();
		@SuppressWarnings("unchecked")
		List<Element> attrs = dataSetAttributes.getChildren();
		for (Element element : attrs) {
//			Map<String, String> attribute = new HashMap<String, String>();
			Attribute attribute = new Attribute();
//			attribute.put(element.getName(), element.getText());
			@SuppressWarnings("unchecked")
			List<Element> eles = element.getChildren();
			for (Element element2 : eles) {
				System.out.println(element2.getName()+" ------->  "+element2.getText());
//				attribute.put(element2.getName(), element2.getText());
				if(element2.getName().equalsIgnoreCase("name")){
					attribute.setName(element2.getText());
				}else if(element2.getName().equalsIgnoreCase("type")){
					String type = element2.getText();
					if("文本".equals(type)){
						type = "word";
					}else if("数值".equals(type)){
						type = "numeric";
					}
					attribute.setType(type);
				}else if(element2.getName().equalsIgnoreCase("sequence")){
					Integer sequence = Integer.valueOf(element2.getText());
					attribute.setSequence(sequence);
				}else{
					boolean isLabel;
					if(element2.getText().equals("0")){
						isLabel = false;
					}else{
						isLabel = true;
					}
					attribute.setLabel(isLabel);
				}
			}
			attributes.add(attribute);
		}
		HadoopConfiguration conf = new HadoopConfiguration.HadoopConfigurationBuilder(dataSetPathString, subPathString)
		.threshold(d).numIterators(num).parameters(parameters).label(label).attributes(attributes).build();
		
		return conf;
	}
	
}
