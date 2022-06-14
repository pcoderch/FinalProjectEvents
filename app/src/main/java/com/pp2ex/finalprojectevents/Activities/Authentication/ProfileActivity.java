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
    private String emailToShow;
    private Button addConnection;
    private ImageView chatImage;
    private ArrayList<User> friends;
    private String emailInt;
    private String imageURL;
    private boolean isFriend;
    private int id;
    private Button backButton;

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
        if (id == 0) {
            id = User.getAuthenticatedUser().getId();
            emailInt = User.getAuthenticatedUser().getEmail();
            imageURL = User.getAuthenticatedUser().getImage();
            addConnection.setText(R.string.editProfile);
            goToChat.setVisibility(View.GONE);
            chatImage.setVisibility(View.GONE);
        }
        profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute(imageURL);
        try {
            if (profilePicture.get() == null) {
                profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute("https://cdn.icon-icons.com/icons2/1378/PNG/512/avatardefault_92824.png");
            }
        } catch (Exception e) {
            e.printStackTrace();
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
        System.out.println("is updating");
        emailInt = User.getAuthenticatedUser().getEmail();
        imageURL = User.getAuthenticatedUser().getImage();
        profilePicture = new BitMapImage(findViewById(R.id.profileImage)).execute(imageURL);
    }


    private void removeFriend(int id) {
        System.out.println("Id: " + id);
        String url = MethodsAPI.removeFriend(id);
        @SuppressLint("ResourceAsColor") StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
            System.out.println("Friend deleted");
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

    @SuppressLint({"ResourceAsColor", "UseCompatLoadingForDrawables"})
    private void setIfNecessaryUpdate(int id) {
        System.out.println("ID if necessary: " + id);
        if(updateIfFriend(id)){
            System.out.println("Is friend");
            addConnection.setText(R.string.friends);
            addConnection.setBackgroundColor(R.color.verde);
            isFriend = true;
        }
    }

    @SuppressLint("ResourceAsColor")
    private boolean updateIfFriend(int id) {
        for (User user : friends) {
            System.out.println("idddd: " + user.getId());
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
                System.out.println("myyyyFriend: " + response);
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
        System.out.println("URL: " + url);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String message = jsonObject.getString("id");
                        System.out.println("Response friend request: " + response);
                        Toast.makeText(ProfileActivity.this, R.string.requestSent, Toast.LENGTH_SHORT).show();
                        addConnection.setText(R.string.pending);
                    } catch (JSONException e) {
                        Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }, error -> {
            Toast.makeText(ProfileActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
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