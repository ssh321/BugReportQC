package com.mooctest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugQuality {
	String id;
	int validity;
	int autoScore;
}
