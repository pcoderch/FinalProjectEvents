package com.pp2ex.finalprojectevents.Activities.Authentication;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.MainActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity{
    private EditText enterFirstName;
    private EditText enterLastName;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText confirmPassword;
    private EditText image;
    public User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_up);
        final Button createAccount = findViewById(R.id.createAccount);
        final ImageButton back = findViewById(R.id.arrowBackSignUp);

        enterFirstName = (EditText) findViewById(R.id.et_firstName);
        enterLastName = (EditText) findViewById(R.id.et_lastName);
        enterEmail = (EditText) findViewById(R.id.et_email);
        enterPassword = (EditText) findViewById(R.id.et_password);
        confirmPassword = (EditText) findViewById(R.id.et_confirm_password);
        image = (EditText) findViewById(R.id.et_image_profile);

        createAccount.setOnClickListener(v -> {
            String firstName = enterFirstName.getText().toString();
            String lastName = enterLastName.getText().toString();
            String email = enterEmail.getText().toString();
            String password = enterPassword.getText().toString();
            String confirmedPassword = confirmPassword.getText().toString();
            String imageToAdd = image.getText().toString();
            if (password.equals(confirmedPassword)) {
                enterPassword.setTextColor(getResources().getColor(R.color.black));
                confirmPassword.setTextColor(getResources().getColor(R.color.black));
                String url = MethodsAPI.URL_REGISTER;
                JSONObject jsonBody = null;
                try {
                    jsonBody = User.registerUserJson(firstName, lastName, email, password, imageToAdd);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (url, jsonBody, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                enterPassword.setTextColor(getResources().getColor(R.color.black));
                                confirmPassword.setTextColor(getResources().getColor(R.color.black));
                                enterEmail.setTextColor(getResources().getColor(R.color.black));
                                System.out.println("Response: " + response);
                                try {
                                    String token = response.getString("accessToken");
                                    System.out.println("token: " + token);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                                Intent goSignIn = new Intent(SignUpActivity.this, SignInActivity.class);
                                startActivity(goSignIn);
                            }
                        }, error -> {
                            enterEmail.setTextColor(getResources().getColor(R.color.red));
                            enterPassword.setTextColor(getResources().getColor(R.color.red));
                            confirmPassword.setTextColor(getResources().getColor(R.color.red));
                            Toast.makeText(SignUpActivity.this, R.string.registerError, Toast.LENGTH_SHORT).show();
                        });

                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            } else {
                enterPassword.setTextColor(getResources().getColor(R.color.red));
                confirmPassword.setTextColor(getResources().getColor(R.color.red));
                Toast.makeText(SignUpActivity.this, R.string.passwordError, Toast.LENGTH_SHORT).show();
            }
        });
        back.setOnClickListener(v -> {
            Intent goMainMenu = new Intent(SignUpActivity.this, MainActivity.class);
            startActivity(goMainMenu);
        });
    }
}
