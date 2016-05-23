package cn.edu.cqupt.rubic_business.service;

import cn.edu.cqupt.rubic_business.Model.po.ProcessRecordPo;
import cn.edu.cqupt.rubic_business.Model.po.ResultPo;

public interface ResultService {
	
	int insertResult(ResultPo dataSetPo);
	
	int addProcessRecord(ProcessRecordPo processRecord);

}
