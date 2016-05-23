package cn.edu.cqupt.rubic_business.service;

import cn.edu.cqupt.rubic_business.Model.po.ProcessRecordPo;

/**
 * 
 * @author he GuangQin
 *
 */
public interface ProcessRecordService {
	
	/**
	 * 更新算法运行记录
	 * @param processRecord
	 * @author LiangYH
	 */
	void updateProcessRecordByID(ProcessRecordPo processRecord);

}
