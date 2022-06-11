package com.pp2ex.finalprojectevents.API;

public class MethodsAPI {
    public static String URL_BASE = "http://puigmal.salle.url.edu/api/v2/";
    public static String URL_LOGIN = URL_BASE + "login"; //@POST
    public static String URL_REGISTER = URL_BASE + "users"; //@POST
    public static String URL_GET_USER = URL_BASE + "users/search"; //@GET
    public static String URL_CREATE_EVENT = URL_BASE + "events";  //@POST
    public static String URL_EDIT_PROFILE = URL_BASE + "users"; //@PUT
    public static String URL_FRIENDS = URL_BASE + "friends"; //@GET
    public static String URL_FRIEND_REQUESTS = URL_FRIENDS + "/requests"; //@GET
    public static String URL_MESSAGES = URL_BASE + "messages"; //@GET
    public static String URL_CHATS = URL_MESSAGES + "/users"; //@GET

    public static String EventCount(int id) {
        return URL_REGISTER + "/" + id + "/assistances";
    }

    public static String getUserData(int id) {
        return URL_GET_USER + "";
    }

    public static String getFriendsCount(int id) {
        return URL_REGISTER + "/" + id + "/friends";
    }

    public static String getOwns(int id) {
        return URL_REGISTER + "/" + id + "/events";
    }

    public static String sendFriendRequest(int id) {
        return URL_FRIENDS + "/" + id;
    }

    public static String removeFriend(int id) {
        return URL_FRIENDS + "/" + id;
    }

    public static String answerRequest(int id) {
        return URL_FRIENDS + "/" + id;
    }

    public static String getLastMessage(int id) {
        return URL_MESSAGES + "/" + id;
    }
}
