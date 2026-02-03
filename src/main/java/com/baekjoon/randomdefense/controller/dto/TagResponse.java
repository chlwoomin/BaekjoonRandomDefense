package com.baekjoon.randomdefense.controller.dto;

public class TagResponse {
    private String key;
    private String displayName;
    private int problemCount;

    public TagResponse() {}

    public TagResponse(String key, String displayName, int problemCount) {
        this.key = key;
        this.displayName = displayName;
        this.problemCount = problemCount;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(int problemCount) {
        this.problemCount = problemCount;
    }
}
