package com.mooctest.model;

import java.util.Set;

import lombok.Data;
@Data
public class BugMirror {
	private String id;

	private String case_take_id;

	private String bug_category;

	private String report_id;

	private int severity;

	private int recurrent;

	private String title;

	private Set<String> good;

	private Set<String> bad;

	private String img_url;

	private boolean flag;

	private String useCase;
}
