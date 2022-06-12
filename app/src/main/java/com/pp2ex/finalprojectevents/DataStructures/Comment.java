package com.pp2ex.finalprojectevents.DataStructures;

public class Comment {
    private int id;
    private String name;
    private String last_name;
    private String email;
    private int rating;
    private String comment;

    public Comment(int id, String name, String last_name, String email, int rating, String comment) {
        this.id = id;
        this.name = name;
        this.last_name = last_name;
        this.email = email;
        this.rating = rating;
        this.comment = comment;
    }

    // Getters
    public int getId() { return id; }
    public String getName() { return name; }
    public String getLast_name() { return last_name; }
    public String getEmail() { return email; }
    public int getRating() { return rating; }
    public String getComment() { return comment; }

    // Setters
    public void setId(int id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setLast_name(String last_name) { this.last_name = last_name; }
    public void setEmail(String email) { this.email = email; }
    public void setRating (int rating) { this.rating = rating; }
    public void setComment(String comment) { this.comment = comment; }

}
