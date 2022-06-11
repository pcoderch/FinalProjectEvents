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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InsideChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private ChatsAdaptor adapter;
    private static String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_chats);
        int id = getIntent().getIntExtra("id", 0);
        userName = getIntent().getStringExtra("name");
        recyclerView = findViewById(R.id.chatsList);
        messages = new ArrayList<>();
        adapter = new ChatsAdaptor(messages);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getMessages(id);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addMessage(Message message) {
        messages.add(message);
        adapter.notifyDataSetChanged();
    }

    private void getMessages(int idOfUser) {
        String url = MethodsAPI.getMessages(idOfUser);
        messages = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String content = jsonObject.getString("content");
                    int user_id_send = jsonObject.getInt("user_id_send");
                    int user_id_recived = jsonObject.getInt("user_id_recived");
                    String timeStamp = jsonObject.getString("timeStamp");
                    Message message = new Message(id, content, user_id_send, user_id_recived, timeStamp);
                    addMessage(message);
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

        private final ArrayList<Message> messageList;

        private ChatsAdaptor(ArrayList<Message> userList) {
            this.messageList = userList;
        }

        @NonNull
        @Override
        public FriendHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new FriendHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(FriendHolder holder, int position) {
            Message message = messageList.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }
    }

    private class FriendHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Message message;
        private final TextView nameOfUserTextView;
        private final TextView lastMessageTextView;
        private final TextView timestampTextView;
        private AsyncTask<String, Void, Bitmap> imageView;


        public FriendHolder(LayoutInflater inflater, ViewGroup parent) {

            super(inflater.inflate(R.layout.element_c, parent, false));
            itemView.setOnClickListener(this);
            nameOfUserTextView = itemView.findViewById(R.id.chatOtherUserName);
            lastMessageTextView = itemView.findViewById(R.id.lastMessageTextViewChat);
            timestampTextView = itemView.findViewById(R.id.timeTextViewChat);
        }

        public void bind(Message message) {
            this.message = message;
            nameOfUserTextView.setText(userName);
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
            String url = MethodsAPI.getMessages(idOfUser);
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
