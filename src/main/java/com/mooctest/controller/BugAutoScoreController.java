package com.mooctest.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugAutoScoreDao;
import com.mooctest.service.BugAutoScoreService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/autoScore")
public class BugAutoScoreController {
    @Autowired
    BugAutoScoreService bugAutoScoreService;
    @Autowired
    BugAutoScoreDao bugAutoScoreDao;
    @RequestMapping("/noParentAutoScore")
    @ResponseBody
    public JSONObject noParentAutoScore(@RequestParam String examId,@RequestParam String caseId) {
    	JSONObject result = new JSONObject();
    	if(bugAutoScoreService.noParentBugAutoScore(examId, caseId)){
    		result.put("status", "200");
    		result.put("detail", "计算保存成功");
    	}
    	else{
    		result.put("status", "500");
    		result.put("detail", "计算保存失败");
    	}
		return result;
    }
    @RequestMapping("/hasParentAutoScore")
    @ResponseBody
    public JSONObject hasParentAutoScore(@RequestParam String examId,@RequestParam String caseId) {
    	JSONObject result = new JSONObject();
    	if(bugAutoScoreService.hasParentBugAutoScore(examId, caseId)){
    		result.put("status", "200");
    		result.put("detail", "计算保存成功");
    	}
    	else{
    		result.put("status", "500");
    		result.put("detail", "计算保存失败");
    	}
		return result;
    }
    @RequestMapping("/getAutoScore")
    @ResponseBody
    public JSONObject getAutoScore(@RequestParam String id) {
    	JSONObject result = new JSONObject();
    	int autoScore = bugAutoScoreDao.getAutoScoreById(id);
    	if(autoScore !=-1){
    		JSONObject jsonObj = new JSONObject();
    		jsonObj.put("autoScore", autoScore);
    		result.put("status", "200");
    		result.put("detail", jsonObj);
    	}
    	else{
    		JSONObject jsonObj = new JSONObject();
    		jsonObj.put("autoScore", "暂无自动评分");
    		result.put("status", "500");
    		result.put("detail", jsonObj);
    	}
		return result;
    }
}
