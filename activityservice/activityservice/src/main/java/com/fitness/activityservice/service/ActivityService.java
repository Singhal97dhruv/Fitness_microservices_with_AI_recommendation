package com.fitness.activityservice.service;

import com.fitness.activityservice.dto.ActivityRequest;
import com.fitness.activityservice.dto.ActivityResponse;

public interface ActivityService {

<<<<<<< HEAD
    ActivityResponse trackActivity(ActivityRequest activityRequest);
    Boolean validateUser(String userId);
=======
    public ActivityResponse trackActivity(ActivityRequest activityRequest);
    public Boolean validateUser(String userId);
>>>>>>> 1a6cf9ec19300989482b8d1458c97ea0f065a07f
}
