package com.pp2ex.finalprojectevents.DataStructures;

import org.json.JSONException;
import org.json.JSONObject;

public class Event {
    private int id;
    private String name;
    private int ownerId;
    private String description;
    private String startDate;
    private String endDate;
    private int numOfParticipants;
    private String image;
    private String location;
    private String type;
    private String comments;
    private int rating; // 0 - 10

    public Event (String name, String description, String startDate, String endDate, int numOfParticipants, String image, String location, String type) {
        this.name = name;
        this.description = description;
        this.startDate = startDate;
        this.endDate = endDate;
        this.numOfParticipants = numOfParticipants;
        this.image = image;
        this.location = location;
        this.type = type;
        this.comments = comments;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public int getOwnerId() { return ownerId; }
    public String getDescription() { return description; }
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
    public void setStartDate(String startDate) { this.startDate = startDate; }
    public void setEndDate(String endDate) { this.endDate = endDate; }
    public void setNumOfParticipants(int numOfParticipants) { this.numOfParticipants = numOfParticipants; }
    public void setImage(String image) { this.image = image; }
    public void setLocation(String location) { this.location = location; }
    public void setType(String type) { this.type = type; }
    public void setComments(String comments) { this.comments = comments; }
    public void setRating(int rating) { this.rating = rating; }

    public JSONObject toJSON() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("name", name);
            jsonObject.put("description", description);
            jsonObject.put("startDate", startDate);
            jsonObject.put("endDate", endDate);
            jsonObject.put("numOfParticipants", numOfParticipants);
            jsonObject.put("image", image);
            jsonObject.put("location", location);
            jsonObject.put("type", type);
            jsonObject.put("comments", comments);
            jsonObject.put("rating", rating);
        } catch (JSONException e) {
            System.out.println("Error: " + e);
            e.printStackTrace();
        }
        return jsonObject;
    }
}
