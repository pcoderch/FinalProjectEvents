package com.pp2ex.finalprojectevents.DataStructures;

public class Event {
    private int id;
    private String name;
    private int ownerId;
    private String description;
    private String date;
    private String startDate;
    private String endDate;
    private int numOfParticipants;
    private String image;
    private String location;
    private String type;
    private String comments;
    private int rating; // 0 - 10

    public Event(int id, String name, int ownerId, String description, String date, String startDate, String endDate, int numOfParticipants, String image, String location, String type, String comments, int rating) {
        this.id = id;
        this.name = name;
        this.ownerId = ownerId;
        this.description = description;
        this.date = date;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numOfParticipants = numOfParticipants;
        this.image = image;
        this.location = location;
        this.type = type;
        this.comments = comments;
        this.rating = rating;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getOwnerId() { return ownerId; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getStartDate() { return startDate; }
    public String getEndDate() { return endDate; }
    public int getNumOfParticipants() { return numOfParticipants; }
    public String getImage() { return image; }
    public String getLocation() { return location; }
    public String getType() { return type; }
    public String getComments() { return comments; }
    public int getRating() { return rating; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setOwnerId(int ownerId) { this.ownerId = ownerId; }
    public void setDescription(String description) { this.description = description; }
    public void setDate(String date) { this.date = date; }
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setNumOfParticipants(int numOfParticipants) { this.numOfParticipants = numOfParticipants; }
    public void setImage(String image) { this.image = image; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setComments(String comments) { this.comments = comments; }
    public void setRating(int rating) { this.rating = rating; }

}
