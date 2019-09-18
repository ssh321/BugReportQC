package com.mooctest.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.dao.TaskDao;

@Service
public class AdminCheckService {
	@Autowired
	TaskDao taskDao;
	@Autowired
	BugDao bugDao;
	//{"TreeRoot":[["10010000034706","true","产品下"]],"Count":144}
    public JSONObject getSingleList(String case_take_id,int start,int count,String bugPages) {
		return null;
    }
    
    //{"TreeRoot":[["10010000034698","3","3","5","true","推荐目的地查看会停止app"]],"Count":21}
    public JSONObject getTreeList(String case_take_id,int start,int count,String bugPages) {
		return null;
    }
    
    //path深度遍历结果
    //{"path":[["10010000034698","10010000034712"],["10010000034698","10010000034717"],["10010000034698","10010000034917","10010000035194"]],"score":[["10010000034698","4"],["10010000034712","5"],["10010000034717","6"],["10010000034917","8"],["10010000035194","10"]],"invalid":[]}
    public JSONObject getTreePath(String bug_id) {
		return null;
    }
    
    public JSONObject judgeBug(String bug_id,String score) {
		return null;
    }
    //BugMirror Bug
    public JSONObject getBugDetail(String bug_id) {
		return null;
    }
    //{"grade":4}
    public JSONObject getGrade(String bug_id) {
		return null;
    }
    //{"autoScore":4,"validity":1}
    public JSONObject getBugQuality(String bug_id) {
		return null;
    }
}
