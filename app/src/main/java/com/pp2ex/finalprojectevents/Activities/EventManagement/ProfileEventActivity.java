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
import android.widget.EditText;
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
import java.util.Objects;

public class ProfileEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private int intentEventId;
    private int assistances;
    private String imageUser;
    private boolean gotData;
    private boolean alreadyJoined, notInTheEvent;
    private Event event;
    private EventAdaptor adapter;
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
    private Button joinEvent;
    private Button leaveEvent;
    private Button getAttendants;
    private Button backButton;
    private ArrayList<Comment> comments;
    private AsyncTask<String, Void, Bitmap> profileImage;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event);
        intentEventId = getIntent().getIntExtra("id", 0);
        recyclerView = findViewById(R.id.commentsInEvent);
        comments = new ArrayList<>();
        adapter = new EventAdaptor(comments);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        eventName = findViewById(R.id.profileName);
        eventDescription = findViewById(R.id.EventDescription);
        eventID = findViewById(R.id.eventsID);
        usersAttending = findViewById(R.id.participantsInEventNumber);
        ownerName = findViewById(R.id.ownsNumber);
        startDate = findViewById(R.id.DateStartEvent);
        endDate = findViewById(R.id.DateEndEvent);
        rating = findViewById(R.id.ratingEvent);
        eventType = findViewById(R.id.EventType);
        location = findViewById(R.id.locationEvent);
        joinEvent = findViewById(R.id.joinEventButton);
        leaveEvent = findViewById(R.id.dropEventButton);
        getAttendants = findViewById(R.id.getAttendantsButton);
        backButton = findViewById(R.id.backButtonEvent);
        gotData = false;
        event = new Event("name", "esto es una desc", "25/07", "25/08", 15, "image", "barcelona", "type");
        getEventData(intentEventId);
        getEventAssistances(intentEventId);
        backButton.setOnClickListener(v -> finish());
        joinEvent.setOnClickListener(v -> {
            joinTheEvent();
        });

        leaveEvent.setOnClickListener(v -> {
            dropTheEvent();
        });
        getAttendants.setOnClickListener(v -> {
            Intent goToAttendants = new Intent(ProfileEventActivity.this, AttendantsEventActivity.class);
            goToAttendants.putExtra("id", intentEventId);
            startActivity(goToAttendants);
        });
    }

    private void joinTheEvent() {
        String url = MethodsAPI.getEventAssistances(intentEventId);
        System.out.println("URL: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.POST, url, null, response -> {
            joinEvent.setText(R.string.joined);
        }, error -> {
            System.out.println("ERRORRRR get user data");
            joinEvent.setText(R.string.joined);
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

    private void dropTheEvent() {
        String url = MethodsAPI.getEventAssistances(intentEventId);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.DELETE, url, null, response -> {
            Toast.makeText(this, R.string.dropped, Toast.LENGTH_SHORT).show();
            joinEvent.setText(R.string.join);
        }, error -> {
            joinEvent.setText(R.string.join);
            System.out.println("error");
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

    @SuppressLint("SetTextI18n")
    private void initializeData() {
        event.setRating(calculateRating());
        eventName.setText(event.getName());
        eventID.setText(String.valueOf(intentEventId));
        usersAttending.setText(String.valueOf(assistances));
        startDate.setText(event.getStartDate());
        endDate.setText(event.getEndDate());
        eventDescription.setText(event.getDescription());
        rating.setText(String.valueOf(event.getRating()));
        eventType.setText(event.getType());
        location.setText(event.getLocation());
        usersAttending.setText(assistances + "/" + event.getNumOfParticipants());
        profileImage = new BitMapImage(findViewById(R.id.eventImage)).execute(event.getImage());
        System.out.println("id of event: " + event.getId());
    }

    private void getEventData(int eventID) {
        String url = MethodsAPI.getEventById(eventID);
        System.out.println("URL GET EVENT: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            System.out.println("aaaaa" + response);
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    int owner_id = jsonObject.getInt("owner_id");
                    String image = jsonObject.getString("image");
                    String location = jsonObject.getString("location");
                    String description = jsonObject.getString("description");
                    String start_date = jsonObject.getString("eventStart_date");
                    String end_date = jsonObject.getString("eventEnd_date");
                    int n_participators = jsonObject.getInt("n_participators");
                    String type = jsonObject.getString("type");
                    event = new Event(name, description, dateFormat(start_date), dateFormat(end_date), n_participators, image, location, type);
                    setEvent();
                    initializeData();
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

    private String dateFormat(String date) {
        try {
            return date.substring(0, 10);
        } catch (Exception e) {
            return "unknown";
        }
    }

    private void setEvent() {
        gotData = true;
    }

    private int calculateRating() {
        int assistancesRating = 0;
        int count = 0;
        for (Comment c : comments) {
            if (c.getRating()!=null && !Objects.equals(c.getRating(), "null")) {
                assistancesRating += Integer.parseInt(c.getRating());
                count++;
            }
        }
        return assistancesRating == 0 ? 0 : assistancesRating / count;
    }

    private void getEventAssistances(int intentEventId) {
        String url = MethodsAPI.getEventAssistances(intentEventId);
        System.out.println("URL GET ASSISTANCES: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            assistances = response.length();
            System.out.println("response length: " + response.length());
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String last_name = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    String rating = jsonObject.getString("puntuation");
                    String comment = jsonObject.getString("comentary");
                    Comment commentToAdd = new Comment(id, name, last_name, email, rating, comment);
                    String image = "";
                    image = getImageOfUser(id);
                    commentToAdd.setImage(image);
                    if (i == 0) {
                        ownerName.setText(name);
                    }
                    addComment(commentToAdd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            updateUI();
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

    private String getImageOfUser(int id) {
        String url = MethodsAPI.getUserData(id);
        System.out.println("URL GET USER: " + url);
        StringRequest request = new StringRequest(Request.Method.GET, url, response -> {
            System.out.println("response: " + response);
            try {
                response.substring(0, response.indexOf("}") + 1);
                JSONObject jsonObject = new JSONObject(response);
                imageUser = jsonObject.getString("image");
            } catch (JSONException e) {
                e.printStackTrace();
            }
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
        return imageUser;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addComment(Comment commentToAdd) {
        if (commentToAdd.getComment() != null && !commentToAdd.getComment().equals("null")) {
            comments.add(commentToAdd);
            adapter.notifyDataSetChanged();
        }
    }

    private void updateUI() {
        adapter = new EventAdaptor(comments);
        for (int i = 0; i < comments.size(); i++) {
            System.out.println(comments.get(i).getComment());
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

        private Comment comment;
        private boolean isFriend;
        private boolean alreadySentRequest;
        private AsyncTask<String, Void, Bitmap> profileImage;
        private final TextView nameTextViewComment;
        private final TextView ratingTextViewComment;
        private final EditText commentCommentEditText;

        private AsyncTask<String, Void, Bitmap> imageView;

        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_comment, parent, false));
            itemView.setOnClickListener(this);
            nameTextViewComment = itemView.findViewById(R.id.nameTextViewComment);
            ratingTextViewComment = itemView.findViewById(R.id.ratingComment);
            commentCommentEditText = itemView.findViewById(R.id.CommentCommentView);
        }

        @SuppressLint({"ResourceAsColor", "SetTextI18n"})
        public void bind(Comment comment) {
            this.comment = comment;
            nameTextViewComment.setText(comment.getName() + " " + comment.getLast_name());
            ratingTextViewComment.setText(String.valueOf(comment.getRating()));
            commentCommentEditText.setText(comment.getComment());
            //profileImage  = new BitMapImage(findViewById(R.id.iconImageViewComment)).execute(event.getImage());
        }


        @Override
        public void onClick(View view) {
            System.out.println("Clicked");
        }

    }

}