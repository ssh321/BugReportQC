package com.mooctest.model;

import lombok.Data;

@Data
public class Worker {
	String id;
	String email;
	String name;
	String mobile;
	String password;
	long createTime;
	String school;
	String photoUrl;
	String province;
	boolean manualCheckValid;
	int availability;
	int integral;
}
