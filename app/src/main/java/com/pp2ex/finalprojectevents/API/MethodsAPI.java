package com.pp2ex.finalprojectevents.API;

public interface MethodsAPI {
    String URL_BASE = "http://puigmal.salle.url.edu/api/v2/";
    String URL_LOGIN = URL_BASE + "login";
    String URL_REGISTER = URL_BASE + "users";
    String URL_GET_USER = URL_BASE + "users/search";
    String URL_CREATE_EVENT = URL_BASE + "events";
}
