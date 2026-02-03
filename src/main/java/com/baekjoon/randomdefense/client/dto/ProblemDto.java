package com.baekjoon.randomdefense.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemDto {
    private int problemId;
    private String titleKo;
    private String titleEn;
    private int level;
    private int solvedCount;
    private double averageTries;
    private List<TagDto> tags;
    private boolean isSolvable;
    private boolean isPartial;

    public ProblemDto() {}

    public int getProblemId() {
        return problemId;
    }

    public void setProblemId(int problemId) {
        this.problemId = problemId;
    }

    public String getTitleKo() {
        return titleKo;
    }

    public void setTitleKo(String titleKo) {
        this.titleKo = titleKo;
    }

    public String getTitleEn() {
        return titleEn;
    }

    public void setTitleEn(String titleEn) {
        this.titleEn = titleEn;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getSolvedCount() {
        return solvedCount;
    }

    public void setSolvedCount(int solvedCount) {
        this.solvedCount = solvedCount;
    }

    public double getAverageTries() {
        return averageTries;
    }

    public void setAverageTries(double averageTries) {
        this.averageTries = averageTries;
    }

    public List<TagDto> getTags() {
        return tags;
    }

    public void setTags(List<TagDto> tags) {
        this.tags = tags;
    }

    public boolean isSolvable() {
        return isSolvable;
    }

    public void setSolvable(boolean solvable) {
        isSolvable = solvable;
    }

    public boolean isPartial() {
        return isPartial;
    }

    public void setPartial(boolean partial) {
        isPartial = partial;
    }

    public String getTitle() {
        return titleKo != null && !titleKo.isBlank() ? titleKo : titleEn;
    }
}
