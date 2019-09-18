package com.mooctest.dao.impl;

import static com.mooctest.util.MongoAPIUtil.EMBEDDED_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugDao;
import com.mooctest.model.Bug;
import com.mooctest.model.BugHistory;
import com.mooctest.model.BugMirror;
import com.mooctest.model.BugPage;
import com.mooctest.model.BugQuality;
import com.mooctest.model.ImageAnnotation;
import com.mooctest.util.HttpUtil;
import com.mooctest.util.MongoAPIUtil;
@Repository
public class BugDaoImpl implements BugDao {    
	public List<ImageAnnotation> getImageAnnotationList(Bug bug){
		List<String> imageAnnotations = getImageAnnotationIdList(bug);
		if(imageAnnotations == null){
			return null;
		}
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<String> entity = new HttpEntity<>(headers);
	
		String filterStr = MongoAPIUtil.genIdFilterStr(imageAnnotations);
		String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.IMAGE_ANNOTATION);
	
		RestTemplate rt = HttpUtil.getRestTemplate();
		ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
	
		JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
		if (jsonArray.size() == 0) {
			return null;
		}
		return jsonArray.toJavaList(ImageAnnotation.class);
	}
	 
	private List<String> getImageAnnotationIdList(Bug bug){
		if(bug.getImg_url()==null || bug.getImg_url().equals("")){
			return null;
		}
		List<String> imgUrls = java.util.Arrays.asList(bug.getImg_url().split(","));
		return imgUrls.stream().map(url->url2ImgAnnotationId(url)).collect(Collectors.toList());
	}
	
	private String url2ImgAnnotationId(String url){
		int endIndex = url.lastIndexOf('/');
		String str1 = url.substring(0,endIndex);
		int startIndex = str1.lastIndexOf('/');
		String str2 = str1.substring(startIndex+1);
		return str2;
	}
	@Override
	public Bug getBugById(String id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_id", id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_COLLECTION);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(Bug.class).get(0);
	}

	@SuppressWarnings("null")
	@Override
	public Bug getParentBug(String id) {
		// TODO Auto-generated method stub
		BugHistory history= this.getBugHistory(id);
		if(history == null && history.getParent().equals("null")){
			return null;
		}
		return this.getBugById(history.getParent());
	}

	@Override
	public BugHistory getBugHistory(String id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        Map<String, Object> queryParams = new HashMap<>();
        queryParams.put("_id", id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_HISTORY_COLLECTION);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugHistory.class).get(0);
	}
	
	@Override
	public BugMirror getBugMirrorById(String id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
	    HttpEntity<String> entity = new HttpEntity<>(headers);
	    Map<String, Object> queryParams = new HashMap<>();
		queryParams.put("_id", id);
		String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
	  	String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUGMIRROR);

        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(BugMirror.class).get(0);
	}

	@Override
	public boolean saveBugValidity(String id, int valid) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("_id", id);
		jsonObj.put("validity",valid);
		jsonArray.add(jsonObj);
		
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(jsonArray, headers);
		String url = MongoAPIUtil.genPostUrl(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_VALIDITY);
		RestTemplate rt = HttpUtil.getRestTemplate();
		ResponseEntity<String> result = rt.exchange(url, HttpMethod.POST, entity, String.class);
		if(result.getStatusCodeValue()==200){
			return true;
		}
		else{
			return false;
		}
	}

	@Override
	public int getBugValidity(String id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_id", id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_VALIDITY);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return -1;
        }
        return (int) jsonArray.getJSONObject(0).get("validity");
	}

	@Override
	public int getBugScore(String id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean judgeBugScore(String bug_id, String score) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public BugQuality getBugQuality(String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean saveBugPage(List<BugPage> bugPageList) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		for(BugPage bugPage :bugPageList){
			JSONObject jsonObj = JSONObject.parseObject(JSON.toJSONString(bugPage));
			jsonObj.put("_id", jsonObj.get("id"));
			jsonObj.remove("id");
			jsonArray.add(jsonObj);
		}
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(jsonArray, headers);
		String url = MongoAPIUtil.genPostUrl(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.BUG_PAGE);
		RestTemplate rt = HttpUtil.getRestTemplate();
		ResponseEntity<String> result = rt.exchange(url, HttpMethod.POST, entity, String.class);
		if(result.getStatusCodeValue()==200){
			return true;
		}
		else{
			return false;
		}
	}
}
