package com.pp2ex.finalprojectevents.Activities.EventManagement;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.Event;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class CreateEventActivity extends AppCompatActivity {

    private EditText enterEventName;
    private EditText enterEventDescription;
    private EditText enterEventCapacity;
    private EditText enterEventLocation;
    private EditText enterEventCategory;
    private EditText enterEventStartDate;
    private EditText enterEventEndDate;
    private EditText enterEventImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        //super.onBackPressed();
        setContentView(R.layout.create_event);
        final Button createEvent = findViewById(R.id.create_event_button);
        final ImageButton goBack = findViewById(R.id.arrowBackCreateEvent);
        enterEventName = findViewById(R.id.et_event_name);
        enterEventDescription = findViewById(R.id.et_event_description);
        enterEventCapacity = findViewById(R.id.et_event_capacity);
        enterEventLocation = findViewById(R.id.et_event_location);
        enterEventCategory = findViewById(R.id.et_event_category);
        enterEventStartDate = findViewById(R.id.et_event_start_date);
        enterEventEndDate = findViewById(R.id.et_event_end_date);
        enterEventImage = findViewById(R.id.et_event_image);

        goBack.setOnClickListener(v -> {
            finish();
        });

        createEvent.setOnClickListener(v -> {
            Event newEvent = new Event(enterEventName.getText().toString(), enterEventDescription.getText().toString(),enterEventStartDate.getText().toString(), enterEventEndDate.getText().toString(), Integer.parseInt(enterEventCapacity.getText().toString()), enterEventLocation.getText().toString(), enterEventCategory.getText().toString(),  enterEventImage.getText().toString());
            JSONObject eventJSON = newEvent.toJSON();
            System.out.println("Event JSON: " + eventJSON);
            System.out.println("User auth: " + User.getAuthenticatedUser().getToken());
            String url = MethodsAPI.URL_EVENT;
            System.out.println("URL: " + url);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, eventJSON, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("Response: " + response);
                    Toast.makeText(CreateEventActivity.this, R.string.create_event_success, Toast.LENGTH_SHORT).show();
                    finish();
                }}, error -> {
                    System.out.println("Error: " + error);
                    Toast.makeText(CreateEventActivity.this, R.string.create_event_fail, Toast.LENGTH_LONG).show();
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
    }
}