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
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.Activities.Authentication.SignInActivity;
import com.pp2ex.finalprojectevents.DataStructures.Event;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ShowEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Event> events;
    private MyEventsAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_my_events);
        final ImageButton backButton = findViewById(R.id.arrowBackMyEvents);
        recyclerView = findViewById(R.id.MyEventslist);
        events = new ArrayList<>();
        adapter = new MyEventsAdapter(events);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getEvents();
        backButton.setOnClickListener(v -> {
            finish();
        });
    }


    @SuppressLint("NotifyDataSetChanged")
    private void addEvent(Event event) {
        if (event.getOwnerId() == User.getAuthenticatedUser().getId()) {
            System.out.println("Adding event" + event.getOwnerId());
            events.add(event);
            adapter.notifyDataSetChanged();
        }
    }

    private void getEvents() {
        String url = MethodsAPI.URL_EVENTS_BEST;
        System.out.println("Searching EVENTS with url: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                System.out.println("weee");
                for(int i = 0; i < response.length(); i++) {
                    JSONObject event = response.getJSONObject(i);
                    Event eventToAdd = new Event(event.getString("name"), event.getString("description"), event.getString("eventStart_date"), event.getString("eventEnd_date"), event.getInt("n_participators"), event.getString("image"), event.getString("location"), event.getString("type"));
                    System.out.println("The event is: " + eventToAdd.getName());
                    eventToAdd.setId(event.getInt("id"));
                    eventToAdd.setOwnerId(event.getInt("owner_id"));
                    System.out.println("Event owner id: " + eventToAdd.getOwnerId());
                    //eventToAdd.setRating(event.getInt("avg_score"));
                    addEvent(eventToAdd);
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

    private void updateUI() {
        adapter = new MyEventsAdapter(events);
        for (int i = 0; i < events.size(); i++) {
            System.out.println(events.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class MyEventsAdapter extends RecyclerView.Adapter<MyEventHolder> {

        private final ArrayList<Event> eventsList;

        private MyEventsAdapter(ArrayList<Event> eventsList) {
            this.eventsList = eventsList;
        }

        @NonNull
        @Override
        public MyEventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new MyEventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(MyEventHolder holder, int position) {
            Event event = eventsList.get(position);
            holder.bind(event);
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }
    }

    private class MyEventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Event event;
        private final TextView nameTextView;
        private final TextView eventTypeTextView;
        private Button editEvent;
        private AsyncTask<String, Void, Bitmap> profileImage;

        public MyEventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_my_event, parent, false));
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameEventEventView);
            eventTypeTextView = itemView.findViewById(R.id.typeTextView);
            editEvent = itemView.findViewById(R.id.editButton);
        }

        public void bind(Event event) {
            this.event = event;
            nameTextView.setText(event.getName());
            eventTypeTextView.setText(event.getType());
            profileImage = new BitMapImage((ImageView) itemView.findViewById(R.id.IconImageView)).execute(event.getImage());
            editEvent.setOnClickListener(v -> {
                Intent goEditEvent = new Intent(getApplicationContext(), EditEventActivity.class);
                goEditEvent.putExtra("id", event.getId());
                startActivity(goEditEvent);
            });
        }

        @Override
        public void onClick(View view) {
            Intent showProfileEvent = new Intent(getApplicationContext(), ProfileEventActivity.class);
            System.out.println("Event id after click: " + event.getId());
            showProfileEvent.putExtra("id", event.getId());
            startActivity(showProfileEvent);
        }

    }

}