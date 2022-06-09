package com.pp2ex.finalprojectevents.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class SignInActivity extends AppCompatActivity{

    private EditText enterEmail;
    private EditText enterPassword;

    protected void onCreate(Bundle savedInstanceState) {
        Objects.requireNonNull(getSupportActionBar()).hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in);
        final Button logIn = findViewById(R.id.login);
        final ImageButton back = findViewById(R.id.arrowBackSignIn);

        enterEmail = (EditText) findViewById(R.id.et_email);
        enterPassword = (EditText) findViewById(R.id.et_password);
        //TODO: check if email is already in use
        logIn.setOnClickListener(v -> {
            String email = enterEmail.getText().toString();
            String password = enterPassword.getText().toString();
            String url = MethodsAPI.URL_LOGIN;
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                System.out.println("Error2: " + e);
                e.printStackTrace();
            }
            System.out.println("URL: " + url);
            System.out.println("email: " + email + " password: " + password);
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (url, jsonBody, new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println("Response: " + response);
                        }
                    }, error -> {
                        // TODO: Handle error
                        System.out.println("Error: " + error);
                    });

            // Access the RequestQueue through your singleton class.
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        });

            back.setOnClickListener(v -> {
            Intent goMainMenu = new Intent(SignInActivity.this, MainActivity.class);
            startActivity(goMainMenu);

        });
    }
}
