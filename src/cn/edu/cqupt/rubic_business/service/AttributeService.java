package cn.edu.cqupt.rubic_business.service;

import java.util.List;

import cn.edu.cqupt.rubic_business.Model.po.AttributePo;
import cn.edu.cqupt.rubic_business.Model.po.ResultdatasetAttributeRelationshipPo;

public interface AttributeService {
	/**
	 * 添加attribute
	 * 
	 * @param attributePo
	 * @return
	 */
	int addAttribute(AttributePo attributePo);

	List<AttributePo> getAttributesByDId(int datasetId);
	
	void addResultdatasetAttributeRelation(ResultdatasetAttributeRelationshipPo resultdatasetAttributeRelationshipPo);

}
