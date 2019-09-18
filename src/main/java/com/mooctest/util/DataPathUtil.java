package com.mooctest.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class DataPathUtil {
	// action配置文件路径 
	public static final String PROP_NAME = "hanlp.properties";
	public static final String MODEL_FILE_PATH = "corpus/sgns.wiki.word.txt";
	public static final String TRAIN_FILE_PATH = "corpus/corpus-training.utf8.txt";
	public static final String DOWNLOAD_IMAGE = "imageCompare";
	public static final String ATTR_DIC = "code-terms";
	public static final String SPLIT_STR = "//";
	private static String getPropertyByName(String path, String name) {
		String result = "";

		// 方法二：通过类加载目录getClassLoader()加载属性文件
		InputStream in = DataPathUtil.class.getClassLoader().getResourceAsStream(path);
		Properties prop = new Properties();
		try {
			prop.load(in);
			result = prop.getProperty(name).trim();
		} catch (IOException e) {
			System.out.println("读取配置文件出错");
			e.printStackTrace();
		}
		return result;
	}
	
	public static String getModelFilePath(){
		return getPropertyByName(PROP_NAME, "data")+SPLIT_STR+MODEL_FILE_PATH;
	}
	
	public static String getTrainFilePath(){
		return getPropertyByName(PROP_NAME, "data")+SPLIT_STR+TRAIN_FILE_PATH;
	}
	
	public static String getCompaeImagePath(){
		return getPropertyByName(PROP_NAME, "data")+SPLIT_STR+DOWNLOAD_IMAGE;
	}
	public static String getBugAttrDicPath(){
		return getPropertyByName(PROP_NAME, "data")+SPLIT_STR+ATTR_DIC;
	}
}
