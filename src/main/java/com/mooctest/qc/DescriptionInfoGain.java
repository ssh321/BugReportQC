package com.mooctest.qc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.hankcs.hanlp.utility.SentencesUtil;

public class DescriptionInfoGain {
	private static final double THRESHOLD = 0.77;
	public static  Map<String, List<String>> getDescriptionInfoGain(String parentDes,String sonDes){
		Map<String,List<String>> map = new HashMap<String, List<String>>();
		List<String> sentenceList1 = preTreatSentences(SentencesUtil.toSentenceList(preTreatDes(parentDes)));
		List<String> sentenceList2 = preTreatSentences(SentencesUtil.toSentenceList(preTreatDes(sonDes)));
		List<String> sentenceList1Copy = deepCopy(sentenceList1);
		List<String> sentenceList2Copy = deepCopy(sentenceList2);
		sentenceList1.forEach(str1->{
			sentenceList2.forEach(str2->{
				if(DocVectorModelSingleton.getInstance().similarity(str1,str2)>THRESHOLD){
					sentenceList1Copy.remove(str1);
					sentenceList2Copy.remove(str2);
        		}
			});
		});
		map.put("parent", sentenceList1Copy);
		map.put("son", sentenceList2Copy);
		return map;
	}
	private static List<String> preTreatSentences(List<String> sentenceList){
		return sentenceList.stream().filter(str->str.length()>0&&!str.equals(",")).collect(Collectors.toList());
	}
	//对字符串预处理
	private static String preTreatDes(String source){
		//去除字符串所有空格
		source = source.replaceAll(" ", "");
		//String puncReg = "\\pP+~$`^=|<>～｀＄＾＋＝｜＜＞￥×]";
		StringBuilder sb  = new StringBuilder(source);
		if(sb.length() != 0){
			//断句（1）(1) 1. 1、
			//断连续数字为句子
			//断连接词
			//断/为句子
	        String reg = "\\(\\d+\\)|\\d+(\\.|、)|/|\\\\";
	        Pattern p = Pattern.compile(reg);
	        Matcher m = p.matcher(source);
	        int count = 0;
	        while(m.find()){
	        	sb.insert(m.start()+count, ',');
	        	count++;
	        }
		}
		return sb.toString();
	}
	//深复制List
	@SuppressWarnings("unchecked")
	private static <T> List<T> deepCopy(List<T> src){ 
		List<T> dest = new ArrayList<T>();
	    ByteArrayOutputStream byteOut = new ByteArrayOutputStream();  
	    ObjectOutputStream out;
		try {
			out = new ObjectOutputStream(byteOut);
			out.writeObject(src);  
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    

	    ByteArrayInputStream byteIn = new ByteArrayInputStream(byteOut.toByteArray());  
	    ObjectInputStream in;
		try {
			in = new ObjectInputStream(byteIn);
		    dest = (List<T>) in.readObject();
		} catch (IOException | ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
	    return dest;  
	}
}
