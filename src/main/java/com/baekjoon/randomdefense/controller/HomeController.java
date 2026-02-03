package com.baekjoon.randomdefense.controller;

import com.baekjoon.randomdefense.client.dto.TagDto;
import com.baekjoon.randomdefense.domain.Tier;
import com.baekjoon.randomdefense.service.SolvedAcService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Arrays;
import java.util.List;

@Controller
public class HomeController {

    private final SolvedAcService solvedAcService;

    public HomeController(SolvedAcService solvedAcService) {
        this.solvedAcService = solvedAcService;
    }

    @GetMapping("/")
    public String home(Model model) {
        // Add tiers (excluding UNRATED)
        List<Tier> tiers = Arrays.stream(Tier.values())
                .filter(t -> t != Tier.UNRATED)
                .toList();
        model.addAttribute("tiers", tiers);

        // Add popular tags (limit to top 30)
        List<TagDto> tags = solvedAcService.getAllTags().stream()
                .limit(30)
                .toList();
        model.addAttribute("tags", tags);

        return "index";
    }
}
