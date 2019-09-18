package com.mooctest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.model.Bug;
import com.mooctest.service.BugInfoGainService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/infoGain")
public class BugInfoGainController {
    @Autowired
    BugInfoGainService bugInfoGainService;
    @Autowired
    BugDao bugDao;
    
    @RequestMapping("/getInfoGain")
    @ResponseBody
    public JSONObject getInfoGain(@RequestParam String id) {
       	JSONObject result = new JSONObject();
    	Bug son = bugDao.getBugById(id);
    	if(son == null){
    		result.put("status","500");
    		result.put("detail", "无此bugId");
    		return result;
    	}
    	Bug parent = bugDao.getParentBug(id);
    	if(parent == null){
    		result.put("status","500");
    		result.put("detail", "无父bug");
    		return result;
    	}
    	String str =JSON.toJSONString(bugInfoGainService.getInfoGain(id));
		result.put("status","200");
		result.put("detail", str);
		return result;
    }
}
