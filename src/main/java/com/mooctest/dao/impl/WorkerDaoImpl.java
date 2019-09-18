package com.mooctest.dao.impl;

import static com.mooctest.util.MongoAPIUtil.EMBEDDED_KEY;

import java.util.HashMap;
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
import com.mooctest.dao.WorkerDao;
import com.mooctest.model.Worker;
import com.mooctest.model.WorkerVO;
import com.mooctest.util.HttpUtil;
import com.mooctest.util.MongoAPIUtil;
@Repository
public class WorkerDaoImpl implements WorkerDao {
	
	public final String URL = "http://114.55.91.83:8191/api/user";
	@Override
	public Worker getWorkerById(String id) {
		// TODO Auto-generated method stub
		String url = URL.concat("/").concat(id);
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class);
        if (dto.getBody()== null) {
            return null;
        }
        return JSON.parseObject(dto.getBody().toJSONString(),Worker.class);
	}
	
	@Override
	public String getWorkerNameByReportId(String report_id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_id", report_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.STUINFO);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONArray jsonArray = dto.getBody().getJSONArray(EMBEDDED_KEY);
        if (jsonArray.size() == 0) {
            return "";
        }
        return (String) jsonArray.getJSONObject(0).get("name");
	}

	@Override
	public WorkerVO getWorkerVOByReportId(String report_id) {
		// TODO Auto-generated method stub
		HttpHeaders headers = MongoAPIUtil.createAuthHeaderForMongo();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("_id", report_id);
        String filterStr = MongoAPIUtil.generateFilterStr(queryParams);
        String url = MongoAPIUtil.genFilterUrlWithMaxPage(MongoAPIUtil.CO_REPORT_DB, MongoAPIUtil.STUINFO);
        RestTemplate rt = HttpUtil.getRestTemplate();
        ResponseEntity<JSONObject> dto = rt.exchange(url, HttpMethod.GET, entity, JSONObject.class, filterStr);
        JSONObject json = (JSONObject) dto.getBody().getJSONArray(EMBEDDED_KEY).get(0);
        if(json == null){
        	return null;
        }
        return new WorkerVO(json.getString("_id"), json.getString("worker_id"), json.getString("name"));
	}
}
