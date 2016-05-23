package cn.edu.cqupt.rubic_business.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cn.edu.cqupt.rubic_business.Model.po.AttributePo;
import cn.edu.cqupt.rubic_business.Model.po.ResultdatasetAttributeRelationshipPo;
import cn.edu.cqupt.rubic_business.dao.AttributeDao;
import cn.edu.cqupt.rubic_business.dao.ResultDao;
import cn.edu.cqupt.rubic_business.dao.ResultdatasetAttributeRelationshipDao;
import cn.edu.cqupt.rubic_business.service.AttributeService;

@Service("attributeService")
public class AttributeServiceImpl implements AttributeService {
	private AttributeDao attributeDao;
	@Autowired
	private ResultDao resultDao;
	
	@Autowired
	private ResultdatasetAttributeRelationshipDao resultdatasetAttributeRelationshipDao;

	@Resource(name = "attributeDao")
	public AttributeDao getAttributeDao(AttributeDao attributeDao) {
		return this.attributeDao = attributeDao;
	}
	
	@Override
	public int addAttribute(AttributePo attributePo) {
		attributeDao.addAttribute(attributePo);
		return attributePo.getAttribute_id();
	}

	@Override
	public List<AttributePo> getAttributesByDId(int datasetId) {
		return attributeDao.findAttributesByDid(datasetId);
	}


	@Override
	public void addResultdatasetAttributeRelation(
			ResultdatasetAttributeRelationshipPo resultdatasetAttributeRelationshipPo) {
		
		resultdatasetAttributeRelationshipDao.addResultdatasetAttributeRelation(resultdatasetAttributeRelationshipPo);
		
	}

}
