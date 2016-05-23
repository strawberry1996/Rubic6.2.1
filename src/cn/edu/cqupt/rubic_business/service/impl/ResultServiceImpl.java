package cn.edu.cqupt.rubic_business.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.cqupt.rubic_business.Model.po.ProcessRecordPo;
import cn.edu.cqupt.rubic_business.Model.po.ResultPo;
import cn.edu.cqupt.rubic_business.dao.ProcessRecordDao;
import cn.edu.cqupt.rubic_business.dao.ResultDao;
import cn.edu.cqupt.rubic_business.service.ResultService;
@Service("resultService")
public class ResultServiceImpl implements ResultService {
	private ResultDao resultDao;
	private ProcessRecordDao processRecordDao;
	
	@Resource(name="resultDao")
	public void setResultDao(ResultDao resultDao) {
		this.resultDao = resultDao;
	}

	@Resource(name="processRecordDao")
	public void setProcessRecordDao(ProcessRecordDao processRecordDao) {
		this.processRecordDao = processRecordDao;
	}
	

	@Override
	public int insertResult(ResultPo dataSetPo) {
		resultDao.addResult(dataSetPo);
		return dataSetPo.getResultdataset_id();		
	}
	
	@Override
	public int addProcessRecord(ProcessRecordPo processRecord){
		processRecordDao.addProcessRecord(processRecord);
		//返回刚刚插入的process的processID
		return processRecord.getProcess_id();
	}

}
