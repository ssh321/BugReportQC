package com.mooctest.qc;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.stream.Collectors;

import com.hankcs.hanlp.HanLP;
import com.mooctest.model.BugAttribute;
import com.mooctest.util.DataPathUtil;

public class DescriptionAttrExtract {
	private static String DATA_DIR = DataPathUtil.getBugAttrDicPath()+DataPathUtil.SPLIT_STR;
	private static String[] IMPR;
	private static String[] ANAP;
	private static String[] DIRE;
	private static String[] NEGA;
	private static String[] BEHA;
	private static String[] ACTI;
	private static String[] INTE;
	public static String[] ITEM;
	static{
		buildDictionary();
	}
	
	// 分词函数
	private static List<String> getSegment(String text) {
		HanLP.Config.ShowTermNature = false;
		return HanLP.segment(text).stream().map(term->term.toString()).collect(Collectors.toList());
	}
	
	//构建词典
	private static  void buildDictionary(){
		IMPR = read_impr();
		ANAP = read_anap();
		DIRE = read_dire();
		NEGA = read_nega();
		BEHA = read_beha();
		ACTI = read_acti();
		INTE = read_inte();
		ITEM = read_item();
	}
	
	// 确定每个report的质量向量
	public static BugAttribute buildVec(String desc,BugAttribute bugAttri){
		List<String> desKeywordList = getSegment(desc);
		String[] desKeywords = (String[])desKeywordList.toArray(new String[desKeywordList.size()]); 
		String token = "，。、：！；";
		bugAttri.setDescription(desc.length());//文本长度
		// System.out.print("文本长度:"+sum0+",");
		bugAttri.setReadability(CnToStrokeCount.getReadability(desc));// 得到描述信息的可读性
		int temp = 1;
		for (int j = 0; j < desc.length(); j++) {
			for (int k = 0; k < token.length(); k++)
				if (desc.charAt(j) == token.charAt(k))
					temp = temp + 1;
		}
		bugAttri.setSentenceAverageLenght((double) desc.length() / temp);// 得到描述信息的平均文本长度
		
		int sum3 = 0;
		for (int j = 0; j < IMPR.length; j++) {
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(IMPR[j])) {
					sum3 = sum3 + 1; // 得到描述信息和输入信息中的imprecise term个数
				}
		}
		bugAttri.setImprecise(sum3);
		// System.out.print("不精确词:"+sum3+",");
		int sum4 = 0;
		for (int j = 0; j < ANAP.length; j++) {
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(ANAP[j])) {
					sum4 = sum4 + 1; // 得到描述信息和输入信息中的anaphoric term个数
				}
		}
		bugAttri.setAnaphoric(sum4);
		// System.out.print("指示词:"+sum4+",");
		int sum5 = 0;
		for (int j = 0; j < DIRE.length; j++) {
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(DIRE[j])) {
					sum5 = sum5 + 1; // 得到描述信息和输入信息中的directive term个数
				}
		}
		bugAttri.setDirective(sum5);
		// System.out.print("实例词:"+sum5+",");
		int sum6 = 0;
		for (int j = 0; j < NEGA.length; j++) {
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(NEGA[j])) {
					sum6 = sum6 + 1; // 得到描述信息中的negative term个数
				}
		}
		bugAttri.setNegative(sum6);
		// System.out.print("负向词:"+sum6+",");
		int sum7 = 0;
		for (int j = 0; j < BEHA.length; j++) {
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(BEHA[j])) {
					sum7 = sum7 + 1; // 得到描述信息中的behavior term个数
				}
		}
		bugAttri.setBehavior(sum7);
		// System.out.print("问题词:"+sum7+",");
		int sum8 = 0;
		for (int j = 0; j < ACTI.length; j++) {
			if (desKeywords.length == 0)
				break;
			for (int k = 0; k < desKeywords.length; k++) {
				if (desKeywords[k].equals(ACTI[j])) {
					sum8 = sum8 + 1; // 得到描述信息中的action term个数
				}
			}
		}
		bugAttri.setAction(sum8);
		// System.out.print("app动作词:"+sum8+",");
		int sum9 = 0;
		for (int j = 0; j < INTE.length; j++) {
			if (desKeywords.length == 0)
				break;
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(INTE[j])) {
					sum9 = sum9 + 1; // 得到输入信息中的interface element个数
				}
		}
		bugAttri.setInterfaceElement(sum9);
		// System.out.print("app界面词:"+sum9+",");
		int sum10 = 0;
		for (int j = 0; j < ITEM.length; j++) {
			if (desKeywords.length == 0)
				break;
			for (int k = 0; k < desKeywords.length; k++)
				if (desKeywords[k].equals(ITEM[j])) {
					sum10 = sum10 + 1; // 得到输入信息中的itemization个数
				}
		}
		bugAttri.setItemization(sum10);
		// System.out.print("app步骤词:"+sum10+",");
		// System.out.print("app截图:"+sum11);
		bugAttri.setEnvironment((short) 1);// 所有测试报告的环境信息默认为1
		return bugAttri;
		
	}
	
	private static String[] buildDictionary(File file) {
		BufferedReader in = null;
		StringBuilder sb = new StringBuilder();
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(file), "gbk"));
			String line = "";
			while ((line = in.readLine()) != null) {
				sb.append(line + " ");
			}
		} catch (Exception e) {
			// TODO: handle exception
		} finally {
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return sb.toString().split(" ");
	}
	
	// 读取所有imprecise terms不精确的
	private static String[] read_impr() {
		File file = new File(DATA_DIR + "4imprecise.txt");
		return buildDictionary(file);
	}
	
	// 读取所有anaphoric terms代词
	private static String[] read_anap(){
		File file = new File(DATA_DIR + "5anaphoric.txt");
		return buildDictionary(file);
	}

	// 读取所有directive terms举例子
	private static String[] read_dire(){
		File file = new File(DATA_DIR + "6directive.txt");
		return buildDictionary(file);
	}

	// 读取所有negative terms负向词
	private static String[] read_nega(){
		File file = new File(DATA_DIR + "7negative.txt");
		return buildDictionary(file);
	}

	// 读取所有behavior terms问题词
	private static String[] read_beha(){
		File file = new File(DATA_DIR + "8behavior.txt");
		return buildDictionary(file);
	}

	// 读取所有action terms app动作词
	private static String[] read_acti(){
		File file = new File(DATA_DIR + "9action.txt");
		return buildDictionary(file);
	}

	// 读取所有interface elements app界面词
	private static String[] read_inte(){
		File file = new File(DATA_DIR + "10interface.txt");
		return buildDictionary(file);
	}

	// 读取所有itemizations 步骤词
	private static String[] read_item(){
		File file = new File(DATA_DIR + "11itemization.txt");
		return buildDictionary(file);
	}
}