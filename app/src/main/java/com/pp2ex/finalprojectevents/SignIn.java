package com.pp2ex.finalprojectevents;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

public class SignIn extends AppCompatActivity{

    private EditText enterEmail;
    private EditText enterPassword;

    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        final Button logIn = findViewById(R.id.login);
        final ImageButton back = findViewById(R.id.arrowBack);

        enterEmail = (EditText) findViewById(R.id.et_email);
        enterPassword = (EditText) findViewById(R.id.et_password);

        logIn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String email = enterEmail.getText().toString();
                String password = enterPassword.getText().toString();
                Intent goMainMenu = new Intent(SignIn.this, MainActivity.class);
                startActivity(goMainMenu);
            }
        });

        back.setOnClickListener(v -> {
            Intent goMainMenu = new Intent(SignIn.this, MainActivity.class);
            startActivity(goMainMenu);
        });
    }
}
