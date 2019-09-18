package com.mooctest.service;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.dao.TaskDao;
import com.mooctest.dao.WorkerDao;
import com.mooctest.model.BugHistory;
import com.mooctest.model.BugMirror;
import com.mooctest.model.Worker;
import com.mooctest.model.WorkerVO;

@Service
public class BugAnalysisService {
	@Autowired
	TaskDao taskDao;
	@Autowired
	WorkerDao workerDao;
	@Autowired
	BugDao bugDao;
	
	public JSONObject getBugListVO(List<String> bugIdList){
		JSONObject jsonResult = new JSONObject();
		List<BugMirror> bugMirrorList = taskDao.getBugMirrorList(bugIdList);
		JSONArray validJSONArray = new JSONArray();
		JSONArray invalidJSONArray = new JSONArray();
		for(BugMirror bugMirror:bugMirrorList){
			if(bugDao.getBugValidity(bugMirror.getId()) == 1){
				JSONObject temp = new JSONObject();
				temp.put("id", bugMirror.getId());
				temp.put("likeCount", bugMirror.getGood().size());
				temp.put("dislikeCount", bugMirror.getBad().size());
				temp.put("forkCount", bugDao.getBugHistory(bugMirror.getId()).getChildren().size());
				validJSONArray.add(temp);
			}
			else{
				JSONObject temp = new JSONObject();
				temp.put("id", bugMirror.getId());
				temp.put("likeCount", bugMirror.getGood().size());
				temp.put("dislikeCount", bugMirror.getBad().size());
				temp.put("forkCount", bugDao.getBugHistory(bugMirror.getId()).getChildren().size());
				invalidJSONArray.add(temp);
			}
		}
		jsonResult.put("valid", validJSONArray);
		jsonResult.put("invalid", invalidJSONArray);
		return jsonResult;
	}
	
	public JSONObject getReportBugList(String report_id){
		JSONObject jsonResult = new JSONObject();
		List<BugMirror> bugMirrorList = taskDao.getReportBugMirrorList(report_id);
		JSONArray validJSONArray = new JSONArray();
		JSONArray invalidJSONArray = new JSONArray();
		for(BugMirror bugMirror:bugMirrorList){
			if(bugDao.getBugValidity(bugMirror.getId()) == 1){
				JSONObject temp = new JSONObject();
				temp.put("id", bugMirror.getId());
				temp.put("likeCount", bugMirror.getGood().size());
				temp.put("dislikeCount", bugMirror.getBad().size());
				temp.put("forkCount", bugDao.getBugHistory(bugMirror.getId()).getChildren().size());
				validJSONArray.add(temp);
			}
			else{
				JSONObject temp = new JSONObject();
				temp.put("id", bugMirror.getId());
				temp.put("likeCount", bugMirror.getGood().size());
				temp.put("dislikeCount", bugMirror.getBad().size());
				temp.put("forkCount", bugDao.getBugHistory(bugMirror.getId()).getChildren().size());
				invalidJSONArray.add(temp);
			}
		}
		jsonResult.put("valid", validJSONArray);
		jsonResult.put("invalid", invalidJSONArray);
		return jsonResult;
	}
	
