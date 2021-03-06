package com.pp2ex.finalprojectevents.Activities.Authentication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.pp2ex.finalprojectevents.Activities.MainActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

public class LogoutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_out);
        final Button signOut = findViewById(R.id.buttonLogout);
        final Button cancel = findViewById(R.id.buttonCancel);

        signOut.setOnClickListener(v -> {
            logoutUser();
            Intent goMainMenu = new Intent(LogoutActivity.this, MainActivity.class);
            startActivity(goMainMenu);
        });
        cancel.setOnClickListener(v -> {
            finish();
        });
    }

    private void logoutUser() {
        User.setAuthenticatedUser(null);
    }
}