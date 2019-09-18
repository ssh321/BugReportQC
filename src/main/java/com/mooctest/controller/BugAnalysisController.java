package com.mooctest.controller;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.alibaba.fastjson.JSONObject;
import com.mooctest.service.BugAnalysisService;
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/analysis")
public class BugAnalysisController {
	@Autowired
	BugAnalysisService bugAnalysisService;
    @RequestMapping("/workerLikeRelation")
    @ResponseBody
    public JSONObject workerLikeRelation(String case_take_id) {
		return bugAnalysisService.workerLikeRelation(case_take_id);
    }
    
    @RequestMapping("/workerDislikeRelation")
    @ResponseBody
    public JSONObject workerDislikeRelation(String case_take_id) {
		return bugAnalysisService.workerDisLikeRelation(case_take_id);
    }
    
    @RequestMapping("/workerForkRelation")
    @ResponseBody
    public JSONObject workerForkRelation(String case_take_id) {
		return bugAnalysisService.workerForkRelation(case_take_id);
    }
    
    @RequestMapping("/getReportBugList")
    @ResponseBody
    public JSONObject getReportBugList(String report_id) {
		return bugAnalysisService.getReportBugList(report_id);
    }
    
    @RequestMapping("/getTwoWorkerBugList")
    @ResponseBody
    public JSONObject getBugListVO(String bugIdListString) {
    	List<String> bugIdList = Stream.of(bugIdListString.split(",")).collect(Collectors.toList());
		return bugAnalysisService.getBugListVO(bugIdList);
    }
}