	public JSONObject workerLikeRelation(String case_take_id){
		JSONObject result = new JSONObject();
		List<BugMirror> bugMirrorList = taskDao.getBugMirrorList(case_take_id);
		Map<String,Integer> nodeMap = new HashMap<String, Integer>();
		Map<String,String> reportId2NameMap = new HashMap<String, String>();
		JSONArray linkArray = new JSONArray();
		JSONArray nodeArray = new JSONArray();
		
		for(BugMirror bugMirror : bugMirrorList){
			if(!bugMirror.getGood().isEmpty()){
				nodeMap.put(bugMirror.getReport_id(),nodeMap.getOrDefault(bugMirror.getReport_id(), 0)+bugMirror.getGood().size());
			    for(String sourceId : bugMirror.getGood()){
			    	if(!nodeMap.containsKey(sourceId)){
			    		nodeMap.put(sourceId, 0);
			    	}
			    	boolean flag = false;
					for (Iterator iterator = linkArray.iterator(); iterator.hasNext();) {
				    	JSONObject json = (JSONObject) iterator.next();
				    	if(json.get("source").equals(sourceId) && json.get("target").equals(bugMirror.getReport_id())){
				    		json.put("value", (int)json.get("value")+1);
				    		List<String> tempList = (List<String>) json.get("bugIdList");
				    		tempList.add(bugMirror.getId());
				    		json.put("bugIdList", tempList);
				    		flag = true;
				    	}
				    }
					if(!flag){
						List<String> bugList= new ArrayList<String>();
						bugList.add(bugMirror.getId());
						JSONObject jsonTemp = new JSONObject();
						jsonTemp.put("source", sourceId);
						jsonTemp.put("target", bugMirror.getReport_id());
						jsonTemp.put("value", 1);
						jsonTemp.put("bugIdList",bugList);
						linkArray.add(jsonTemp);
					}
			    }
			}
		}
		
		for(Map.Entry<String, Integer> entry:nodeMap.entrySet()){
			//String name = workerDao.getWorkerNameByReportId(entry.getKey());
			WorkerVO workerVO = workerDao.getWorkerVOByReportId(entry.getKey());
			Worker worker = workerDao.getWorkerById(workerVO.getWorker_id());
			reportId2NameMap.put(entry.getKey(), worker.getName());
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("name", worker.getName());
			jsonTemp.put("value", entry.getValue());
			jsonTemp.put("id", entry.getKey());
			jsonTemp.put("worker",worker);
			nodeArray.add(jsonTemp);
		}
		
		for(Iterator iterator = linkArray.iterator(); iterator.hasNext();){
			JSONObject json = (JSONObject) iterator.next();
			String sourceid = json.getString("source");
			String targetid = json.getString("target");
			//翻转下
			json.put("source", targetid);
			json.put("target", sourceid);
			if(reportId2NameMap.containsKey(sourceid)){
				json.put("targetName", reportId2NameMap.get(sourceid));
			}
			else{
				json.put("targetName", workerDao.getWorkerNameByReportId(sourceid));
			}
			
			if(reportId2NameMap.containsKey(targetid)){
				json.put("sourceName", reportId2NameMap.get(targetid));
			}
			else{
				json.put("sourceName", workerDao.getWorkerNameByReportId(targetid));
			}
		}
		result.put("nodes", nodeArray);
		result.put("links", linkArray);
		return result;
	}
	
	public JSONObject workerDisLikeRelation(String case_take_id){
		JSONObject result = new JSONObject();
		List<BugMirror> bugMirrorList = taskDao.getBugMirrorList(case_take_id);
		Map<String,Integer> nodeMap = new HashMap<String, Integer>();
		Map<String,String> reportId2NameMap = new HashMap<String, String>();
		JSONArray linkArray = new JSONArray();
		JSONArray nodeArray = new JSONArray();
		
		for(BugMirror bugMirror : bugMirrorList){
			if(!bugMirror.getBad().isEmpty()){
				nodeMap.put(bugMirror.getReport_id(),nodeMap.getOrDefault(bugMirror.getReport_id(), 0)+bugMirror.getBad().size());
			    for(String sourceId : bugMirror.getBad()){
			    	if(!nodeMap.containsKey(sourceId)){
			    		nodeMap.put(sourceId, 0);
			    	}
			    	boolean flag = false;
					for (Iterator iterator = linkArray.iterator(); iterator.hasNext();) {
				    	JSONObject json = (JSONObject) iterator.next();
				    	if(json.get("source").equals(sourceId) && json.get("target").equals(bugMirror.getReport_id())){
				    		json.put("value", (int)json.get("value")+1);
				    		List<String> tempList = (List<String>) json.get("bugIdList");
				    		tempList.add(bugMirror.getId());
				    		json.put("bugIdList", tempList);
				    		flag = true;
				    	}
				    }
					if(!flag){
						List<String> bugList= new ArrayList<String>();
						bugList.add(bugMirror.getId());
						JSONObject jsonTemp = new JSONObject();
						jsonTemp.put("source", sourceId);
						jsonTemp.put("target", bugMirror.getReport_id());
						jsonTemp.put("value", 1);
						jsonTemp.put("bugIdList",bugList);
						linkArray.add(jsonTemp);
					}
			    }
			}
		}
		
		for(Map.Entry<String, Integer> entry:nodeMap.entrySet()){
			//String name = workerDao.getWorkerNameByReportId(entry.getKey());
			WorkerVO workerVO = workerDao.getWorkerVOByReportId(entry.getKey());
			Worker worker = workerDao.getWorkerById(workerVO.getWorker_id());
			reportId2NameMap.put(entry.getKey(), worker.getName());
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("name", worker.getName());
			jsonTemp.put("value", entry.getValue());
			jsonTemp.put("id", entry.getKey());
			jsonTemp.put("worker",worker);
			nodeArray.add(jsonTemp);
		}
		
		for(Iterator iterator = linkArray.iterator(); iterator.hasNext();){
			JSONObject json = (JSONObject) iterator.next();
			String sourceid = json.getString("source");
			String targetid = json.getString("target");
			//翻转下
			json.put("source", targetid);
			json.put("target", sourceid);
			if(reportId2NameMap.containsKey(sourceid)){
				json.put("targetName", reportId2NameMap.get(sourceid));
			}
			else{
				json.put("targetName", workerDao.getWorkerNameByReportId(sourceid));
			}
			
			if(reportId2NameMap.containsKey(targetid)){
				json.put("sourceName", reportId2NameMap.get(targetid));
			}
			else{
				json.put("sourceName", workerDao.getWorkerNameByReportId(targetid));
			}
		}
		result.put("nodes", nodeArray);
		result.put("links", linkArray);
		return result;
	}
	
