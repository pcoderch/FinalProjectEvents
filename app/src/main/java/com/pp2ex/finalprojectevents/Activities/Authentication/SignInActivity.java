package com.pp2ex.finalprojectevents.Activities.Authentication;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.content.Intent;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.Activities.Chat.ChatActivity;
import com.pp2ex.finalprojectevents.Activities.EventManagement.ProfileEventActivity;
import com.pp2ex.finalprojectevents.Activities.EventManagement.SearchEventsActivity;
import com.pp2ex.finalprojectevents.Activities.MainActivity;
import com.pp2ex.finalprojectevents.Activities.MainMenuActivity;
import com.pp2ex.finalprojectevents.Activities.SocialFeatures.SearchUsersActivity;
import com.pp2ex.finalprojectevents.DataStructures.User;
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
        logIn.setOnClickListener(v -> {
            String email = enterEmail.getText().toString();
            String password = enterPassword.getText().toString();
            String url = MethodsAPI.URL_LOGIN;
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("email", email);
                jsonBody.put("password", password);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (url, jsonBody, response -> {
                        try {
                            String token = response.getString("accessToken");
                            setUser(token, email);
                        } catch (JSONException | AuthFailureError e) {
                            e.printStackTrace();
                        }
                    }, error -> {
                        Toast.makeText(SignInActivity.this, R.string.loginError, Toast.LENGTH_SHORT).show();
                    });
            VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        });

            back.setOnClickListener(v -> {
                Intent goMainMenu = new Intent(SignInActivity.this, MainActivity.class);
                startActivity(goMainMenu);
        });
    }

    public void setUser(String token, String email) throws AuthFailureError {
        String url = MethodsAPI.URL_GET_USER + "?s=" + email;
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        User user = null;
                        try {
                            user = User.getUserFromJson(response);
                            user.setToken(token);
                            User.setAuthenticatedUser(user);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Intent goMainView = new Intent(SignInActivity.this, MainMenuActivity.class);
                        startActivity(goMainView);
                    } }, error -> {
                    Toast.makeText(SignInActivity.this, "Error: " + error, Toast.LENGTH_SHORT).show();
                } ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Authorization", "Bearer " + token);
                return headers;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
    }


}

