package com.pp2ex.finalprojectevents.DataStructures;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    private int id;
    private String name;
    private String lastName;
    private String email;
    private String password;
    private String image;
    private String token;
    private static User authenticatedUser;

    public User(int id, String name, String lastName, String email, String password, String image) {
        this.id = id;
        this.name = name;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.image = image;
    }

    public static User getUserFromJson(String response) throws JSONException {
        response = response.substring(1, response.length() - 1);
        JSONObject jsonObject = new JSONObject(response);
        int id = jsonObject.getInt("id");
        String name = jsonObject.getString("name");
        String lastName = jsonObject.getString("last_name");
        String email = jsonObject.getString("email");
        String password = jsonObject.getString("password");
        String image = jsonObject.getString("image");
        return new User(id, name, lastName, email, password, image);
    }

    public String getEmail() { return email; }
    public String getPassword() { return password; }
    public String getImage() { return image; }
    public String getName() { return name; }
    public String getLastName() { return lastName; }
    public String getToken() { return token; }
    public static User getAuthenticatedUser() { return authenticatedUser; }

    public void setEmail(String email) { this.email = email; }
    public void setPassword(String password) { this.password = password; }
    public void setImage(String image) { this.image = image; }
    public void setName(String name) { this.name = name; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setToken(String token) { this.token = token; }
    public static void setAuthenticatedUser(User authenticatedUser) { User.authenticatedUser = authenticatedUser; }


    public boolean verifyEmailChar(String email) {
        return email.contains("@") && email.contains(".");
    }
    public boolean verifyPasswordChar(String password) {
        return password.length() >= 8 && password.matches(".*\\d.*");
    }

}
