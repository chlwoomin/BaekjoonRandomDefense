package com.baekjoon.randomdefense.controller.dto;

import com.baekjoon.randomdefense.domain.Tier;

import java.util.List;

public class ProblemResponse {
    private int problemId;
    private String title;
    private Tier tier;
    private String tierDisplayName; // 추가된 필드
    private int tierLevel;
    private String tierColor;
    private List<String> tags;
    private int solvedCount;
    private double averageTries;
    private String url;

    public ProblemResponse() {}

    public int getProblemId() { return problemId; }
    public void setProblemId(int problemId) { this.problemId = problemId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public Tier getTier() { return tier; }
    public void setTier(Tier tier) { this.tier = tier; }

    public String getTierDisplayName() { return tierDisplayName; }
    public void setTierDisplayName(String tierDisplayName) { this.tierDisplayName = tierDisplayName; }

    public int getTierLevel() { return tierLevel; }
    public void setTierLevel(int tierLevel) { this.tierLevel = tierLevel; }

    public String getTierColor() { return tierColor; }
    public void setTierColor(String tierColor) { this.tierColor = tierColor; }

    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }

    public int getSolvedCount() { return solvedCount; }
    public void setSolvedCount(int solvedCount) { this.solvedCount = solvedCount; }

    public double getAverageTries() { return averageTries; }
    public void setAverageTries(double averageTries) { this.averageTries = averageTries; }

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final ProblemResponse response = new ProblemResponse();

        public Builder problemId(int problemId) {
            response.problemId = problemId;
            return this;
        }

        public Builder title(String title) {
            response.title = title;
            return this;
        }

        public Builder tier(Tier tier) {
            response.tier = tier;
            response.tierDisplayName = tier.getDisplayName(); // 값 설정
            response.tierColor = tier.getColor();
            return this;
        }

        public Builder tierLevel(int tierLevel) {
            response.tierLevel = tierLevel;
            return this;
        }

        public Builder tags(List<String> tags) {
            response.tags = tags;
            return this;
        }

        public Builder solvedCount(int solvedCount) {
            response.solvedCount = solvedCount;
            return this;
        }

        public Builder averageTries(double averageTries) {
            response.averageTries = averageTries;
            return this;
        }

        public Builder url(String url) {
            response.url = url;
            return this;
        }

        public ProblemResponse build() {
            return response;
        }
    }
}
