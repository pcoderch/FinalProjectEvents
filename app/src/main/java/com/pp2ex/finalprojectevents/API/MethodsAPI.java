package com.pp2ex.finalprojectevents.API;

public interface MethodsAPI {
    String URL_BASE = "http://puigmal.salle.url.edu/api/v2/";
    String URL_LOGIN = URL_BASE + "login"; //@POST
    String URL_REGISTER = URL_BASE + "users"; //@POST
    String URL_GET_USER = URL_BASE + "users/search"; //@GET
    String URL_CREATE_EVENT = URL_BASE + "events";  //@POST
    String URL_EDIT_PROFILE = URL_BASE + "users"; //@PUT
}
