package cn.edu.cqupt.apipipe;

import cn.edu.cqupt.rubic_business.service.ModelAPIService;
import com.alibaba.fastjson.JSONObject;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by LiuMian on 2015/12/2.
 *
 */

@Controller
@RequestMapping("/api")
public class ModelAPIController {

	// TODO 暂时先不管apikey、rettype
	@Resource
	private ModelAPIService modelAPIService;
	@Autowired
	private static final Logger logger = Logger.getLogger(ModelAPIController.class);


	@RequestMapping(value = "/instance",produces = "application/json;charset=UTF-8"
			,method = RequestMethod.GET)
	@ResponseBody
	public JSONObject callModelAPI(HttpServletRequest request, @RequestParam("apiid") int apiId, @RequestParam("userid") int userid){

		Enumeration<String> parameterNames = request.getParameterNames();
		logger.info("call ModelAPI parameters: "+request.getParameterMap());
		System.out.println("userid: " + userid);
		System.out.println("apiid: "+apiId);
		
		//模型参数存入hashmap中
		Map<String,String> attrs = new HashMap<String,String>();

		while (parameterNames.hasMoreElements()){
			String parameterName = parameterNames.nextElement();
			if("apiid" != parameterName)
				try {
					attrs.put(parameterName,new String(request.getParameter(parameterName).getBytes("ISO-8859-1"), "UTF-8"));
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
		}

		JSONObject result = new JSONObject();
		try {
			result = modelAPIService.callModel(apiId,attrs,userid);
		} catch (Exception e) {
			logger.error("call model error "+ e.getMessage());
//			e.printStackTrace();
			result.put("errmsg",e.getMessage());
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	@RequestMapping(value = "/instance",produces = "application/json;charset=UTF-8"
			,method = RequestMethod.POST)
	@ResponseBody
	public JSONObject callModelAPI(@RequestBody JSONObject json){
		JSONObject result = new JSONObject();

		int apiId = json.getIntValue("apiid");
		Map<String,String> attrs = (Map<String, String>) json.get("attrs");
		int userid = Integer.parseInt(json.get("userid").toString());

		try {
			result = modelAPIService.callModel(apiId, attrs, userid);
		} catch (Exception e) {
			logger.error("call model error "+ e.getMessage());
			e.printStackTrace();
			result.put("errmsg", e.getMessage());
		}
		return result;
	}
	
}
