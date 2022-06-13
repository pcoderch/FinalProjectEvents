package com.pp2ex.finalprojectevents.Activities.EventManagement;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonArrayRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AttendantsEventActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ArrayList<User> attendants;
    private AttendantsAdaptor adapter;
    private int eventId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_who_attends_event);
        eventId = getIntent().getIntExtra("id", 0);
        recyclerView = findViewById(R.id.myConnectionsList);
        attendants = new ArrayList<>();
        adapter = new AttendantsAdaptor(attendants);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        getAttendants();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void addUser(User user) {
        System.out.println("Adding user" + user.getEmail());
        attendants.add(user);
        adapter.notifyDataSetChanged();
    }

    private void getAttendants() {
        String url = MethodsAPI.getAttendants(eventId);
        attendants = new ArrayList<>();
        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null, response -> {
            for (int i = 0; i < response.length(); i++) {
                System.out.println(response);
                try {
                    JSONObject jsonObject = response.getJSONObject(i);
                    int id = jsonObject.getInt("id");
                    String name = jsonObject.getString("name");
                    String lastName = jsonObject.getString("last_name");
                    String email = jsonObject.getString("email");
                    User user = new User(id, name, lastName, email, "", "image");
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
        adapter = new AttendantsAdaptor(attendants);
        for (int i = 0; i < attendants.size(); i++) {
            System.out.println(attendants.get(i).getName());
        }
        recyclerView.setAdapter(adapter);
    }
    private class AttendantsAdaptor extends RecyclerView.Adapter<AttendantsHolder> {

        private final ArrayList<User> userList;

        private AttendantsAdaptor(ArrayList<User> userList) {
            this.userList = userList;
        }

        @NonNull
        @Override
        public AttendantsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
            return new AttendantsHolder(layoutInflater, parent);
        }

        @Override
        public void onBindViewHolder(AttendantsHolder holder, int position) {
            User user = userList.get(position);
            holder.bind(user);
        }

        @Override
        public int getItemCount() {
            return userList.size();
        }
    }

    private class AttendantsHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private User user;
        private final TextView nameTextView;
        private final TextView emailTextView;
        //private final ImageView imageView;


        public AttendantsHolder(LayoutInflater inflater, ViewGroup parent) {
            super(inflater.inflate(R.layout.element_attendee_event, parent, false));
            itemView.setOnClickListener(this);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            emailTextView = itemView.findViewById(R.id.emailTextView);
        }

        public void bind(User user) {
            this.user = user;
            nameTextView.setText(user.getName());
            emailTextView.setText(user.getEmail());
        }

        @Override
        public void onClick(View view) {
            Intent showProfile = new Intent(getApplicationContext(), ProfileActivity.class);
            showProfile.putExtra("email", user.getEmail());
            showProfile.putExtra("id", user.getId());
            showProfile.putExtra("image", user.getImage());
            startActivity(showProfile);
        }

    }

}