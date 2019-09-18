package com.mooctest.dao.impl;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.model.BugHistory;
import com.mooctest.model.BugMirror;
import com.mooctest.dao.TaskDao;
import com.mooctest.model.Bug;
import com.mooctest.model.BugScore;
import com.mooctest.util.HttpUtil;
import com.mooctest.util.MongoAPIUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.mooctest.util.MongoAPIUtil.EMBEDDED_KEY;

@Repository
public class TaskDaoImpl implements TaskDao {

/*    public List<ReportDTO> getReports(long examId, long caseId) {

        HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("examId", examId);
        queryParams.put("caseId", caseId);

        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.REPORT_COLLECTION);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);

        return extractReportDTOs(dto.getBody().getJSONArray(EMBEDDED_KEY));
    }*/
    
    public List<Bug> getBugList(String examId, String caseId) {

        HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("case_take_id", caseId+"-"+examId);
        //queryParams.put("case_take_id", caseId+"-"+examId);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_COLLECTION);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(Bug.class);

    }
    
    public List<BugScore> getBugScoreList(List<String> bugIds){
    	 HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
         HttpEntity<String> entity = new HttpEntity<>(headers);

         String filterStr = MongoAPIUtil.genIdFilterStr(bugIds);
         String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_SCORE);

         RestTemplate rt = HttpUtil.getRestTemplate();
         ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
         
         JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
         if (jsonArray.size() == 0) {
             return null;
         }
         return jsonArray.toJavaList(BugScore.class);
    }
	
	public List<BugMirror> getBugMirrorList(List<String> bugIds) {
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String filterStr = MongoAPIUtil.genIdFilterStr(bugIds);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUGMIRROR);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugMirror.class);
	}

//    public List<ImageAnnotation> getImageAnnotationList(Bug bug){
//    	List<String> imageAnnotations = getImageAnnotationIdList(bug);
//    	if(imageAnnotations == null){
//    		return null;
//    	}
//		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
//		HttpEntity<String> entity = new HttpEntity<>(headers);
//
//		String filterStr = MongoAPIUtil.genIdFilterStr(imageAnnotations);
//		String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.IMAGE_ANNOTATION);
//
//		RestTemplate rt = HttpUtil.getRestTemplate();
//		ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
//
//		JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
//		if (jsonArray.size() == 0) {
//			return null;
//		}
//		return jsonArray.toJavaList(ImageAnnotation.class);
//    }
	@Override
	public List<BugMirror> getBugMirrorList(String case_take_id) {
		// TODO Auto-generated method stub
        HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("case_take_id", case_take_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUGMIRROR);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugMirror.class);
	}

	@Override
	public List<BugHistory> getBugHistoryList(List<String> bugIds) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        String filterStr = MongoAPIUtil.genIdFilterStr(bugIds);
        System.out.println(filterStr);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_HISTORY_COLLECTION);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);

        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugHistory.class);
	}

	@Override
	public List<BugMirror> getReportBugMirrorList(String report_id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("report_id", report_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUGMIRROR);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugMirror.class);
	}

	@Override
	public List<Bug> getBugListByPage(String page) {
		// TODO Auto-generated method stub
        HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        String url = MongoAPIUtil.genNoFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_COLLECTION,page);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(Bug.class);
	}
}
