package com.pp2ex.finalprojectevents.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.pp2ex.finalprojectevents.Activities.Authentication.SignInActivity;
import com.pp2ex.finalprojectevents.Activities.Authentication.SignUpActivity;
import com.pp2ex.finalprojectevents.R;

import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button signUpButton = findViewById(R.id.ButtonSignUp);
        final Button signInButton = findViewById(R.id.ButtonSignIn);

        signUpButton.setOnClickListener(v -> {
            Intent SignUpTask = new Intent(MainActivity.this, SignUpActivity.class);
            startActivity(SignUpTask);
        });
        signInButton.setOnClickListener(v -> {
            Intent SignInTask = new Intent(MainActivity.this, SignInActivity.class);
            startActivity(SignInTask);
        });
    }
}