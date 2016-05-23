package cn.edu.cqupt.rubic_business.service;

import com.alibaba.fastjson.JSONObject;

import java.util.Map;

/**
 * Created by Dengzy on 2016/5/10.
 */
public interface ModelYiApIService {
    //通过model_id调用model
    JSONObject callModel(Map<String, Object> map)throws Exception;

}
