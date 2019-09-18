package com.mooctest.model;

import lombok.Data;

@Data
public class BehaviorHelper {
	String bug_id;
	String time;
	public BehaviorHelper(String bug_id, String time) {
		super();
		this.bug_id = bug_id;
		this.time = time;
	}
}
