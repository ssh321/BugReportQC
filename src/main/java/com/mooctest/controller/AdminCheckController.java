package com.mooctest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mooctest.service.AdminCheckService;
import com.mooctest.service.BugInfoGainService;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/adminCheck")
public class AdminCheckController {
	@Autowired
	AdminCheckService adminCheckService;
	@Autowired
	BugInfoGainService bugInfoGainService;
    @RequestMapping("/getSingle")
    @ResponseBody
    public JSONObject getSingleList(String case_take_id,int start,int count,String bugPages) {
		return adminCheckService.getSingleList(case_take_id, start, count, bugPages);
    }
    
    @RequestMapping("/getTree")
    @ResponseBody
    public JSONObject getTreeList(String case_take_id,int start,int count,String bugPages) {
		return adminCheckService.getTreeList(case_take_id, start, count, bugPages);
    }
    
    @RequestMapping("/getTreePath")
    @ResponseBody
    public JSONObject getTreePath(String bug_id) {
		return adminCheckService.getTreePath(bug_id);
    }
    
    @RequestMapping("/judgeBug")
    @ResponseBody
    public JSONObject judgeBug(String bug_id,String score) {
		return adminCheckService.judgeBug(bug_id, score);
    }
    
    @RequestMapping("/getDetail")
    @ResponseBody
    public JSONObject getBugDetail(String bug_id) {
		return adminCheckService.getBugDetail(bug_id);
    }
    
    @RequestMapping("/getGrade")
    @ResponseBody
    public JSONObject getGrade(String bug_id) {
		return adminCheckService.getGrade(bug_id);
    }
    
    @RequestMapping("/getBugQuality")
    @ResponseBody
    public JSONObject getBugQuality(String bug_id) {
		return adminCheckService.getBugQuality(bug_id);
    }
    
    @RequestMapping("/getInfogain")
    @ResponseBody
    public JSONObject getInfoGain(String bug_id) {
		return adminCheckService.getBugQuality(bug_id);
    }
}
