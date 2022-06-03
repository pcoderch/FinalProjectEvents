package com.pp2ex.finalprojectevents;

public class User {
    private String username;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String image;

    public User (String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public String getEmail() { return email; }
    public String getUsername() { return username; }
    public String getPassword() { return password; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }

    public void setEmail(String email) { this.email = email; }
    public void setUsername(String username) { this.username = username; }
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
    public boolean verifyUsernameChar(String username) {
        return username.length() >= 4;
    }

}
