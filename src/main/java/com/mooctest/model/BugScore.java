package com.mooctest.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BugScore {
    private String id;
	
	private int grade;
	
	private int score;
}
