package com.baekjoon.randomdefense.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagDto {
    private String key;
    private boolean isMeta;
    private int bojTagId;
    private int problemCount;
    private List<DisplayName> displayNames;

    public TagDto() {}

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @JsonProperty("isMeta")
    public boolean isMeta() {
        return isMeta;
    }

    public void setMeta(boolean meta) {
        isMeta = meta;
    }

    public int getBojTagId() {
        return bojTagId;
    }

    public void setBojTagId(int bojTagId) {
        this.bojTagId = bojTagId;
    }

    public int getProblemCount() {
        return problemCount;
    }

    public void setProblemCount(int problemCount) {
        this.problemCount = problemCount;
    }

    public List<DisplayName> getDisplayNames() {
        return displayNames;
    }

    public void setDisplayNames(List<DisplayName> displayNames) {
        this.displayNames = displayNames;
    }

    public String getDisplayName() {
        if (displayNames == null || displayNames.isEmpty()) {
            return key;
        }
        return displayNames.stream()
                .filter(d -> "ko".equals(d.getLanguage()))
                .findFirst()
                .map(DisplayName::getName)
                .orElse(displayNames.get(0).getName());
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DisplayName {
        private String language;
        private String name;
        private String shortName;

        public DisplayName() {}

        public String getLanguage() {
            return language;
        }

        public void setLanguage(String language) {
            this.language = language;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getShortName() {
            return shortName;
        }

        public void setShortName(String shortName) {
            this.shortName = shortName;
        }
    }
}
