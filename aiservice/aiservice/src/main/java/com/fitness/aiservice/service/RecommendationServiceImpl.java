package com.fitness.aiservice.service;

import com.fitness.aiservice.dto.RecommendationResponse;
import com.fitness.aiservice.model.Recommendation;
import com.fitness.aiservice.repository.RecommendationRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService{

    @Autowired
    private RecommendationRepository recommendationRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Override
    public List<RecommendationResponse> getUserRecommendations(String userId) {

        List<Recommendation>recommendations=recommendationRepository.findByUserId(userId);
        List<RecommendationResponse> recommendationResponses=recommendations.stream().map(recommendation -> modelMapper.map(recommendation, RecommendationResponse.class)).toList();
        return recommendationResponses;
    }

    @Override
    public RecommendationResponse getActivityRecommendation(String activityId) {
        Recommendation recommendation=recommendationRepository.findByActivityId(activityId).orElseThrow(()->new RuntimeException("No recommendation found with this activityID"));
        RecommendationResponse recommendationResponse=modelMapper.map(recommendation,RecommendationResponse.class);
        return recommendationResponse;
    }
}
