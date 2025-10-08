package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;

public interface ActivityService {

    ActivityResponse trackActivity(ActivityRequest activityRequest);
    Boolean validateUser(String userId);
}
