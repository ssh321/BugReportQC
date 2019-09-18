package com.mooctest.model;

import lombok.Data;

@Data
public class ImageAnnotation {
    private String id;
	
	private String width;
	
	private String height;
	
	private String[] xs;
	
	private String[] ys;
}
