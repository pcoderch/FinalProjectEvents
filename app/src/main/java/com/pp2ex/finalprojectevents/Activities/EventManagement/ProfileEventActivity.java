package com.pp2ex.finalprojectevents.Activities.EventManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.DataStructures.Comment;
import com.pp2ex.finalprojectevents.DataStructures.Event;
import com.pp2ex.finalprojectevents.DataStructures.Message;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ProfileEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int intentEventId;
    private int assistances;
    private Event event;
    //private EventAdaptor adapter;
    private AsyncTask<String, Void, Bitmap> profileImage;
    private TextView eventName;
    private TextView eventID;
    private TextView eventDescription;
    private TextView usersAttending;
    private TextView ownerName;
    private TextView startDate;
    private TextView endDate;
    private TextView rating;
    private TextView eventType;
    private TextView location;
    private ArrayList<Comment> comments;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        intentEventId = getIntent().getIntExtra("id", 0);
        recyclerView = findViewById(R.id.commentsInEvent);
        comments = new ArrayList<>();
        //adapter = new EventAdaptor(comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventName = findViewById(R.id.profileName);
        eventID = findViewById(R.id.eventsID);
        usersAttending = findViewById(R.id.participantsInEventNumber);
        ownerName = findViewById(R.id.ownsNumber);
        startDate = findViewById(R.id.DateStartEvent);
        endDate = findViewById(R.id.DateEndEvent);
        rating = findViewById(R.id.ratingEvent);
        eventType = findViewById(R.id.typeEvent);
        location = findViewById(R.id.locationEvent);
        getEventData(intentEventId);
        getEventAssistances(intentEventId);
        event.setRating(calculateRating());
        eventName.setText(event.getName());
        eventID.setText(String.valueOf(event.getId()));
        usersAttending.setText(String.valueOf(assistances));
        ownerName.setText(event.getOwnerId());
        startDate.setText(event.getStartDate());
        endDate.setText(event.getEndDate());
        rating.setText(String.valueOf(event.getRating()));
        eventType.setText(event.getType());
        location.setText(event.getLocation());
        usersAttending.setText(assistances + "/" + event.getNumOfParticipants());
        profileImage = new BitMapImage((ImageView) findViewById(R.id.IconImageView)).execute(event.getImage());
    }

    private int calculateRating() {
        assistances = 0;
        for (Comment c : comments) {
            assistances += c.getRating();
        }
        //return assistances / comments.size();
        return assistances == 0 ? 0 : assistances / comments.size();
    }

    private void getEventAssistances(int intentEventId) {
        String url = MethodsAPI.getEventAssistances(intentEventId);
        System.out.println("URL GET ASSISTANCES: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String last_name = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    int rating = jsonObject.getInt("puntuation");
                    String comment = jsonObject.getString("commentary");
                    Comment commentToAdd = new Comment(id, name, last_name, email, rating, comment);
                    addComment(commentToAdd);
                    assistances = response.length();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("not entering the loop");
        }, error -> {
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addComment(Comment commentToAdd) {
        if (commentToAdd.getComment() != null) {
            comments.add(commentToAdd);
            //adapter.notifyDataSetChanged();
        }
    }

    private void getEventData(int eventID) {
        String url = MethodsAPI.getEventById(eventID);
        System.out.println("URL GET EVENT: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    int owner_id = jsonObject.getInt("owner_id");
                    String image = jsonObject.getString("image");
                    String location = jsonObject.getString("location");
                    String description = jsonObject.getString("description");
                    String start_date = jsonObject.getString("eventStart_date");
                    String end_date = jsonObject.getString("eventEnd_date");
                    int n_participators = jsonObject.getInt("n_participators");
                    String type = jsonObject.getString("type");
                    event = new Event(name, description, start_date, end_date, n_participators, image, location, type);
                    event.setOwnerId(owner_id);
                    event.setId(id);
                } catch (JSONException e) {
                    System.out.println("Error json get user data: " + e.getMessage());
                    e.printStackTrace();
                }
            }
            System.out.println("not entering the loop user data");
        }, error -> {
            System.out.println("ERRORRRR get user data");
            Toast.makeText(this, R.string.error, Toast.LENGTH_SHORT).show();
        }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }


    /*private void updateUI() {
        adapter = new EventAdaptor(comments);
        for (int i = 0; i < usersSearch.size(); i++) {
            System.out.println(usersSearch.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }

    private class EventAdaptor extends RecyclerView.Adapter<EventHolder> {

        private final ArrayList<Comment> comments;

        private EventAdaptor(ArrayList<Comment> comments) {
            this.comments = comments;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Comment comment = comments.get(position);
            holder.bind(comment);
        }

        @Override
        public int getItemCount() {
            return comments.size();
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Comment commnent;
        private boolean isFriend;
        private boolean alreadySentRequest;
        private final TextView nameTextViewRequest;
        private final TextView emailTextViewRequest;
        private AsyncTask<String, Void, Bitmap> imageView;

        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_user_i_can_request, parent, false));
            itemView.setOnClickListener(this);
            nameTextViewRequest = itemView.findViewById(R.id.nameTextView);
            emailTextViewRequest = itemView.findViewById(R.id.emailTextView);
            connectButton = itemView.findViewById(R.id.connectButton);
        }

        @SuppressLint("ResourceAsColor")
        public void bind(Comment comment) {
            this.user = user;
            isFriend = false;
            alreadySentRequest = false;
            friends = new ArrayList<>();
            friendRequests = new ArrayList<>();
            nameTextViewRequest.setText(user.getName());
            emailTextViewRequest.setText(user.getEmail());


        @Override
        public void onClick(View view) {
            Intent showProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            showProfile.putExtra("email", user.getEmail());
            showProfile.putExtra("id", user.getId());
            startActivity(showProfile);
        }

    }*/

}