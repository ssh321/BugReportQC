package com.mooctest.dao.impl;
import static com.mooctest.util.MongoAPIUtil.EMBEDDED_KEY;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.BugAutoScoreDao;
import com.mooctest.model.BugAttribute;
import com.mooctest.util.HttpUtil;
import com.mooctest.util.MongoAPIUtil;
@Repository
public class BugAutoScoreDaoImpl implements BugAutoScoreDao {

	@Override
	public boolean saveAutoScoreList(List<BugAttribute> bugAttrList) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		for(BugAttribute bugAttr : bugAttrList){
			JSONObject jsonObj = new JSONObject();
			jsonObj.put("_id", bugAttr.getId());
			jsonObj.put("autoScore", (int)bugAttr.getScore());
			jsonArray.add(jsonObj);
		}
		
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(jsonArray, headers);
		String url = MongoAPIUtil.genPostUrl(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.AUTO_SCORE_COLLECTION);
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
	public int getAutoScoreById(String id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_id", id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.AUTO_SCORE_COLLECTION);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return -1;
        }
        return (int) jsonArray.getJSONObject(0).get("autoScore");
	}

	@Override
	public boolean saveAutoScore(BugAttribute bugAttr) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = new JSONObject();
		jsonObj.put("_id", bugAttr.getId());
		jsonObj.put("autoScore", (int)bugAttr.getScore());
		jsonArray.add(jsonObj);
		
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(jsonArray, headers);
		String url = MongoAPIUtil.genPostUrl(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.AUTO_SCORE_COLLECTION);
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
