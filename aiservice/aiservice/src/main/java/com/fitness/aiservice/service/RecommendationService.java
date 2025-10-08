package com.fitness.aiservice.service;

import com.fitness.aiservice.dto.RecommendationResponse;

import java.util.List;

public interface RecommendationService {

    public List<RecommendationResponse> getUserRecommendations(String userId);
    public RecommendationResponse getActivityRecommendation(String activityId);

}
