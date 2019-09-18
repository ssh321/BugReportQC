package com.mooctest.util;

import org.springframework.web.client.RestTemplate;

public class HttpUtil {
    static RestTemplate rt = new RestTemplate();

    public static RestTemplate getRestTemplate() {
        return rt;
    }
}
