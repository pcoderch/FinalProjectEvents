package com.pp2ex.finalprojectevents.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.Image;
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
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FriendRequestActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> friendRequests;
    private FriendsAdaptor adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_follower_request);
        recyclerView = findViewById(R.id.listFriendsRequest);
        friendRequests = new ArrayList<>();
        adapter = new FriendsAdaptor(friendRequests);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getFriendRequests();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUser(User user) {
        System.out.println("Adding user" + user.getEmail());
        friendRequests.add(user);
        adapter.notifyDataSetChanged();
    }

    private void getFriendRequests() {
        String url = MethodsAPI.URL_FRIEND_REQUESTS;
        friendRequests = new ArrayList<>();
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
        adapter = new FriendsAdaptor(friendRequests);
        for (int i = 0; i < friendRequests.size(); i++) {
            System.out.println(friendRequests.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class FriendsAdaptor extends RecyclerView.Adapter<FriendHolder> {

        private final ArrayList<User> userList;

        private FriendsAdaptor(ArrayList<User> userList) {
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
        private final TextView nameTextViewRequest;
        private final TextView emailTextViewRequest;
        private final ImageButton acceptButton;
        private final ImageButton rejectButton;
        private AsyncTask<String, Void, Bitmap> profileImageView;

        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_request, parent, false));
            itemView.setOnClickListener(this);
            nameTextViewRequest = itemView.findViewById(R.id.nameTextViewRequest);
            emailTextViewRequest = itemView.findViewById(R.id.emailTextViewRequest);
            acceptButton = itemView.findViewById(R.id.acceptRequest);
            rejectButton = itemView.findViewById(R.id.cancelrequest);
        }

        public void bind(User user) {
            this.user = user;
            nameTextViewRequest.setText(user.getName());
            emailTextViewRequest.setText(user.getEmail());
            profileImageView = new BitMapImage(findViewById(R.id.IconImageViewRequest)).execute(user.getImage());
            acceptButton.setOnClickListener(v -> {
                acceptRequest(user.getId());
            });
            rejectButton.setOnClickListener(v -> {
                rejectRequest(user.getId());
            });
        }

        private void rejectRequest(int id) {
            String url = MethodsAPI.answerRequest(id);
            StringRequest request = new StringRequest(Request.Method.DELETE, url, response -> {
                Toast.makeText(getApplicationContext(), R.string.request_rejected, Toast.LENGTH_SHORT).show();
                getFriendRequests();
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

        private void acceptRequest(int id) {
            String url = MethodsAPI.answerRequest(id);
            StringRequest request = new StringRequest(Request.Method.PUT, url, response -> {
                Toast.makeText(getApplicationContext(), R.string.request_accepted, Toast.LENGTH_SHORT).show();
                getFriendRequests();
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