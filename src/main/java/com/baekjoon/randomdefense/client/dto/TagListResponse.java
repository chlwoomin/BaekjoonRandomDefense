package com.baekjoon.randomdefense.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TagListResponse {
    private int count;
    private List<TagDto> items;

    public TagListResponse() {}

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<TagDto> getItems() {
        return items;
    }

    public void setItems(List<TagDto> items) {
        this.items = items;
    }
}
