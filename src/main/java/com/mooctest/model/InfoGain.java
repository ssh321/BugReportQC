package com.mooctest.model;

import java.util.List;

import lombok.Data;

@Data
public class InfoGain {
	List<String> sentences;
	List<String> images;
	int likeCount;
	int dissCount;
}
