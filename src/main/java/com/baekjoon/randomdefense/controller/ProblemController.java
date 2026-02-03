package com.baekjoon.randomdefense.controller;

import com.baekjoon.randomdefense.client.dto.TagDto;
import com.baekjoon.randomdefense.controller.dto.ProblemResponse;
import com.baekjoon.randomdefense.controller.dto.TagResponse;
import com.baekjoon.randomdefense.controller.dto.TierResponse;
import com.baekjoon.randomdefense.domain.ProblemFilter;
import com.baekjoon.randomdefense.domain.Tier;
import com.baekjoon.randomdefense.service.RandomSelectionService;
import com.baekjoon.randomdefense.service.SolvedAcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class ProblemController {

    private static final Logger log = LoggerFactory.getLogger(ProblemController.class);
    private static final String BOJ_PROBLEM_URL = "https://www.acmicpc.net/problem/";

    private final RandomSelectionService randomSelectionService;
    private final SolvedAcService solvedAcService;

    public ProblemController(RandomSelectionService randomSelectionService, SolvedAcService solvedAcService) {
        this.randomSelectionService = randomSelectionService;
        this.solvedAcService = solvedAcService;
    }

    @GetMapping("/problems/random")
    public ResponseEntity<ProblemResponse> getRandomProblem(
            @RequestParam(defaultValue = "BRONZE_5") Tier minTier,
            @RequestParam(defaultValue = "DIAMOND_1") Tier maxTier,
            @RequestParam(required = false) List<String> tags,
            @RequestParam(required = false) List<String> excludeTags,
            @RequestParam(required = false) String excludeUser) {

        log.info("Request: min={}, max={}, tags={}, excludeTags={}, user={}",
                minTier, maxTier, tags, excludeTags, excludeUser);

        ProblemFilter filter = ProblemFilter.builder()
                .minTier(minTier)
                .maxTier(maxTier)
                .tags(tags)
                .excludeTags(excludeTags)
                .excludeUsername(excludeUser)
                .build();

        return randomSelectionService.selectRandomProblem(filter)
                .map(problem -> {
                    List<String> tagKeys = problem.getTags() != null
                            ? problem.getTags().stream()
                                .map(TagDto::getDisplayName)
                                .collect(Collectors.toList())
                            : List.of();

                    ProblemResponse response = ProblemResponse.builder()
                            .problemId(problem.getProblemId())
                            .title(problem.getTitle())
                            .tier(Tier.fromLevel(problem.getLevel()))
                            .tierLevel(problem.getLevel())
                            .tags(tagKeys)
                            .solvedCount(problem.getSolvedCount())
                            .averageTries(problem.getAverageTries())
                            .url(BOJ_PROBLEM_URL + problem.getProblemId())
                            .build();
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/tags")
    public ResponseEntity<List<TagResponse>> getTags() {
        List<TagResponse> tags = solvedAcService.getAllTags().stream()
                .map(tag -> new TagResponse(tag.getKey(), tag.getDisplayName(), tag.getProblemCount()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tags);
    }

    @GetMapping("/tiers")
    public ResponseEntity<List<TierResponse>> getTiers() {
        List<TierResponse> tiers = Arrays.stream(Tier.values())
                .filter(t -> t != Tier.UNRATED)
                .map(t -> new TierResponse(t.name(), t.getDisplayName(), t.getLevel(), t.getColor()))
                .collect(Collectors.toList());
        return ResponseEntity.ok(tiers);
    }
}
