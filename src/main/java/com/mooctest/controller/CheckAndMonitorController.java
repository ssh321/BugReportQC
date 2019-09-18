package com.mooctest.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.TaskDao;
import com.mooctest.model.InfoGain;
import com.mooctest.model.SuspiciousBehavior;
import com.mooctest.model.Worker;
import com.mooctest.service.BugInfoGainService;
import com.mooctest.service.CheckAndMonitorService;
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/check")
public class CheckAndMonitorController {
    @Autowired
    BugInfoGainService bugInfoGainService;
    @Autowired
    CheckAndMonitorService checkAndMonitorService;
    @RequestMapping("/checkAndValid")
    @ResponseBody
    public JSONObject checkAndValidBug(String id,String worker_id,String report_id, String case_take_id) {
    	JSONObject jsonObj = new JSONObject();
    	String feedbackInfo = checkAndMonitorService.checkBug(id);
    	int validity = 1;
    	List<String> similarBugIdList = checkAndMonitorService.checkSimilarity(id);
    	if(!feedbackInfo.equals("")){
        	if(!checkAndMonitorService.addInvalidBug(id, worker_id, report_id, case_take_id)){
        		System.out.println("ERROR:addInvalidBug");
        	}
        	validity = 0;
    	}
    	if(!similarBugIdList.isEmpty()){
    		if(!checkAndMonitorService.addSimilarBug(id, worker_id, report_id, case_take_id, similarBugIdList)){
    			System.out.println("ERROR:addSimilarBug");
    		}
    	}
    	jsonObj.put("validity", validity);
    	jsonObj.put("similarity", similarBugIdList);
    	jsonObj.put("feedbackInfo", feedbackInfo);
		return jsonObj;
    }
    
    @RequestMapping("/checkValid")
    @ResponseBody
    public JSONObject checkValid(String id) {
    	JSONObject jsonObj = new JSONObject();
    	String feedbackInfo = checkAndMonitorService.checkBug(id);
    	int validity = 1;
    	List<String> similarBugIdList = checkAndMonitorService.checkSimilarity(id);
    	if(!feedbackInfo.equals("")){
        	validity = 0;
    	}
    	jsonObj.put("validity", validity);
    	jsonObj.put("feedbackInfo", feedbackInfo);
		return jsonObj;
    }
    
    @RequestMapping("/checkForkSubmit")
    @ResponseBody
    public JSONObject checkForkSubmit(String id,String worker_id,String report_id, String case_take_id) {
    	JSONObject result = new JSONObject();
    	InfoGain infoGain = bugInfoGainService.getInfoGain(id);
    	if(infoGain.getSentences().isEmpty() && infoGain.getImages().isEmpty()){
    		if(!checkAndMonitorService.addForkBugMap(id, worker_id, report_id, case_take_id)){
    			System.out.println("ERROR:addForkBugMap");
    		}
    	}
    	String str =JSON.toJSONString(bugInfoGainService.getInfoGain(id));
		result.put("status","200");
		result.put("detail", str);
		return result;
    }
    
    @RequestMapping("/jmeterCheckForkSubmit")
    @ResponseBody
    public JSONObject checkForkSubmit(String id) {
    	JSONObject result = new JSONObject();
    	InfoGain infoGain = bugInfoGainService.getInfoGain(id);
    	String str =JSON.toJSONString(bugInfoGainService.getInfoGain(id));
		result.put("status","200");
		result.put("detail", str);
		return result;
    }
    @RequestMapping("/checkLike")
    @ResponseBody
    public JSONObject checkLike(String id,String worker_id,String report_id, String case_take_id) {
    	JSONObject result = new JSONObject();
    	int validity = checkAndMonitorService.checkLike(id);
    	if(validity == -1){
    		System.out.println("ERROR:该bug尚未评价合理性");
    		result.put("status","500");
    		result.put("detail", "该bug尚未评价合理性");
    		return result;
    	}
    	else if(validity == 0){
    		checkAndMonitorService.addInvalidLikeBug(id, worker_id, report_id, case_take_id);
    	}
		result.put("status","200");
		result.put("validity", validity);
		return result;
    }
    
    @RequestMapping("/checkDisLike")
    @ResponseBody
    public JSONObject checkDislike(String id,String worker_id,String report_id, String case_take_id) {
    	JSONObject result = new JSONObject();
    	int validity = checkAndMonitorService.checkDislike(id);
    	if(validity == -1){
    		System.out.println("ERROR:该bug尚未评价合理性");
    		result.put("status","500");
    		result.put("detail", "该bug尚未评价合理性");
    		return result;
    	}
    	else if(validity == 0){
    		checkAndMonitorService.addInvalidDislikeBug(id, worker_id, report_id, case_take_id);
    	}
		result.put("status","200");
		result.put("validity", validity);
		return result;
    }
    
    @RequestMapping("/getSuspiciousBehavior")
    @ResponseBody
    public JSONObject getSuspiciousBehavior(String case_take_id, String start, String count) {
    	JSONObject result = new JSONObject();
    	List<SuspiciousBehavior> suspList = checkAndMonitorService.getSuspiciousBehavior(case_take_id, Integer.parseInt(start), Integer.parseInt(count));
    	if(suspList == null){
    		result.put("status","500");
    		return result;
    	}
    	List<Worker> workerList = checkAndMonitorService.getSuspiciousWorker(suspList);
		result.put("status","200");
		result.put("suspiciousBehaviorList", JSONArray.parseArray(JSON.toJSONString(suspList)));
		result.put("workerList", JSONArray.parseArray(JSON.toJSONString(workerList)));
		result.put("sumCount", checkAndMonitorService.getSuspiciousTotalCount(case_take_id));
		return result;
    }
    
}
