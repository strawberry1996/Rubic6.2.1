package cn.edu.cqupt.rubic_business.service.impl;
import cn.edu.cqupt.net.handle.TransFileHandle;
import cn.edu.cqupt.rubic_business.Model.po.AlgorithmPo;
import cn.edu.cqupt.rubic_business.Model.po.DataSetPo;
import cn.edu.cqupt.rubic_business.Model.po.ModelPo;
import cn.edu.cqupt.rubic_business.Model.po.UserPo;
import cn.edu.cqupt.rubic_business.dao.ModelAPIDao;
import cn.edu.cqupt.rubic_business.service.*;
import cn.edu.cqupt.rubic_business.util.ModelUtils;
import cn.edu.cqupt.rubic_core.controller.APIRunThread;
import cn.edu.cqupt.rubic_core.controller.RunThread;
import cn.edu.cqupt.rubic_core.controller.RunTreadPool;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.jdom.JDOMException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import javax.servlet.ServletContext;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dengzy on 2016/5/10.
 */
@Service("modelYiApiService")
public class ModelYiApIServiceImp implements ModelYiApIService {

    private ModelAPIDao modelAPIDao;
    @Autowired
    private DataSetService dataSetService;
    @Autowired
    private ModelServiceImpl modelService;

//   private static ExecutorService runThreadPool = Executors.newCachedThreadPool();
    //通过model_id调用model
    public JSONObject callModel(Map<String, Object> map)throws Exception{
        ServletContext servletContext = (ServletContext) map.get("servletContext");
        //根据model_api_id查找model_id
        int modelId =  modelAPIDao.getModelId(Integer.parseInt(map.get("apiid").toString()));
        //通过model_id找到model
        ModelPo model = modelAPIDao.findModelByModelId(modelId);
        //解析model中的xmlInfo
        Map<String, Object> modelInfo = getModelInfos(model);
        Map<String, Object> parameter  = new HashMap<String, Object>();
        JSONObject json = new JSONObject();
        //得到算法列表
        List<AlgorithmPo> algorithmList =(List<AlgorithmPo>) modelInfo.get("algorithm");
        //因为现在是算法是一对一的，所以暂时写死
        AlgorithmPo algorithm = algorithmList.get(0);
        //得到这个算法对应的平台
        String platform = algorithm.getPlatform();

        byte[] bytes = model.getObject();
        Object modelObject = modelService.getObjectFromByte(bytes);

        parameter.put("userid", map.get("userid"));
        parameter.put("platform", platform);
        parameter.put("data_id", map.get("data_id"));
        parameter.put("data_type", modelInfo.get("datasetType"));
        parameter.put("model", modelObject);
        parameter.put("algorithms", algorithmList);
        parameter.put("attributes", modelInfo.get("attributes"));
        parameter.put("notify_address", map.get("notify_address"));

//        servletContext.setAttribute("processHash", processHash);
//        servletContext.setAttribute("threadPool", runThreadPool);
        RunTreadPool pool =  new RunTreadPool((ExecutorService) servletContext.getAttribute("threadPool"));

        if(platform.equals("java")){
            APIRunThread apiRunThread = new APIRunThread(servletContext, parameter);
            pool.excuteTread(apiRunThread);
        }
        else if(platform.equals("hadoop")) {
            /*
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
            *
             */
        }
        return JSON.parseObject(parameter.toString());
    }

    private DataSetPo getDataSetPos(int dataIds) {
        DataSetPo dataSetPos = new DataSetPo();
        dataSetPos = dataSetService.findDataSetById(dataIds);

        return dataSetPos;
    }

    public Map<String, Object> getModelInfos(ModelPo model) throws JDOMException,IOException {
        // 解析xml
        ByteArrayInputStream modelInfos = new ByteArrayInputStream(model.getXmlInfo());
        //把xml里面的内容封装成Map
        return ModelUtils.readXML(modelInfos);

    }
}
