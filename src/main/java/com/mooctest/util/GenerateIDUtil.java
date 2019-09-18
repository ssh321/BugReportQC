package com.mooctest.util;

import java.util.UUID;

public class GenerateIDUtil {
	public static String getUUID(){
        UUID uuid=UUID.randomUUID();
        String str = uuid.toString(); 
        String uuidStr=str.replace("-", "");
        return uuidStr;
    }
}
