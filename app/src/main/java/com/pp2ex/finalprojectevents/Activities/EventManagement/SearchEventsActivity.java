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
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.BitMapImage;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.DataStructures.Event;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class SearchEventsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<Event> eventsSearch;
    private EventAdaptor adapter;
    private Button searchButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_all_events);
        recyclerView = findViewById(R.id.AllEventsView);
        eventsSearch = new ArrayList<>();
        adapter = new EventAdaptor(eventsSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(v -> {
            String[] search = new String[3];
            search[0] = ((TextView)findViewById(R.id.searchEventName)).getText().toString();
            search[1] = ((TextView)findViewById(R.id.searchEventLocation)).getText().toString();
            search[2] = ((TextView)findViewById(R.id.searchEventDate)).getText().toString();
            String url = getUrl(search);
            if(!search[0].isEmpty() || !search[1].isEmpty() || !search[2].isEmpty()) {
                searchEvents(url, search);
            }
        });
    }

    private String getUrl(String[] search) {
        String url = MethodsAPI.URL_EVENTS_SEARCH;
        url = url + "?";
        int count = 0;
        if(!search[0].isEmpty()) {
            url += "keyword=" + search[0];
            count++;
        }
        if(!search[1].isEmpty()) {
            if (count > 0) {
                url += "&";
            }
            url += "location=" + search[1];
        }
        if(!search[2].isEmpty()) {
            if (count > 0) {
                url += "&";
            }
            url += "date=" + search[2];
        }
        return url;
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUser(Event event) {
        System.out.println("Adding user" + event.getName());
        eventsSearch.add(event);
        adapter.notifyDataSetChanged();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addEventToSearched(Event event, String search, String date) {
        System.out.println("Adding user to searched" + event.getName());
        eventsSearch.add(event);
        adapter.notifyDataSetChanged();
    }

    private void searchEvents(String url, String[] search) {
        eventsSearch = new ArrayList<>();
        System.out.println("Searching users with url: " + url);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            try {
                System.out.println("weee");
                for(int i = 0; i < response.length(); i++) {
                    JSONObject event = response.getJSONObject(i);
                    Event eventToAdd = new Event(event.getString("name"), event.getString("description"), event.getString("eventStart_date"), event.getString("eventEnd_date"), event.getInt("n_participators"), event.getString("image"), event.getString("location"), event.getString("type"));
                    eventToAdd.setId(event.getInt("id"));
                    System.out.println("The event is: " + eventToAdd.getName());
                    addEventToSearched(eventToAdd, search[2], event.getString("date"));
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
        adapter = new EventAdaptor(eventsSearch);
        for (int i = 0; i < eventsSearch.size(); i++) {
            System.out.println(eventsSearch.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class EventAdaptor extends RecyclerView.Adapter<EventHolder> {

        private final ArrayList<Event> eventsList;

        private EventAdaptor(ArrayList<Event> eventsList) {
            this.eventsList = eventsList;
        }

        @NonNull
        @Override
        public EventHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new EventHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(EventHolder holder, int position) {
            Event event = eventsList.get(position);
            holder.bind(event);
        }

        @Override
        public int getItemCount() {
            return eventsList.size();
        }
    }

    private class EventHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private Event event;
        private final TextView nameTextViewRequest;
        private final TextView descTextView;
        private AsyncTask<String, Void, Bitmap> imageView;

        public EventHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_event, parent, false));
            itemView.setOnClickListener(this);
            nameTextViewRequest = itemView.findViewById(R.id.nameTextView);
            descTextView = itemView.findViewById(R.id.descriptionTextView);
        }

        @SuppressLint("ResourceAsColor")
        public void bind(Event event) {
            this.event = event;
            nameTextViewRequest.setText(event.getName());
            descTextView.setText(cutDesc(event.getDescription()));
            //imageView = new BitMapImage((ImageView) itemView.findViewById(R.id.IconImageView)).execute(event.getImage());
        }

        private String cutDesc(String description) {
            if (description.length() > 25) {
                return description.substring(0, 24) + "...";
            } else {
                return description;
            }
        }

        @Override
        public void onClick(View view) {
            Intent showProfile = new Intent(getApplicationContext(), ProfileEventActivity.class);
            showProfile.putExtra("id", event.getId());
            startActivity(showProfile);
        }

    }

}