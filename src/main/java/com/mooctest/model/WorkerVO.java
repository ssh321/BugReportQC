package com.mooctest.model;

import lombok.Data;

@Data
public class WorkerVO {
	String report_id;
	String worker_id;
	String name;
	public WorkerVO(String report_id, String worker_id, String name) {
		super();
		this.report_id = report_id;
		this.worker_id = worker_id;
		this.name = name;
	}
	
}
