package com.mooctest.model;

import java.util.List;
import java.util.Map;

import lombok.Data;
@Data
public class SuspiciousBehavior {
	String id;
	String worker_id;
	String case_take_id;
	String report_id;
	List<BehaviorHelper> invalidBugIds;
	List<BehaviorHelper> invalidLikeBugIds;
	List<BehaviorHelper> invalidDisLikeBugIds;
	Map<String,BehaviorHelper> invalidForkBugMap;
	Map<String,BehaviorSimilarHelper> similarBugMap;
	int behaviorCount;
	public SuspiciousBehavior(String id,String worker_id, String case_take_id, String report_id) {
		super();
		this.id = id;
		this.worker_id = worker_id;
		this.case_take_id = case_take_id;
		this.report_id = report_id;
	}
	
	public SuspiciousBehavior(String id, String worker_id, String case_take_id, String report_id,
			List<BehaviorHelper> invalidBugIds, List<BehaviorHelper> invalidLikeBugIds,
			List<BehaviorHelper> invalidDisLikeBugIds, Map<String, BehaviorHelper> invalidForkBugMap,
			Map<String, BehaviorSimilarHelper> similarBugMap) {
		super();
		this.id = id;
		this.worker_id = worker_id;
		this.case_take_id = case_take_id;
		this.report_id = report_id;
		this.invalidBugIds = invalidBugIds;
		this.invalidLikeBugIds = invalidLikeBugIds;
		this.invalidDisLikeBugIds = invalidDisLikeBugIds;
		this.invalidForkBugMap = invalidForkBugMap;
		this.similarBugMap = similarBugMap;
	}
	
}
