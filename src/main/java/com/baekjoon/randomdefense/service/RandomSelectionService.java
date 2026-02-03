package com.baekjoon.randomdefense.service;

import com.baekjoon.randomdefense.client.dto.ProblemDto;
import com.baekjoon.randomdefense.domain.ProblemFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class RandomSelectionService {

    private static final Logger log = LoggerFactory.getLogger(RandomSelectionService.class);

    private final SolvedAcService solvedAcService;
    private final Random random = new SecureRandom();

    public RandomSelectionService(SolvedAcService solvedAcService) {
        this.solvedAcService = solvedAcService;
    }

    public Optional<ProblemDto> selectRandomProblem(ProblemFilter filter) {
        log.info("Selecting random problem with filter: {}", filter.toSolvedAcQuery());

        List<ProblemDto> problems = solvedAcService.searchProblems(filter);

        if (problems.isEmpty()) {
            log.warn("No problems found for the given filter");
            return Optional.empty();
        }

        // solved.ac API already returns random order with sort=random
        // but we add extra randomization for cached results
        int index = random.nextInt(problems.size());
        ProblemDto selected = problems.get(index);

        log.info("Selected problem: {} - {}", selected.getProblemId(), selected.getTitle());
        return Optional.of(selected);
    }
}
