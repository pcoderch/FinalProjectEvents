package com.pp2ex.finalprojectevents;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

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
            Intent SignUpTask = new Intent(MainActivity.this, SignUp.class);
            startActivity(SignUpTask);
        });
        signInButton.setOnClickListener(v -> {
            Intent SignInTask = new Intent(MainActivity.this, SignIn.class);
            startActivity(SignInTask);
        });
    }
}