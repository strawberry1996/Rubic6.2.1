package cn.edu.cqupt.rubic_business.dao;

import cn.edu.cqupt.rubic_business.Model.po.ModelPo;
/**
 * Created by LY on 2015/12/6.
 */
public interface ModelAPIDao {

   	   ModelPo findModelByAPIId(int apiId) throws Exception;

	   int getModelId(int ModelApiId)throws Exception;

	   ModelPo findModelByModelId(int modelId) throws Exception;

}
