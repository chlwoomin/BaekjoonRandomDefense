package com.baekjoon.randomdefense.domain;

import java.util.List;

public class ProblemFilter {
    private final Tier minTier;
    private final Tier maxTier;
    private final List<String> tags;
    private final List<String> excludeTags;
    private final String excludeUsername;

    public ProblemFilter(Tier minTier, Tier maxTier, List<String> tags, List<String> excludeTags, String excludeUsername) {
        this.minTier = minTier;
        this.maxTier = maxTier;
        this.tags = tags;
        this.excludeTags = excludeTags;
        this.excludeUsername = excludeUsername;
    }

    public String toSolvedAcQuery() {
        StringBuilder query = new StringBuilder();

        // 한국어 문제 필터
        query.append("lang:ko");

        // 티어 범위
        if (minTier != null && maxTier != null) {
            query.append(" *").append(minTier.getQueryCode())
                    .append("..").append(maxTier.getQueryCode());
        }

        // 포함 태그 (OR 조건)
        if (tags != null && !tags.isEmpty()) {
            query.append(" (");
            for (int i = 0; i < tags.size(); i++) {
                if (i > 0) query.append(" | ");
                query.append("#").append(tags.get(i));
            }
            query.append(")");
        }

        // 제외 태그 (AND 조건)
        if (excludeTags != null && !excludeTags.isEmpty()) {
            for (String tag : excludeTags) {
                query.append(" -#").append(tag);
            }
        }

        // 푼 문제 제외
        if (excludeUsername != null && !excludeUsername.isBlank()) {
            query.append(" -s@").append(excludeUsername);
        }

        return query.toString().trim();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private Tier minTier;
        private Tier maxTier;
        private List<String> tags;
        private List<String> excludeTags;
        private String excludeUsername;

        public Builder minTier(Tier minTier) {
            this.minTier = minTier;
            return this;
        }

        public Builder maxTier(Tier maxTier) {
            this.maxTier = maxTier;
            return this;
        }

        public Builder tags(List<String> tags) {
            this.tags = tags;
            return this;
        }

        public Builder excludeTags(List<String> excludeTags) {
            this.excludeTags = excludeTags;
            return this;
        }

        public Builder excludeUsername(String excludeUsername) {
            this.excludeUsername = excludeUsername;
            return this;
        }

        public ProblemFilter build() {
            return new ProblemFilter(minTier, maxTier, tags, excludeTags, excludeUsername);
        }
    }
}
