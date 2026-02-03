package com.baekjoon.randomdefense.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class ProblemSearchResponse {
    private int count;
    private List<ProblemDto> items;

    public ProblemSearchResponse() {}

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<ProblemDto> getItems() {
        return items;
    }

    public void setItems(List<ProblemDto> items) {
        this.items = items;
    }
}
