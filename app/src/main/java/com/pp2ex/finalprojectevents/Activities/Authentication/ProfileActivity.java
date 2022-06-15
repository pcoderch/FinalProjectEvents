package com.pp2ex.finalprojectevents.Activities.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Chat.ChatActivity;
import com.pp2ex.finalprojectevents.Activities.Chat.InsideChatActivity;
import com.pp2ex.finalprojectevents.Activities.EventManagement.ShowEventsActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {

    private AsyncTask<String, Void, Bitmap> profilePicture;
    private TextView eventsCount;
    private TextView friendsCount;
    private TextView owns;
    private TextView name;
    private TextView email;
    private TextView goToChat;
    private TextView messageConnection;
    private String emailToShow;
    private Button addConnection;
    private Button myEvents;
    private Button backButton;
    private ImageView chatImage;
    private ArrayList<User> friends;
    private String emailInt;
    private String imageURL;
    private boolean isFriend;
    private int id;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile);
        Intent intent = getIntent();
        emailInt = intent.getStringExtra("email");
        id = intent.getIntExtra("id", 0);
        imageURL = intent.getStringExtra("image");
        isFriend = false;
        friends = new ArrayList<>();
        eventsCount = findViewById(R.id.eventsNumber);
        friendsCount = findViewById(R.id.usersNumber);
        owns = findViewById(R.id.ownsNumber);
        email = findViewById(R.id.showEmailProfile);
        goToChat = findViewById(R.id.goToChatProfile);
        name = findViewById(R.id.profileName);
        addConnection = findViewById(R.id.addConnectionButton);
        chatImage = findViewById(R.id.chatImage);
        backButton = findViewById(R.id.BackFromProfile);
        messageConnection = findViewById(R.id.messageConnection);
        myEvents = findViewById(R.id.eventsButton);
        myEvents.setVisibility(View.GONE);
        if (id == User.getAuthenticatedUser().getId()) {
            id = User.getAuthenticatedUser().getId();
            emailInt = User.getAuthenticatedUser().getEmail();
            imageURL = User.getAuthenticatedUser().getImage();
            addConnection.setText(R.string.editProfile);
            messageConnection.setVisibility(View.GONE);
            goToChat.setVisibility(View.GONE);
            chatImage.setVisibility(View.GONE);
            myEvents.setVisibility(View.VISIBLE);
        }
        profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute(imageURL);
        try {
            if (profilePicture.get() == null) {
                profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute("https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(id == User.getAuthenticatedUser().getId()){
            addConnection.setText(R.string.editProfile);
            chatImage.setVisibility(View.GONE);
            goToChat.setVisibility(View.GONE);
        }
        getUserData(emailInt);
        getEventsCount(id);
        getFriendsCount(id);
        getOwns(id);
        getFriends(id);
        int finalId = id;
        addConnection.setOnClickListener(v -> {
            if (finalId == User.getAuthenticatedUser().getId()) {
                Intent editProfile = new Intent(ProfileActivity.this, EditProfileActivity.class);
                startActivity(editProfile);
            } else {
                if(isFriend) {
                    Toast.makeText(ProfileActivity.this, R.string.alreadyFriends, Toast.LENGTH_SHORT).show();
                } else {
                    sendFriendRequest(finalId);
                }
            }
        });
        goToChat.setOnClickListener(v -> {
            Intent intent1 = new Intent(ProfileActivity.this, InsideChatActivity.class);
            intent1.putExtra("id", finalId);
            intent1.putExtra("name", name.getText().toString());
            startActivity(intent1);
        });
        email.setOnClickListener(v -> {
            email.setText(emailToShow);
        });
        backButton.setOnClickListener(v -> {
            finish();
        });
        myEvents.setOnClickListener(view -> {
            Intent goShowEvents = new Intent(ProfileActivity.this, ShowEventsActivity.class);
            goShowEvents.putExtra("id", finalId);
            startActivity(goShowEvents);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        int id = getIntent().getIntExtra("id", 0);
        if (id == 0) {
            updateProfile();
        }
    }

    private void updateProfile() {
        emailInt = User.getAuthenticatedUser().getEmail();
        imageURL = User.getAuthenticatedUser().getImage();
        profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute(imageURL);
    }


    private void removeFriend(int id) {
        String url = MethodsAPI.removeFriend(id);
        @SuppressLint("ResourceAsColor") StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            Toast.makeText(ProfileActivity.this, R.string.removedFriend, Toast.LENGTH_SHORT).show();
            addConnection.setText(R.string.addConnection);
            addConnection.setBackgroundColor(R.color.azul_claro);
            isFriend = false;
        }, error -> {
            Toast.makeText(ProfileActivity.this, R.string.errorRemovingFriend, Toast.LENGTH_SHORT).show();
        } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
        getOwns(id);
    }

    private void addFriendToList(User user) {
        friends.add(user);
    }

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables", "UseCompatLoadingForColorStateLists"})
    private void setIfNecessaryUpdate(int id) {
        if(updateIfFriend(id)){
            addConnection.setText(R.string.friends);
            addConnection.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.verde));
            isFriend = true;
        }
    }

    @SuppressLint("ResourceAsColor")
    private boolean updateIfFriend(int id) {
        for (User user : friends) {
            if (user.getId() == id) {
                return true;
            }
        }
        return false;
    }

    private void getFriends(int idUserToShow) {
        String url = MethodsAPI.URL_FRIENDS;
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String lastName = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    String image = jsonObject.getString("image");
                    User user = new User(id, name, lastName, email, "", image);
                    System.out.println("added " + user);
                    friends.add(user);
                    addFriendToList(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            setIfNecessaryUpdate(idUserToShow);
        }, error -> {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void sendFriendRequest(int id) {
        String url = MethodsAPI.sendFriendRequest(id);
        @SuppressLint("UseCompatLoadingForColorStateLists") StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    Toast.makeText(ProfileActivity.this, R.string.requestSent, Toast.LENGTH_SHORT).show();
                    addConnection.setText(R.string.pending);
                    addConnection.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.yellow_claro));
                }, error -> {
            Toast.makeText(ProfileActivity.this, R.string.alreadySentRequestSt, Toast.LENGTH_SHORT).show();
        } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                params.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


    private void getFriendsCount(int id) {
        String url = MethodsAPI.getFriendsCount(id);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest
                (Request.Method.GET, url, null, response -> {
                    initializeFriendsCount(response.length());
                }, error -> {
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
                    initializeOwns(response.length());
                }, error -> {
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
                    initializeEventsCount(response.length());
                }, error -> {
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
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    initializeDataUser(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } }, error -> {
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
        String fullName = user.getName() + " " + user.getLastName();
        name.setText(fullName);
        emailToShow = user.getEmail();
    }
}