	public JSONObject workerForkRelation(String case_take_id){
		JSONObject result = new JSONObject();
		JSONArray linkArray = new JSONArray();
		JSONArray nodeArray = new JSONArray();
		Map<String,Integer> nodeMap = new HashMap<String, Integer>();
		Map<String,WorkerVO> reportId2WorkerVOMap = new HashMap<String, WorkerVO>();
		List<BugMirror> bugMirrorList = taskDao.getBugMirrorList(case_take_id);
		List<String> bugIds =bugMirrorList.stream().map(BugMirror::getId).collect(Collectors.toList());
		List<BugHistory> bugHistoryList = taskDao.getBugHistoryList(bugIds);
		List<BugHistory> hisList = taskDao.getBugHistoryList(bugIds).stream()
				 .filter(his->!his.getChildren().isEmpty())
				 .collect(Collectors.toList());
		
		for(BugHistory his:hisList){
			String targetReportId = getReportIdByMirrorList(bugMirrorList, his.getId());
			nodeMap.put(targetReportId,nodeMap.getOrDefault(targetReportId, 0)+his.getChildren().size());
			for(String child :his.getChildren()){
				String sourceReportId = getReportIdByMirrorList(bugMirrorList, child);
				if(!nodeMap.containsKey(sourceReportId)){
					nodeMap.put(sourceReportId,0);
				}
				boolean flag = false;
				for (Iterator iterator = linkArray.iterator(); iterator.hasNext();) {
			    	JSONObject json = (JSONObject) iterator.next();
			    	if(json.get("source").equals(sourceReportId) && json.get("target").equals(targetReportId)){
			    		json.put("value", (int)json.get("value")+1);
			    		JSONObject jsonHis = new JSONObject();
						jsonHis.put("parent", his.getId());
						jsonHis.put("son", child);
						JSONArray jsonArray = json.getJSONArray("forkList");
						jsonArray.add(jsonHis);
						json.put("forkList", jsonArray);
			    		flag = true;
			    	}
			    }
				if(!flag){
					JSONObject jsonTemp = new JSONObject();
					JSONArray jsonArray =new JSONArray();
					JSONObject jsonHis = new JSONObject();
					jsonHis.put("parent", his.getId());
					jsonHis.put("son", child);
					jsonArray.add(jsonHis);
					jsonTemp.put("source", sourceReportId);
					jsonTemp.put("target", targetReportId);
					jsonTemp.put("value", 1);
					jsonTemp.put("forkList", jsonArray);
					linkArray.add(jsonTemp);
				}
			}
		}
		
		for(Map.Entry<String, Integer> entry:nodeMap.entrySet()){
			WorkerVO workerVO = workerDao.getWorkerVOByReportId(entry.getKey());
			Worker worker = workerDao.getWorkerById(workerVO.getWorker_id());
			reportId2WorkerVOMap.put(entry.getKey(), workerVO);
			JSONObject jsonTemp = new JSONObject();
			jsonTemp.put("name", worker.getName());
			jsonTemp.put("value", entry.getValue());
			jsonTemp.put("id", entry.getKey());
			jsonTemp.put("worker",worker);
			nodeArray.add(jsonTemp);
		}
		
		for(Iterator iterator = linkArray.iterator(); iterator.hasNext();){
			JSONObject json = (JSONObject) iterator.next();
			String sourceid = json.getString("source");
			String targetid = json.getString("target");
			//翻转下
			json.put("source", targetid);
			json.put("target", sourceid);
			
			if(reportId2WorkerVOMap.containsKey(sourceid)){
				json.put("targetName", ((WorkerVO)reportId2WorkerVOMap.get(sourceid)).getName());
			}
			else{
				json.put("targetName", workerDao.getWorkerNameByReportId(sourceid));
			}
			
			if(reportId2WorkerVOMap.containsKey(targetid)){
				json.put("sourceName", ((WorkerVO)reportId2WorkerVOMap.get(targetid)).getName());
			}
			else{
				json.put("sourceName", workerDao.getWorkerNameByReportId(targetid));
			}
		}
		result.put("nodes", nodeArray);
		result.put("links", linkArray);
		return result;
	}
	
	private String getReportIdByMirrorList(List<BugMirror> bugMirrorList,String bugId){
		for(BugMirror bugMirror :bugMirrorList){
			if(bugMirror.getId().equals(bugId)){
				return bugMirror.getReport_id();
			}
		}
		return "";
	}
}
