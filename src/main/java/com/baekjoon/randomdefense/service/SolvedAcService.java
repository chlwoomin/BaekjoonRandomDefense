package com.baekjoon.randomdefense.service;

import com.baekjoon.randomdefense.client.SolvedAcApiClient;
import com.baekjoon.randomdefense.client.dto.ProblemDto;
import com.baekjoon.randomdefense.client.dto.ProblemSearchResponse;
import com.baekjoon.randomdefense.client.dto.TagDto;
import com.baekjoon.randomdefense.client.dto.TagListResponse;
import com.baekjoon.randomdefense.domain.ProblemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class SolvedAcService {

    private static final Logger log = LoggerFactory.getLogger(SolvedAcService.class);

    private final SolvedAcApiClient apiClient;

    public SolvedAcService(SolvedAcApiClient apiClient) {
        this.apiClient = apiClient;
    }

    // Removed @Cacheable to ensure random problems are fetched every time
    public List<ProblemDto> searchProblems(ProblemFilter filter) {
        String query = filter.toSolvedAcQuery();
        log.info("Searching problems with query: {}", query);

        ProblemSearchResponse response = apiClient.searchProblems(query, 1);

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        return response.getItems();
    }

    @Cacheable(value = "tags", key = "'all'")
    public List<TagDto> getAllTags() {
        log.info("Fetching all tags from solved.ac");

        TagListResponse response = apiClient.getAllTags();

        if (response == null || response.getItems() == null) {
            return Collections.emptyList();
        }

        // Filter out meta tags and sort by problem count
        return response.getItems().stream()
                .filter(tag -> !tag.isMeta())
                .sorted((a, b) -> Integer.compare(b.getProblemCount(), a.getProblemCount()))
                .toList();
    }
}
