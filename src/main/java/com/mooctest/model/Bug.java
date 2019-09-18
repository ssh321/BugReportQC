package com.mooctest.model;

import lombok.Data;
@Data
public class Bug {
    private String id;

	private String case_take_id;
	
	private String bug_category;
	
	private int severity;
	
	private int recurrent;
	
	private String title;
	
	private String report_id;

	private String create_time_millis;
	
	private String description;
	
	private String img_url;
	
	private String bug_page;
	
	private String case_id;
}
