package cn.edu.cqupt.apipipe;

import cn.edu.cqupt.rubic_business.service.ModelYiApIService;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Dengzy on 2016/5/10.
 */

@Controller
@RequestMapping("/modelApi")
public class ModelYiApiController {
    @Resource
    private ModelYiApIService modelYiAPIService;
    @Autowired
    private  HashMap<String, Object> processHash;
    @Autowired
    private static ExecutorService runThreadPool = Executors.newCachedThreadPool();

    @RequestMapping(value = "/yiModelApi",produces = "application/json;charset=UTF-8"
            ,method = RequestMethod.GET)
    @ResponseBody
    public JSONObject YiModelApi(HttpServletRequest request){
        ServletContext servletContext = request.getServletContext();
        JSONObject results = new JSONObject();
        Map<String,Object> map = new HashMap<String, Object>();

//        processHash = new HashMap<String,Object>();
//        servletContext.setAttribute("threadPool", runThreadPool);
//        servletContext.setAttribute("processHash", processHash);
        // 得到model_api_id
        int userId = Integer.parseInt(request.getParameter("userid"));
        int modelApiId = Integer.parseInt(request.getParameter("apiid"));
        int dataId = Integer.parseInt(request.getParameter("data_id"));
        String notifyAddress = request.getParameter("notify_address");
        map.put("userid", userId);
        map.put("apiid", modelApiId);
        map.put("data_id", dataId);
        map.put("notify_address", notifyAddress);
        map.put("servletContext",servletContext);

        try {
            results = modelYiAPIService.callModel(map);
        }catch(Exception e){
            e.printStackTrace();
        }
        return results;
    }
}
