package com.pp2ex.finalprojectevents.API;


import android.content.Context;

import com.android.volley.Request;
import com.android.volley.toolbox.Volley;
import com.android.volley.RequestQueue;
import com.pp2ex.finalprojectevents.Activities.SignInActivity;

public class VolleySingleton {
    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;

    private VolleySingleton(Context context) {
        mRequestQueue = (RequestQueue) Volley.newRequestQueue(context.getApplicationContext());
    }

    public static synchronized VolleySingleton getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new VolleySingleton(context);
        }
        return mInstance;
    }

    public static RequestQueue newRequestQueue(SignInActivity signInActivity) {
        return VolleySingleton.getInstance(signInActivity).getRequestQueue();
    }

    public RequestQueue getRequestQueue() {
        return mRequestQueue;
    }

    public void addToRequestQueue(Request request) {
        mRequestQueue.add(request);
    }

}
