package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;

public interface ActivityService {

    public ActivityResponse trackActivity(ActivityRequest activityRequest);
    public Boolean validateUser(String userId);
}
