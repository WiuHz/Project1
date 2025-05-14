package com.example.model;

import java.util.List;

public class UserRegistrationState {
    private List<String> eventIds;
    private User user = new User();
    private UserRegistrationStep step = UserRegistrationStep.ASK_NAME;

    public User getUser() { 
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public UserRegistrationStep getStep() { 
        return step; 
    }
    
    public void setStep(UserRegistrationStep step) { 
        this.step = step; 
    }

    public List<String> getEventIds() { 
        return eventIds; 
    }

    public void setEventIds(List<String> eventIds) { 
        this.eventIds = eventIds; 
    }
}