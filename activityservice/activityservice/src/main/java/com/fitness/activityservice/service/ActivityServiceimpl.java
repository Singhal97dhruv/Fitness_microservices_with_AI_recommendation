package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.models.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class ActivityServiceimpl implements ActivityService{

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private WebClient userWebClient;

    @Override
    public ActivityResponse trackActivity(ActivityRequest activityRequest) {

        Boolean validUser= validateUser(activityRequest.getUserId());

        if(!validUser){
            throw new RuntimeException("Invalid userId: "+activityRequest.getUserId());
        }

        Activity activity= modelMapper.map(activityRequest,Activity.class);
        activity.setActivityId(null);
        Activity savedActivity=activityRepository.save(activity);

        ActivityResponse activityResponse=modelMapper.map(savedActivity,ActivityResponse.class);
        return activityResponse;
    }

    @Override
    public Boolean validateUser(String userId) {
        try{
            return userWebClient.get()
                    .uri("/api/v1/users/{userId}/validate",userId)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();
        }
        catch (WebClientResponseException e){
            e.printStackTrace();
        }
        return false;
    }
}
