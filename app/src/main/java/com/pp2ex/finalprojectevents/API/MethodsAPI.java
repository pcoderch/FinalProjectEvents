package com.pp2ex.finalprojectevents.API;

import com.pp2ex.finalprojectevents.DataStructures.User;

public class MethodsAPI {

    public static String URL_BASE = "http://puigmal.salle.url.edu/api/v2/";
    public static String URL_LOGIN = URL_BASE + "login"; //@POST
    public static String URL_REGISTER = URL_BASE + "users"; //@POST
    public static String URL_GET_USER = URL_BASE + "users/search"; //@GET
    public static String URL_EVENT = URL_BASE + "events";  //@POST
    public static String URL_EDIT_PROFILE = URL_BASE + "users"; //@PUT
    public static String URL_FRIENDS = URL_BASE + "friends"; //@GET
    public static String URL_FRIEND_REQUESTS = URL_FRIENDS + "/requests"; //@GET
    public static String URL_MESSAGES = URL_BASE + "messages"; //@GET
    public static String URL_CHATS = URL_MESSAGES + "/users"; //@GET
    public static String URL_ASSISTANCES = URL_BASE + "assistances"; //@GET
    public static final String URL_EVENTS_SEARCH = URL_EVENT + "/search"; //@GET
    public static final String URL_EVENTS_BEST = URL_EVENT + "/best"; //@GET

    public static String EventCount(int id) {
        return URL_REGISTER + "/" + id + "/assistances";
    }

    public static String getUserData(int id) {return URL_GET_USER + "/" + id; }

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

    public static String getMessages(int id) {
        return URL_MESSAGES + "/" + id;
    }

    public static String getUserByString(String search) { return URL_GET_USER + "?s=" + search; }

    public static String getEventById(int eventID) { return URL_EVENT + "/" + eventID; }

    public static String getEventAssistances(int intentEventId) { return URL_EVENT + "/" + intentEventId + "/assistances"; }

    public static String getAllUserEvents(int eventId, int userId) { return getEventAssistances(eventId) + "/" + userId; }

    public static String deleteAssistance(int intentEventId, int userId) { return URL_ASSISTANCES + "/" + userId + "/" + intentEventId; }

    public static String getAttendants(int eventId) { return URL_EVENT + "/" + eventId + "/assistances"; }
}
