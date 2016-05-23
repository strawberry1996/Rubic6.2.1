package cn.edu.cqupt.rubic_business.service.impl;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import cn.edu.cqupt.rubic_business.Model.po.DataSetPo;
import cn.edu.cqupt.rubic_business.dao.DataSetDao;
import cn.edu.cqupt.rubic_business.service.DataSetService;

@Service("dataSetService")
public class DataSetServiceImpl implements DataSetService {
	
	private DataSetDao dataSetDao;
	
	@Resource(name = "dataSetDao")
	public void setDataSetDao(DataSetDao dataSetDao) {
		this.dataSetDao = dataSetDao;
	}

	public DataSetPo findDataSetById(int dataset_id) {
		//去文件服务器取得数据集，并构造成DataSetPo,删除数据文件
		DataSetPo dataSet=dataSetDao.findDataSetById(dataset_id);
		return dataSet;
	}

}
