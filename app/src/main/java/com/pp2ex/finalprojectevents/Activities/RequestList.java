package com.pp2ex.finalprojectevents.Activities;

public class RequestList {
    public String color;
    public String name;
    public String email;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public RequestList(String color, String name, String email) {
        this.color = color;
        this.name = name;
        this.email = email;
    }
}
