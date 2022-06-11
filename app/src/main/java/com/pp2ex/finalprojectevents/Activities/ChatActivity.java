package com.pp2ex.finalprojectevents.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.Message;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> chats;
    private ChatsAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chats);
        recyclerView = findViewById(R.id.chatsList);
        chats = new ArrayList<>();
        adapter = new ChatsAdaptor(chats);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getChats();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUser(User user) {
        chats.add(user);
        adapter.notifyDataSetChanged();
    }

    private void getChats() {
        String url = MethodsAPI.URL_CHATS;
        chats = new ArrayList<>();
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
        adapter = new ChatsAdaptor(chats);
        for (int i = 0; i < chats.size(); i++) {
            System.out.println(chats.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class ChatsAdaptor extends RecyclerView.Adapter<FriendHolder> {

        private final ArrayList<User> userList;

        private ChatsAdaptor(ArrayList<User> userList) {
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
        private final TextView nameTextView;
        private final TextView lastMessageTextView;
        private final TextView timestampTextView;
        private AsyncTask<String, Void, Bitmap> imageView;


        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_chat, parent, false));
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameTextViewChat);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextViewChat);
            timestampTextView = itemView.findViewById(R.id.timeTextViewChat);
        }

        public void bind(User user) {
            this.user = user;
            nameTextView.setText(user.getName());
            getLastMessage(user.getId());
            imageView = new BitMapImage((ImageView) itemView.findViewById(R.id.iconImageViewChat)).execute(user.getImage());
        }

        @Override
        public void onClick(View view) {
            Intent showProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            showProfile.putExtra("email", user.getEmail());
            showProfile.putExtra("id", user.getId());
            showProfile.putExtra("image", user.getImage());
            startActivity(showProfile);
        }

        private String getContentMessage(String content) {
            if (content.length() > 20) {
                return content.substring(0, 19) + "...";
            } else {
                return content;
            }
        }

        private String getLastMessageTime(String lastMessageOfAll) {
            if (lastMessageOfAll.equals("")) {
                return "";
            } else {
                return lastMessageOfAll.substring(11, 16);
            }
        }

        private Drawable LoadImageFromWebOperations(String url) {
            try {
                InputStream is = (InputStream) new URL(url).getContent();
                return Drawable.createFromStream(is, "src name");
            } catch (Exception e) {
                return null;
            }
        }

        private void getLastMessage(int idOfUser) {
            String url = MethodsAPI.getLastMessage(idOfUser);
            JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
                if (response.length() == 0) {
                    lastMessageTextView.setText("");
                    timestampTextView.setText("");
                } else {
                    try {
                        JSONObject jsonObject = response.getJSONObject(response.length() - 1);
                        String content = jsonObject.getString("content");
                        String time = jsonObject.getString("timeStamp");
                        lastMessageTextView.setText(getContentMessage(content));
                        timestampTextView.setText(getLastMessageTime(time));
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
    }


}
