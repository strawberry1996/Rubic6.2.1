package cn.edu.cqupt.rubic_business.dao;

import java.util.List;

import cn.edu.cqupt.rubic_business.Model.po.AttributePo;

public interface AttributeDao {
	
	List<AttributePo> findAttributesByDid(int dataset_id);
	
	int addAttribute(AttributePo attributePo);
	
}
