package cn.edu.cqupt.rubic_core.protocol;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;


/**
 * 构造解析协议No.20160111
 * 
 * @author Felix
 * 
 */
public class HadoopProtocol {

	/**
	 * 解析xml，获得运行参数
	 * 
	 * @param content
	 * @return
	 */
	public Map<String, Object> parserRequest(String content) {
		try {
		
			StringReader xmlReader = new StringReader(content);
			SAXBuilder builder = new SAXBuilder();

			Document document = builder.build(xmlReader);

			Map<String, Object> protocol = new HashMap<String, Object>();
			Element root = document.getRootElement();
			
			Element node_protocol = root.getChild("protocol");
			
			//解析user_id
			Element user_id = node_protocol.getChild("user_id");
			protocol.put("userId", new Integer(user_id.getValue()));
//			System.out.println( new Integer(user_id.getValue()));
			
			//解析platform
			Element platform = node_protocol.getChild("platform");
			protocol.put("platform", platform.getValue());
//			System.out.println( plantform.getValue());
			
			//解析data_id_s
			Element data_id_s = node_protocol.getChild("data_id_s");
			List<Element> datas = data_id_s.getChildren();
			int[] data_s_int = new int[datas.size()]; 
			int i = 0;
			for (Element data : datas){
				data_s_int[i] = new Integer(data.getValue());
//				System.out.println( data_s_int[i]);
				i++;
			}
			protocol.put("dataIds", data_s_int);
				
			//解析algorithm_id
			Element algorithm_id = node_protocol.getChild("algorithm_id");
			protocol.put("algorithmId", new Integer(algorithm_id.getValue()));
//			System.out.println( new Integer(algorithm_id.getValue()));
			
			//解析parameter_s
			Element parameter_s = node_protocol.getChild("parameter_s");
			List<Element> parameterss = parameter_s.getChildren();
			double[] parameter_s_double = new double[parameterss.size()]; 
			int j= 0;
			for (Element data : parameterss){
				parameter_s_double[j] = new Double(data.getValue());
//				System.out.println( parameter_s_double[j]);
				j++;
			}
			protocol.put("parameters", parameter_s_double);
			
			//解析config
			Element config_element = node_protocol.getChild("config");
			List<Element> config_childs = config_element.getChildren();
			
			for (Element element : config_childs) {
				String element_name = element.getName();
				String element_value = element.getValue();
				protocol.put(element_name, element_value);
			}
			
			
		
		return protocol;
		} catch (JDOMException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
		
	}

//	/**
//	 * 不加线程池版本
//	 * @param responseMap
//	 * @return
//	 */
//	public String creatResponse(Map<String, Object> responseMap){
//		Document doc=null;
//		Element root=null;
//		String string="";
//		try {
//			//构造根节点
//			root=new Element("process");
//			doc=new Document(root);
//			//构造type节点，并放入根节点
//			Element typeElement=new Element("type");
//			typeElement.setText((String) responseMap.get("type"));
//			root.addContent(typeElement);
//			//构造result节点，并放入根节点
//			Element resultElement=new Element("result");
//			Element codeElement=new Element("code");
//			codeElement.setText(""+ responseMap.get("code"));
//			Element reasonElement=new Element("reason");
//			reasonElement.setText((String) responseMap.get("reason"));
//			Element contentElement=new Element("content");
//			contentElement.setText(""+ responseMap.get("content"));
//			resultElement.addContent(codeElement);
//			resultElement.addContent(reasonElement);
//			resultElement.addContent(contentElement);
//			root.addContent(resultElement);
//
//			//转换为xml,并返回字符串型
//			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
//			StringWriter writer = new StringWriter();
//			XMLOut.output(doc, writer);
//			string=writer.toString();
//	        System.out.println(string);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return string;
//	}

	/**
	 * 添加线程池返回协议
	 * @return
	 */
	public String creatResponse(){
		Document doc=null;
		Element root=null;
		String string="";
		try {
			//构造根节点
			root=new Element("process");
			doc=new Document(root);
			//构造type节点，并放入根节点
			Element typeElement=new Element("type");
			typeElement.setText("response");
			root.addContent(typeElement);
			//构造result节点，并放入根节点
			Element resultElement=new Element("result");
			Element codeElement=new Element("code");
			//---------------此处默认提交成功，日后需要修改--------
			codeElement.setText("1");
			Element reasonElement=new Element("reason");
			reasonElement.setText("");
			Element contentElement=new Element("content");
			contentElement.setText("运行提交成功");
			//----------------end------------------------------
			resultElement.addContent(codeElement);
			resultElement.addContent(reasonElement);
			resultElement.addContent(contentElement);
			root.addContent(resultElement);

			//转换为xml,并返回字符串型
			XMLOutputter XMLOut = new XMLOutputter(FormatXML());
			StringWriter writer = new StringWriter();
			XMLOut.output(doc, writer);
			string=writer.toString();
			System.out.println(string);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return string;
	}


	//设置XML文件格式
		public Format FormatXML(){  
	        Format format = Format.getCompactFormat();  
	        format.setEncoding("utf-8");  
	        format.setIndent(" ");  
	        return format;  
	    } 
	public static void main(String[] args) {
//		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
//				"<process>\r\n" + 
//				"        <type>request</type>\r\n" + 
//				"        <!--1表示协议类型是运行协议（对应operationaldata里面的run方法）-->\r\n" + 
//				"        <protocol>\r\n" + 
//				"            <protocol_id>1</protocol_id>\r\n" + 
//				"            <user_id>129</user_id>\r\n" + 
//				"            <plantform>java</plantform>\r\n" + 
//				"            <!--需要的数据编号-->\r\n" + 
//				"            <data_id_s>\r\n" + 
//				"                <data_id>12</data_id>\r\n" + 
//				"                <data_id>16</data_id>\r\n" + 
//				"            </data_id_s>\r\n" + 
//				"            <algorithm_id>8</algorithm_id>\r\n" + 
//				"            <parameter_s>\r\n" + 
//				"                <!--属性按顺序存放-->\r\n" + 
//				"                <parameter>0.6</parameter>\r\n" + 
//				"                <parameter>0.8</parameter>\r\n" + 
//				"                <parameter>0.3</parameter>\r\n" + 
//				"            </parameter_s>\r\n" + 
//				"        </protocol >\r\n" + 
//				"    </process>";
////		String str = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<cms>"
////	    + "<more>" + 1 + "\">" + "</more>" + "<article>"
////	    + "<id>1</id>" + "<itemid>A1001001A10G06B13619A23971</itemid>"
////	    + "<articleTitle>test</articleTitle>" + "<articleUrl>"
////	    + 2 + "</articleUrl>" + "</article>" + "</cms>";
//		Protocol20160111_1_1 protocol = new Protocol20160111_1_1();
//		Map<String, Object> pmap = protocol.parserRequest(str);
//		System.out.println(pmap.toString());
		
		Map<String, Object> protocalMap=new HashMap<String, Object>();
		protocalMap.put("type", "response");
		protocalMap.put("code", 1);
		protocalMap.put("reason", "失败原因");
		protocalMap.put("content", 123456);
		
		new Protocol20160111_1_1().creatResponse(protocalMap);
		
	}
}
