package com.mooctest.model;

import java.util.List;

import lombok.Data;
@Data
public class BehaviorSimilarHelper {
	String time;
	List<String> similarList;
	public BehaviorSimilarHelper(String time, List<String> similarList) {
		super();
		this.time = time;
		this.similarList = similarList;
	}
}
