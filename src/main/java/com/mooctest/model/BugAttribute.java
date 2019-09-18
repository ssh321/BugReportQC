package com.mooctest.model;

import java.util.HashMap;
import java.util.Map;

import lombok.Data;

@Data
public class BugAttribute {
	private String id;
	private double description;//越大越好
	private double readability;
	private double sentenceAverageLenght;//7-15较好
	private double imprecise;//越小越好0最好
	private double anaphoric;//越小越好0最好
	private double directive;//越大越好 增益用到
	private double negative;//越大越好 增益用到
	private double behavior;//越大越好 增益用到
	private double action;//越大越好 增益用到
	private double interfaceElement;//越大越好 增益用到
	private double itemization;//越大越好 增益用到
	private double imageCount;//越大越好
	private double like;//越大越好
	private double disLike;//越小越好0最好
	private double imageAnnotationAreaRatio;//越大越好
	private short environment;
	private double score;
	public Map<String,Double> weightMap = new HashMap<String,Double>();
	public BugAttribute(String id) {
		// TODO Auto-generated constructor stub
		this.id = id;
	}
	public BugAttribute(){
		
	}
}
