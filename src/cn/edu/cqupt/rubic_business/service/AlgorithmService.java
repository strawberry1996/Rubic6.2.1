package cn.edu.cqupt.rubic_business.service;

import cn.edu.cqupt.rubic_business.Model.po.AlgorithmPo;

public interface AlgorithmService {
	
	/**
	 * 根据aid查询算法
	 * @param aid
	 * @return
	 */
	AlgorithmPo findAlgorithmById(int aid);
}
