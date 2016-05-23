package cn.edu.cqupt.rubic_business.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.cqupt.rubic_business.Model.po.ProcessRecordPo;
import cn.edu.cqupt.rubic_business.dao.ProcessRecordDao;
import cn.edu.cqupt.rubic_business.service.ProcessRecordService;

/**
 * 
 * @author he GuangQin
 *
 */
@Service("processRecordService")
public class ProcessRecordServiceImpl implements ProcessRecordService {
	
	private ProcessRecordDao processRecordDao;
	
	@Resource(name="processRecordDao")
	public void setProcessRecordDao(ProcessRecordDao processRecordDao) {
		this.processRecordDao = processRecordDao;
	}

	@Override
	public void updateProcessRecordByID(ProcessRecordPo processRecord) {
		processRecordDao.updateProcessRecordByID(processRecord);
	}
	
}
