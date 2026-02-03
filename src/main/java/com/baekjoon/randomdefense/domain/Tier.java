package com.baekjoon.randomdefense.domain;

import java.util.Arrays;

public enum Tier {
    UNRATED(0, "Unrated", "nr"),
    BRONZE_5(1, "Bronze V", "b5"),
    BRONZE_4(2, "Bronze IV", "b4"),
    BRONZE_3(3, "Bronze III", "b3"),
    BRONZE_2(4, "Bronze II", "b2"),
    BRONZE_1(5, "Bronze I", "b1"),
    SILVER_5(6, "Silver V", "s5"),
    SILVER_4(7, "Silver IV", "s4"),
    SILVER_3(8, "Silver III", "s3"),
    SILVER_2(9, "Silver II", "s2"),
    SILVER_1(10, "Silver I", "s1"),
    GOLD_5(11, "Gold V", "g5"),
    GOLD_4(12, "Gold IV", "g4"),
    GOLD_3(13, "Gold III", "g3"),
    GOLD_2(14, "Gold II", "g2"),
    GOLD_1(15, "Gold I", "g1"),
    PLATINUM_5(16, "Platinum V", "p5"),
    PLATINUM_4(17, "Platinum IV", "p4"),
    PLATINUM_3(18, "Platinum III", "p3"),
    PLATINUM_2(19, "Platinum II", "p2"),
    PLATINUM_1(20, "Platinum I", "p1"),
    DIAMOND_5(21, "Diamond V", "d5"),
    DIAMOND_4(22, "Diamond IV", "d4"),
    DIAMOND_3(23, "Diamond III", "d3"),
    DIAMOND_2(24, "Diamond II", "d2"),
    DIAMOND_1(25, "Diamond I", "d1"),
    RUBY_5(26, "Ruby V", "r5"),
    RUBY_4(27, "Ruby IV", "r4"),
    RUBY_3(28, "Ruby III", "r3"),
    RUBY_2(29, "Ruby II", "r2"),
    RUBY_1(30, "Ruby I", "r1");

    private final int level;
    private final String displayName;
    private final String queryCode;

    Tier(int level, String displayName, String queryCode) {
        this.level = level;
        this.displayName = displayName;
        this.queryCode = queryCode;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getQueryCode() {
        return queryCode;
    }

    public static Tier fromLevel(int level) {
        return Arrays.stream(values())
                .filter(t -> t.level == level)
                .findFirst()
                .orElse(UNRATED);
    }

    public String getColor() {
        if (this == UNRATED) return "#2D2D2D";
        if (level >= 1 && level <= 5) return "#AD5600";   // Bronze
        if (level >= 6 && level <= 10) return "#435F7A";  // Silver
        if (level >= 11 && level <= 15) return "#EC9A00"; // Gold
        if (level >= 16 && level <= 20) return "#27E2A4"; // Platinum
        if (level >= 21 && level <= 25) return "#00B4FC"; // Diamond
        return "#FF0062"; // Ruby
    }
}
