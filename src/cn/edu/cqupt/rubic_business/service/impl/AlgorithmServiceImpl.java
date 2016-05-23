package cn.edu.cqupt.rubic_business.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;
import cn.edu.cqupt.rubic_business.Model.po.AlgorithmPo;
import cn.edu.cqupt.rubic_business.dao.AlgorithmDao;
import cn.edu.cqupt.rubic_business.service.AlgorithmService;

@Service("algorithmService")
public class AlgorithmServiceImpl implements AlgorithmService {
	private AlgorithmDao algorithmDao;
	@Resource(name = "algorithmDao")
	public void setAlgorithmDao(AlgorithmDao algorithmDao) {
		this.algorithmDao = algorithmDao;
	}

	public AlgorithmPo findAlgorithmById(int aid) {
		AlgorithmPo algorithm = algorithmDao.findAlgorithmById(aid);
		return algorithm;
	}

} 
