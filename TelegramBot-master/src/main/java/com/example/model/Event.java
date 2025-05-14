package com.example.model;

public class Event {
    private String id;
    private String eventName;
    private String time;
    private String description;
    private String groupLink;
    private int capacity;

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getEventName() { return eventName; }
    public void setEventName(String eventName) { this.eventName = eventName; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getGroupLink() { return groupLink; }
    public void setGroupLink(String groupLink) { this.groupLink = groupLink; }

    public int getCapacity() { return capacity; }
    public void setCapacity(int capacity) { this.capacity = capacity; }
}