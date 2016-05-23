package cn.edu.cqupt.rubic_core.test.tools;

import cn.edu.cqupt.rubic_framework.algorithm_interface.Event;



public class VisulEvent implements Event {

	public String content;
	
	public void setContent(String str){
		content = str;
	}
	public Object getContent() {
		// TODO Auto-generated method stub
		return content;
	}

	public void print() {
		System.out.println(content);
		// TODO Auto-generated method stub

	}

}

