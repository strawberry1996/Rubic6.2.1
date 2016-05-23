package cn.edu.cqupt.rubic_business.dao;

import cn.edu.cqupt.rubic_business.Model.po.ProcessRecordPo;

/**
 * 
 * @author He Huangqin
 *
 */
public interface ProcessRecordDao {

	int addProcessRecord(ProcessRecordPo processRecord); 
	
	/**
	 * 更新算法运行记录
	 * @param processRecord
	 * @author LiangYH
	 */
	void updateProcessRecordByID(ProcessRecordPo processRecord);
	
}
