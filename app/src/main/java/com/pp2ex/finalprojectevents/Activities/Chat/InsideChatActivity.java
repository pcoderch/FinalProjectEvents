package com.pp2ex.finalprojectevents.Activities.Chat;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.Message;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class InsideChatActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Message> messages;
    private MessageAdaptor adapter;
    private TextView userName;
    private EditText messageToSend;
    private Button sendMessage;
    private Button backButton;
    private boolean threadRunning;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_one_chat);
        messages = new ArrayList<>();
        userName = findViewById(R.id.chatOtherUserName);
        messageToSend = findViewById(R.id.sendMessage);
        sendMessage = findViewById(R.id.sendMessageButton);
        backButton = findViewById(R.id.backButtonOneChat);
        int id = getIntent().getIntExtra("id", 0);
        userName.setText(getIntent().getStringExtra("name"));
        recyclerView = findViewById(R.id.MessagesInChat);
        adapter =    new MessageAdaptor(messages);
        threadRunning = true;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getMessages(id);
        sendMessage.setOnClickListener(v -> {
            String content = messageToSend.getText().toString();
            if (!content.isEmpty()) {
                sendMessageToChat(content, User.getAuthenticatedUser().getId(), id);
            } else {
                Toast.makeText(InsideChatActivity.this, R.string.empty_message, Toast.LENGTH_SHORT).show();
            }
        });
        Thread thread = new Thread(() -> {
            while (threadRunning) {
                try {
                    Thread.sleep(950);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                getMessages(id);
            }
        });
        thread.start();
        backButton.setOnClickListener(v -> {
            threadRunning = false;
            finish();
        });
    }

    private JSONObject insertJsonBody(String content, int senderId, int receiverId) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("content", content);
            jsonObject.put("user_id_send", senderId);
            jsonObject.put("user_id_recived", receiverId);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    private void sendMessageToChat(String content, int senderId, int receiverId) {
        String url = MethodsAPI.URL_MESSAGES;
        JSONObject jsonBody = insertJsonBody(content, senderId, receiverId);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, jsonBody, response -> {
            Toast.makeText(InsideChatActivity.this, R.string.message_sent, Toast.LENGTH_SHORT).show();
            messageToSend.setText("");
            getMessages(receiverId);
        }, error -> {
            Toast.makeText(InsideChatActivity.this, R.string.error, Toast.LENGTH_SHORT).show();
        } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(InsideChatActivity.this).addToRequestQueue(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addMessage(Message message) {
        if(!messages.contains(message)){
            messages.add(message);
            adapter.notifyDataSetChanged();
        }
    }

    private void getMessages(int idOfUser) {
        String url = MethodsAPI.getMessages(idOfUser);
        int messagesSize = messages.size();
        messages = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
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
            if (messagesSize != messages.size()) {
                updateUI();
            }
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
        recyclerView.scrollToPosition(messages.size() - 1);
        adapter = new MessageAdaptor(messages);
        recyclerView.setAdapter(adapter);
    }
    private class MessageAdaptor extends RecyclerView.Adapter<MessagesHolder> {

        private final ArrayList<Message> messageList;
        private boolean sentByMe;

        private MessageAdaptor(ArrayList<Message> userList) {
            this.messageList = userList;
        }

        @NonNull
        @Override
        public MessagesHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View typeOfMessage;
            if (viewType == 1) { //sent by me
                typeOfMessage = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_my_message, parent, false);
            } else { //sent by the other user
                typeOfMessage = LayoutInflater.from(parent.getContext()).inflate(R.layout.element_her_message, parent, false);
            }
            return new MessagesHolder(typeOfMessage);
        }

        @Override
        public void onBindViewHolder(MessagesHolder holder, int position) {
            Message message = messageList.get(position);
            holder.bind(message);
        }

        @Override
        public int getItemCount() {
            return messageList.size();
        }

        @Override
        public int getItemViewType(int position) {
            return messageList.get(position).getSenderId() == User.getAuthenticatedUser().getId() ? 1 : 0;
        }
    }

    private class MessagesHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Message message;
        private final TextView messageContent;
        private final TextView timestampTextView;
        private AsyncTask<String, Void, Bitmap> imageView;

        public MessagesHolder(View typeOfMessage) {
            super(typeOfMessage);
            itemView.setOnClickListener(this);
            messageContent = typeOfMessage.findViewById(R.id.messageContent);
            timestampTextView = itemView.findViewById(R.id.messageTimestamp);
        }

        public void bind(Message message) {
            this.message = message;
            messageContent.setText(message.getContent());
            timestampTextView.setText(getMessageTime(message.getTimestamp()));
        }

        private String getMessageTime(String lastMessageOfAll) {
            if (lastMessageOfAll.equals("")) {
                return "";
            } else {
                return lastMessageOfAll.substring(11, 16);
            }
        }

        @Override
        public void onClick(View view) {
            System.out.println("Clicked");
        }

    }

}
