package com.pp2ex.finalprojectevents.Activities.EventManagement;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.Event;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditEventActivity extends AppCompatActivity {

    private EditText enterEventName;
    private EditText enterEventDescription;
    private EditText enterEventCapacity;
    private EditText enterEventLocation;
    private EditText enterEventStartingDate;
    private EditText enterEventEndingDate;
    private EditText enterEventCategory;
    private EditText enterEventImage;
    private int eventId;
    private ImageButton backButton;
    private Button saveChanges;
    private Event eventToUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.update_event);
        saveChanges = findViewById(R.id.create_event_button_update);
        backButton = findViewById(R.id.arrowBackUpdateEvent);
        eventId = getIntent().getIntExtra("id", 0);
        getEventData(eventId);
        saveChanges.setOnClickListener(v -> {
            String eventName = enterEventName.getText().toString();
            String eventDescription = enterEventDescription.getText().toString();
            String eventCapacity = enterEventCapacity.getText().toString();
            String eventEndingDate = enterEventEndingDate.getText().toString();
            String eventLocation = enterEventLocation.getText().toString();
            String eventStartingDate = enterEventStartingDate.getText().toString();
            String eventCategory = enterEventCategory.getText().toString();
            String eventImage = enterEventImage.getText().toString();
            JSONObject jsonBody = getInfoInJson(eventName, eventDescription, eventCapacity, eventEndingDate, eventLocation, eventStartingDate, eventCategory, eventImage);
            String url = MethodsAPI.getEventById(eventId);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody, response -> {
                Toast.makeText(getApplicationContext(), R.string.eventUpdated, Toast.LENGTH_LONG).show();
                finish();
            }, error -> {
                Toast.makeText(getApplicationContext(), R.string.errorUpdatingEvent, Toast.LENGTH_LONG).show();
            } ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                    return headers;
                }
            };
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

        });
        backButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void getEventData(int eventID) {
        String url = MethodsAPI.getEventById(eventID);
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    String name = jsonObject.getString("name");
                    String image = jsonObject.getString("image");
                    String location = jsonObject.getString("location");
                    String description = jsonObject.getString("description");
                    String start_date = jsonObject.getString("eventStart_date");
                    String end_date = jsonObject.getString("eventEnd_date");
                    int n_participators = jsonObject.getInt("n_participators");
                    String type = jsonObject.getString("type");
                    Event eventToAdd = new Event(name, description, start_date, end_date, n_participators, image, location, type);
                    setEvent(eventToAdd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            initializeTextFields();
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

    private void setEvent(Event eventToAdd) {
        eventToUpdate = eventToAdd;
    }

    private JSONObject getInfoInJson(String eventName, String eventDescription, String eventCapacity, String eventEndingDate, String eventLocation, String eventStartingDate, String eventCategory, String eventImage) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("name", eventName);
            jsonBody.put("description", eventDescription);
            jsonBody.put("n_participators", eventCapacity);
            jsonBody.put("eventEnd_date", eventEndingDate);
            jsonBody.put("location", eventLocation);
            jsonBody.put("eventStart_date", eventStartingDate);
            jsonBody.put("type", eventCategory);
            jsonBody.put("image", eventImage);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    private void initializeTextFields() {
        enterEventName = (EditText) findViewById(R.id.et_event_name_update);
        enterEventDescription = (EditText) findViewById(R.id.et_event_description_update);
        enterEventCapacity = (EditText) findViewById(R.id.et_event_capacity_update);
        enterEventEndingDate = (EditText) findViewById(R.id.et_event_end_date_update);
        enterEventLocation = (EditText) findViewById(R.id.et_event_location_update);
        enterEventStartingDate = (EditText) findViewById(R.id.et_event_start_date_update);
        enterEventCategory = (EditText) findViewById(R.id.et_event_category_update);
        enterEventImage = (EditText) findViewById(R.id.et_event_image_update);

        enterEventName.setText(eventToUpdate.getName());
        enterEventDescription.setText(eventToUpdate.getDescription());
        enterEventCapacity.setText(String.valueOf(eventToUpdate.getNumOfParticipants()));
        enterEventEndingDate.setText(eventToUpdate.getEndDate());
        enterEventLocation.setText(eventToUpdate.getLocation());
        enterEventStartingDate.setText(eventToUpdate.getStartDate());
        enterEventCategory.setText(eventToUpdate.getType());
        enterEventImage.setText(eventToUpdate.getImage());

    }
}