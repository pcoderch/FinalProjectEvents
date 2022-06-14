package com.pp2ex.finalprojectevents.Activities.SocialFeatures;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchUsersActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> usersSearch;
    private UsersAdaptor adapter;
    private Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_users_in_app);
        final ImageButton backButton = findViewById(R.id.arrowBackListAllUsers);
        recyclerView = findViewById(R.id.myConnectionsList);
        usersSearch = new ArrayList<>();
        adapter = new UsersAdaptor(usersSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String search = ((TextView)findViewById(R.id.searchUserEditText)).getText().toString();
            if(!search.isEmpty()) {
                searchUsers(search);
            } else {
                Toast.makeText(this, R.string.enterNamePlease, Toast.LENGTH_SHORT).show();
            }
        });
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUser(User user) {
        System.out.println("Adding user" + user.getEmail());
        usersSearch.add(user);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUserToSearched(User user, String search) {
        System.out.println("Adding user to searched" + user.getEmail());
        if (verifySearch(user, search) && user.getId() != User.getAuthenticatedUser().getId()) {
            usersSearch.add(user);
            adapter.notifyDataSetChanged();
        }
    }

    private boolean verifySearch(User user, String search) {
        return user.getName().equals(search) || user.getEmail().equals(search) || user.getLastName().equals(search);
    }

    private void searchUsers(String search) {
        usersSearch = new ArrayList<>();
        String url = MethodsAPI.getUserByString(search);
        System.out.println("Searching users with url: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                for(int i = 0; i < response.length(); i++) {
                    JSONObject user = response.getJSONObject(i);
                    User userToAdd = new User(user.getInt("id"), user.getString("name"), user.getString("last_name"), user.getString("email"), user.getString("password"), user.getString("image"));
                    addUserToSearched(userToAdd, search);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            updateUI();
        }, error -> {
            Toast.makeText(this, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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

    private void getFriendRequests() {
        String url = MethodsAPI.URL_FRIEND_REQUESTS;
        usersSearch = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String lastName = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    String image = jsonObject.getString("image");
                    User user = new User(id, name, lastName, email, "", image);
                    addUser(user);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            updateUI();
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

    private void updateUI() {
        adapter = new UsersAdaptor(usersSearch);
        for (int i = 0; i < usersSearch.size(); i++) {
            System.out.println(usersSearch.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class UsersAdaptor extends RecyclerView.Adapter<FriendHolder> {

        private final ArrayList<User> userList;

        private UsersAdaptor(ArrayList<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new FriendHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

    private class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User user;
        private boolean isFriend;
        private boolean alreadySentRequest;
        private ArrayList<User> friends;
        private ArrayList<User> friendRequests;
        private final TextView nameTextViewRequest;
        private final TextView emailTextViewRequest;
        private AsyncTask<String, Void, Bitmap> imageView;
        private final Button connectButton;

        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_user_i_can_request, parent, false));
            itemView.setOnClickListener(this);
            nameTextViewRequest = itemView.findViewById(R.id.nameTextView);
            emailTextViewRequest = itemView.findViewById(R.id.emailTextView);
            connectButton = itemView.findViewById(R.id.connectButton);
        }

        @SuppressLint("ResourceAsColor")
        public void bind(User user) {
            this.user = user;
            isFriend = false;
            alreadySentRequest = false;
            friends = new ArrayList<>();
            friendRequests = new ArrayList<>();
            nameTextViewRequest.setText(user.getName());
            emailTextViewRequest.setText(user.getEmail());
            imageView = new BitMapImage((ImageView) itemView.findViewById(R.id.IconImageView)).execute(user.getImage());
            getFriends(user.getId());
            getFriendRequests();
            connectButton.setOnClickListener(v -> {
                if (!user.isFriend() && !user.isRequested()) {
                    sendFriendRequest(user.getId());
                } else if (user.isFriend()){
                    Toast.makeText(getApplicationContext(), R.string.alreadyFriends, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.alreadySentRequestSt, Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void addFriendRequest(User user) {
            friendRequests.add(user);
        }

        private void getFriendRequests() {
            String url = MethodsAPI.URL_FRIEND_REQUESTS;
            System.out.println("Searching users requests with url: " + url);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                for (int i = 0; i < response.length(); i++) {
                    System.out.println("requests" + response);
                    try {
                        JSONObject jsonObject = response.getJSONObject(i);
                        int id = jsonObject.getInt("id");
                        String name = jsonObject.getString("name");
                        String lastName = jsonObject.getString("last_name");
                        String email = jsonObject.getString("email");
                        String image = jsonObject.getString("image");
                        User user = new User(id, name, lastName, email, "", image);
                        addFriendRequest(user);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                updateIfAlreadySentRequest();
            }, error -> {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            } ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                    return headers;
                }
            };
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }

        @SuppressLint({"ResourceAsColor", "UseCompatLoadingForColorStateLists"})
        private void updateIfAlreadySentRequest() {
            System.out.println("updating pending");
            for (int i = 0; i < friendRequests.size(); i++) {
                if (friendRequests.get(i).getId() == user.getId()) {
                    connectButton.setText(R.string.pending);
                    connectButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.yellow_claro));
                    user.setRequested(true);
                }
            }
        }

        private void sendFriendRequest(int id) {
            String url = MethodsAPI.sendFriendRequest(id);
            System.out.println("URL: " + url);
            @SuppressLint({"ResourceAsColor", "UseCompatLoadingForColorStateLists"}) StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                    response -> {
                        System.out.println("Response friend request: " + response);
                        Toast.makeText(getApplicationContext(), R.string.requestSent, Toast.LENGTH_SHORT).show();
                        connectButton.setText(R.string.pending);
                        connectButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.yellow_claro));
                        user.setRequested(true);
                    }, error -> {
                Toast.makeText(getApplicationContext(), R.string.alreadySentRequest, Toast.LENGTH_SHORT).show();
            } ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                    return params;
                }
            };
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);
        }

        @SuppressLint("UseCompatLoadingForColorStateLists")
        private void updateIsFriend() {
            connectButton.setText(R.string.friends);
            connectButton.setBackgroundTintList(getApplicationContext().getResources().getColorStateList(R.color.verde));
            user.setFriend(true);
            user.setRequested(false);
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
                        if (id == idUserToShow) {
                            updateIsFriend();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, error -> {
                Toast.makeText(getApplicationContext(), R.string.error, Toast.LENGTH_SHORT).show();
            } ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                    return headers;
                }
            };
            VolleySingleton.getInstance(getApplicationContext()).addToRequestQueue(request);
        }

        @Override
        public void onClick(View view) {
            Intent showProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            showProfile.putExtra("email", user.getEmail());
            showProfile.putExtra("id", user.getId());
            startActivity(showProfile);
        }

    }

}