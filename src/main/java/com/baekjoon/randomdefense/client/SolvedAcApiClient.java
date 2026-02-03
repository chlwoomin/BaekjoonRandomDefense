package com.baekjoon.randomdefense.client;

import com.baekjoon.randomdefense.client.dto.ProblemSearchResponse;
import com.baekjoon.randomdefense.client.dto.TagListResponse;
import com.baekjoon.randomdefense.exception.SolvedAcApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Duration;

@Component
public class SolvedAcApiClient {

    private static final Logger log = LoggerFactory.getLogger(SolvedAcApiClient.class);
    private static final Duration TIMEOUT = Duration.ofSeconds(10);

    private final WebClient webClient;

    public SolvedAcApiClient(WebClient solvedAcWebClient) {
        this.webClient = solvedAcWebClient;
    }

    public ProblemSearchResponse searchProblems(String query, int page) {
        log.debug("Searching problems with query: {}, page: {}", query, page);

        try {
            ProblemSearchResponse response = webClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/search/problem")
                            .queryParam("query", query)
                            .queryParam("page", page)
                            .queryParam("sort", "random")
                            .build())
                    .retrieve()
                    .bodyToMono(ProblemSearchResponse.class)
                    .block(TIMEOUT);

            log.debug("Found {} problems", response != null ? response.getCount() : 0);
            return response;
        } catch (WebClientResponseException e) {
            log.error("API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new SolvedAcApiException("solved.ac API 호출 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error calling solved.ac API", e);
            throw new SolvedAcApiException("solved.ac API 연결 실패: " + e.getMessage());
        }
    }

    public TagListResponse getAllTags() {
        log.debug("Fetching all tags");

        try {
            TagListResponse response = webClient.get()
                    .uri("/tag/list")
                    .retrieve()
                    .bodyToMono(TagListResponse.class)
                    .block(TIMEOUT);

            log.debug("Found {} tags", response != null ? response.getCount() : 0);
            return response;
        } catch (WebClientResponseException e) {
            log.error("API error: {} - {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new SolvedAcApiException("태그 목록 조회 실패: " + e.getMessage());
        } catch (Exception e) {
            log.error("Unexpected error calling solved.ac API", e);
            throw new SolvedAcApiException("solved.ac API 연결 실패: " + e.getMessage());
        }
    }
}
