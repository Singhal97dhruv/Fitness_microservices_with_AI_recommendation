package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;
import com.fitness.activityservice.models.Activity;
import com.fitness.activityservice.repository.ActivityRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceimpl implements ActivityService{

    @Value("${kafka.topic.name}")
    private String topicName;

    @Autowired
    private ActivityRepository activityRepository;
    @Autowired
    private ModelMapper modelMapper;

    private final KafkaTemplate<String,Activity> kafkaTemplate;

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

        try{
            kafkaTemplate.send(topicName,savedActivity.getUserId(),savedActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    @Override
    public List<ActivityResponse> getUserActivities(String userId) {
        log.info("user id received: {}",userId);
        List<Activity> activities=activityRepository.findByUserId(userId);
        log.info("Activities received: {}",activities);
        List<ActivityResponse> activitiesResponse=activities.stream().map(activity -> modelMapper.map(activity, ActivityResponse.class)).toList();
        return activitiesResponse;
    }

    @Override
    public void deleteActivity(String activityId) {

        Activity activity=activityRepository.findById(activityId).orElseThrow(()->new ResourceNotFoundException("Activity with given activityId isn't present in Database: "+activityId));
        activityRepository.delete(activity);
    }
}
