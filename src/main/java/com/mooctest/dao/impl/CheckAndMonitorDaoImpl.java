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

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mooctest.dao.CheckAndMonitorDao;
import com.mooctest.model.SuspiciousBehavior;
import com.mooctest.util.HttpUtil;
import com.mooctest.util.MongoAPIUtil;
@Repository
public class CheckAndMonitorDaoImpl implements CheckAndMonitorDao {
	@Override
	public SuspiciousBehavior getSuspiciousBehavior(String worker_id, String case_take_id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("worker_id", worker_id);
        queryParams.put("case_take_id", case_take_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.SUSPICIOUS_BEHAVIOR);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(SuspiciousBehavior.class).get(0);
	}

	@Override
	public boolean saveSuspiciousBehavior(SuspiciousBehavior suspiciousBehavior) {
		// TODO Auto-generated method stub
		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObj = JSONObject.parseObject(JSON.toJSONString(suspiciousBehavior));
		jsonObj.put("_id", jsonObj.get("id"));
		jsonObj.remove("id");
		jsonArray.add(jsonObj);
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
		HttpEntity<JSONArray> entity = new HttpEntity<JSONArray>(jsonArray, headers);
		String url = MongoAPIUtil.genPostUrl(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.SUSPICIOUS_BEHAVIOR);
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
	public List<SuspiciousBehavior> getAllSuspiciousBehavior(String case_take_id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("case_take_id", case_take_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.SUSPICIOUS_BEHAVIOR);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return null;
        }
        return jsonArray.toJavaList(SuspiciousBehavior.class);
	}
}
