package cn.edu.cqupt.rubic_core.execute;

import cn.edu.cqupt.rubic_framework.algorithm_interface.Event;
import cn.edu.cqupt.rubic_framework.service_interface.EventListener;

public class OperationListener implements EventListener {
	private Object content = null;

	@Override
	public void callBackAndView(Event e) {
		content = e.getContent();
	}

	@Override
	public void stuteChanged(Event e) {
		content = e.getContent();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public <T extends Object> T getContent(){
		
		return (T) content;
	}
}
