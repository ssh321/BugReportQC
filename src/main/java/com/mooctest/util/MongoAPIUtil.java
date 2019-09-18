package com.mooctest.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.http.HttpHeaders;

import java.util.List;
import java.util.Map;

public class MongoAPIUtil {
    public static final String HOST = "114.55.91.27";
    public static final String PORT = "80";
    public static final String TEST_DB = "testdb";
    public static final String CO_REPORT_DB = "co-report";
    public static final String REPORT_COLLECTION = "report";
    public static final String BUG_HISTORY_COLLECTION = "bugHistory";
    public static final String BUG_KEYWORD_COLLECTION = "keyWords";
    public static final String BUG_SCORE = "bugScore";
    public static final String THUMSUP = "thumsUp";
    public static final String BUGMIRROR = "bugMirror";
    public static final String IMAGE_ANNOTATION = "imageAnnotation";
    public static final String BUG_COLLECTION = "bug";
    public static final String AUTO_SCORE_COLLECTION = "autoScore";
    public static final String BUG_VALIDITY = "bugValidity";
    public static final String SUSPICIOUS_BEHAVIOR = "suspiciousBehavior";
    public static final String STUINFO = "stuInfo";
    public static final String BUG_PAGE = "bugPage";
    //public static final String BASE64_AUTH = "Basic YWRtaW46Y2hhbmdlaXQ=";
    public static final String BASE64_AUTH = "Basic YWRtaW46MTIzdHNldGNvb20=";

    public static final String WITH_MAX_PAGE_SIZE = "&count&page=1&pagesize=1000";

    public static final String EMBEDDED_KEY = "_embedded";

    public static HttpHeaders createAuthHeaderForMongo() {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", BASE64_AUTH);
        return headers;
    }

    public static String genCommonUrl(String collectionName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(HOST).append(":")
                .append(PORT).append("/")
                .append(TEST_DB).append("/").append(collectionName);
        return builder.toString();
    }

    public static String genCommonUrl(String dbName, String collectionName) {
        StringBuilder builder = new StringBuilder();
        builder.append("http://").append(HOST).append(":")
                .append(PORT).append("/")
                .append(dbName).append("/").append(collectionName);
        return builder.toString();
    }

//    public static String genFilterUrl(String collectionName) {
//        return genCommonUrl(collectionName) + "?filter={filter}";
//    }

    public static String genFilterUrlWithMaxPage(String collectionName) {
        return genCommonUrl(collectionName) + "?filter={filter}" + WITH_MAX_PAGE_SIZE;
    }

    public static String genFilterUrlWithMaxPage(String dbName, String collectionName) {
        return genCommonUrl(dbName, collectionName) + "?filter={filter}" + WITH_MAX_PAGE_SIZE;
    }
    
    public static String genNoFilterUrlWithMaxPage(String dbName, String collectionName, String page) {
        return genCommonUrl(dbName, collectionName) + "?&count&pagesize=1000&page="+page;
    }

    public static String genPostUrl(String dbName, String collectionName) {
        return genCommonUrl(dbName, collectionName);
    }

    public static String generateFilterStr(Map params) {
        JSONObject jsonObject = new JSONObject(params);
        return jsonObject.toString();
    }

    public static String genIdFilterStr(List<String> ids) {
        JSONObject jsonObject = new JSONObject();
        JSONObject in = new JSONObject();
        JSONArray idList = new JSONArray();
        idList.addAll(ids);
        in.put("$in", idList);
        jsonObject.put("_id", in);
        return jsonObject.toJSONString();
    }

}
