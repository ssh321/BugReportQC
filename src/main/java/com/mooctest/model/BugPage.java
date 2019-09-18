package com.mooctest.model;

import lombok.Data;

@Data
public class BugPage implements java.io.Serializable{
	private static final long serialVersionUID = 5647215351692151191L;

    private String id;
	
	private String case_take_id;
	
	private String page1;
	
	private String page2;
	
	private String page3;	
}
