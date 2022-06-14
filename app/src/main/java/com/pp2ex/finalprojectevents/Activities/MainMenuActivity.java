package com.pp2ex.finalprojectevents.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.pp2ex.finalprojectevents.Activities.Authentication.LogoutActivity;
import com.pp2ex.finalprojectevents.Activities.Authentication.ProfileActivity;
import com.pp2ex.finalprojectevents.Activities.Chat.ChatActivity;
import com.pp2ex.finalprojectevents.Activities.EventManagement.CreateEventActivity;
import com.pp2ex.finalprojectevents.Activities.EventManagement.SearchEventsActivity;
import com.pp2ex.finalprojectevents.Activities.SocialFeatures.FriendRequestActivity;
import com.pp2ex.finalprojectevents.Activities.SocialFeatures.SearchUsersActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

public class MainMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_view);
        LinearLayout myProfileLayout = findViewById(R.id.profile);
        LinearLayout everyoneLayout = findViewById(R.id.everyone);
        LinearLayout chatsLayout = findViewById(R.id.chats);
        LinearLayout createEventLayout = findViewById(R.id.createEvent);
        LinearLayout searchEventsLayout = findViewById(R.id.searchEvents);
        LinearLayout requestsLayout = findViewById(R.id.requests);
        Button logoutButton = findViewById(R.id.logout);

        myProfileLayout.setOnClickListener(v -> {
            Intent goToProfile = new Intent(MainMenuActivity.this, ProfileActivity.class);
            goToProfile.putExtra("id", User.getAuthenticatedUser().getId());
            startActivity(goToProfile);
        });

        everyoneLayout.setOnClickListener(v -> {
            Intent goToSearchUsers = new Intent(MainMenuActivity.this, SearchUsersActivity.class);
            startActivity(goToSearchUsers);
        });

        chatsLayout.setOnClickListener(v -> {
            Intent goToChats = new Intent(MainMenuActivity.this, ChatActivity.class);
            startActivity(goToChats);
        });

        createEventLayout.setOnClickListener(v -> {
            Intent goToCreateEvent = new Intent(MainMenuActivity.this, CreateEventActivity.class);
            startActivity(goToCreateEvent);
        });

        searchEventsLayout.setOnClickListener(v -> {
            Intent goToSearchEvents = new Intent(MainMenuActivity.this, SearchEventsActivity.class);
            startActivity(goToSearchEvents);
        });

        requestsLayout.setOnClickListener(v -> {
            Intent goToRequests = new Intent(MainMenuActivity.this, FriendRequestActivity.class);
            startActivity(goToRequests);
        });

        logoutButton.setOnClickListener(v -> {
            Intent goToLogout = new Intent(MainMenuActivity.this, LogoutActivity.class);
            startActivity(goToLogout);
        });
    }

}
