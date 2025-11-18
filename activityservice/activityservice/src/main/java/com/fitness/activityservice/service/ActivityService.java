package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;

import java.util.List;

public interface ActivityService {

    public ActivityResponse trackActivity(ActivityRequest activityRequest);
    public Boolean validateUser(String userId);
    public List<ActivityResponse> getUserActivities(String userId);
    public void deleteActivity(String activityId);


}
