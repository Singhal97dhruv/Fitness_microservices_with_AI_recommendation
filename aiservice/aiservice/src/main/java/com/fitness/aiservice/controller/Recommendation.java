package com.fitness.aiservice.controller;

import com.fitness.aiservice.dto.RecommendationResponse;
import com.fitness.aiservice.service.RecommendationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/recommendation")
public class Recommendation {

    @Autowired
    private RecommendationService recommendationService;

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<RecommendationResponse>> getUserRecommendation(@PathVariable String userId){
        return ResponseEntity.ok(recommendationService.getUserRecommendations(userId));
    }

    @GetMapping("/activity/{activityId}")
    public ResponseEntity<RecommendationResponse> getActivityRecommendation(@PathVariable String activityId){
        return ResponseEntity.ok(recommendationService.getActivityRecommendation(activityId));
    }

}
