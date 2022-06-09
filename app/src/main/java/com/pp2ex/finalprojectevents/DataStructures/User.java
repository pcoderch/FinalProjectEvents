package com.pp2ex.finalprojectevents.DataStructures;

public class User {
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String image;

    public User(String name, String lastName, String email, String password, String image) {
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.image = image;
    }
    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setImage(String image) { this.image = image; }
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public boolean verifyEmailChar(String email) {
        return email.contains("@") && email.contains(".");
    }
    public boolean verifyPasswordChar(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

}
