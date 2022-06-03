package com.pp2ex.finalprojectevents;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.Objects;

public class SignUp extends AppCompatActivity{
    private EditText enterUsername;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText confirmPassword;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        final Button createAccount = findViewById(R.id.createAccount);
        final ImageButton back = findViewById(R.id.arrowBackSignUp);

        enterUsername = (EditText) findViewById(R.id.et_username);
        enterEmail = (EditText) findViewById(R.id.et_email);
        enterPassword = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);

        createAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String username = enterUsername.getText().toString();
                String email = enterEmail.getText().toString();
                String password = enterPassword.getText().toString();
                String confirmedPassword = confirmPassword.getText().toString();
                if (password.equals(confirmedPassword)) {
                    //user.setPassword(password);
                    enterPassword.setTextColor(getResources().getColor(R.color.black));
                    confirmPassword.setTextColor(getResources().getColor(R.color.black));
                    Intent goMainMenu = new Intent(SignUp.this, MainActivity.class);
                    startActivity(goMainMenu);
                }
                else {
                    enterPassword.setTextColor(getResources().getColor(R.color.red));
                    confirmPassword.setTextColor(getResources().getColor(R.color.red));
                }
            }
        });
        back.setOnClickListener(v -> {
            Intent goMainMenu = new Intent(SignUp.this, MainActivity.class);
            startActivity(goMainMenu);
        });
    }
}
