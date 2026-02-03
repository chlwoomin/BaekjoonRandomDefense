package com.baekjoon.randomdefense.controller.dto;

public class TierResponse {
    private String name;
    private String displayName;
    private int level;
    private String color;

    public TierResponse() {}

    public TierResponse(String name, String displayName, int level, String color) {
        this.name = name;
        this.displayName = displayName;
        this.level = level;
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
