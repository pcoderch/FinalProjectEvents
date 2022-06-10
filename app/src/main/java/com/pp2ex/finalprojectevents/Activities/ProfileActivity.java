package com.pp2ex.finalprojectevents.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class ProfileActivity extends AppCompatActivity {

    //private ImageView profilePicture = findViewById(R.id.profileImage);
    private TextView eventsCount;
    private TextView friendsCount;
    private TextView owns;
    private TextView name;
    private TextView email;
    private String emailToShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        String emailInt = intent.getStringExtra("email");
        int id = intent.getIntExtra("id", 0);
        eventsCount = findViewById(R.id.eventsNumber);
        friendsCount = findViewById(R.id.usersNumber);
        owns = findViewById(R.id.ownsNumber);
        email = findViewById(R.id.showEmailProfile);
        TextView goToChat = findViewById(R.id.goToChatProfile);
        name = findViewById(R.id.profileName);
        getUserData(emailInt);
        getEventsCount(id);
        getFriendsCount(id);
        getOwns(id);
        goToChat.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProfileActivity.this, ChatActivity.class);
            intent1.putExtra("id", id);
            startActivity(intent1);
        });
        email.setOnClickListener(v -> {
            email.setText(emailToShow);
        });
    }

    private void getFriendsCount(int id) {
        String url = MethodsAPI.getFriendsCount(id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    System.out.println("Response frie: " + response);
                    initializeFriendsCount(response.length());
                }, error -> {
                    System.out.println("Error: " + error);

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void initializeFriendsCount(int length) {
        Object friendsCountObject = length;
        friendsCount.setText(String.valueOf(friendsCountObject));
    }

    private void getOwns(int id) {
        String url = MethodsAPI.getOwns(id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    System.out.println("Response own: " + response);
                    initializeOwns(response.length());
                }, error -> {
                    System.out.println("Error: " + error);

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void initializeOwns(int length) {
        Object ownsObject = length;
        owns.setText(String.valueOf(ownsObject));
    }

    private void getEventsCount(int id) {
        String url = MethodsAPI.EventCount(id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    System.out.println("Response eve: " + response);
                    initializeEventsCount(response.length());
                }, error -> {
                    System.out.println("Error: " + error);

                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(jsonArrayRequest);
    }

    private void initializeEventsCount(int length) {
        Object eventsCountObject = length;
        eventsCount.setText(String.valueOf(eventsCountObject));
    }

    private void getUserData(String email) {
        String url = MethodsAPI.URL_GET_USER + "?s=" + email;
        System.out.println("url: " + url);
        System.out.println("auth: " + User.getAuthenticatedUser().getToken());
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println("Response data: " + response);
                try {
                    initializeDataUser(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } }, error -> {
                System.out.println("Error: " + error);
            } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }

    private void initializeDataUser(String response) throws JSONException {
        User user = User.getUserFromJson(response);
        System.out.println("user initialize: " + user.getName());
        String fullName = user.getName() + " " + user.getLastName();
        name.setText(fullName);
        emailToShow = user.getEmail();
    }
}