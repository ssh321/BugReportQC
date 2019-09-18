package com.mooctest.model;

import lombok.Data;

import java.util.List;

@Data
public class BugHistory {
    private String id;

    private String parent;

    private List<String> children;

    private String root;
}
