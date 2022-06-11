package com.pp2ex.finalprojectevents.Activities;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;
import com.pp2ex.finalprojectevents.API.MethodsAPI;
import com.pp2ex.finalprojectevents.API.VolleySingleton;
import com.pp2ex.finalprojectevents.DataStructures.User;
import com.pp2ex.finalprojectevents.R;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    private EditText enterFirstName;
    private EditText enterLastName;
    private EditText enterEmail;
    private EditText enterPassword;
    private EditText confirmPassword;
    private EditText enterImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_profile);
        final Button saveChanges = findViewById(R.id.editProfileSaveChanges);
        final ImageButton back = findViewById(R.id.arrowBackEditProfile);
        initializeTextFields();
        saveChanges.setOnClickListener(v -> {
            String firstName = enterFirstName.getText().toString();
            String lastName = enterLastName.getText().toString();
            String email = enterEmail.getText().toString();
            String image = enterImage.getText().toString();
            String password = enterPassword.getText().toString();
            String confirmedPassword = confirmPassword.getText().toString();
            if (password.equals(confirmedPassword)) {
                enterPassword.setTextColor(getResources().getColor(R.color.black));
                confirmPassword.setTextColor(getResources().getColor(R.color.black));
                JSONObject jsonBody = getInfoInJson(firstName, lastName, email, password, image);
                System.out.println("JSON :" + jsonBody);
                String url = MethodsAPI.URL_EDIT_PROFILE;
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, jsonBody, response -> {
                    System.out.println("response: " + response);
                    Toast.makeText(getApplicationContext(), "Profile updated successfully", Toast.LENGTH_LONG).show();
                    finish();
                }, error -> {
                    Toast.makeText(getApplicationContext(), "Error updating profile", Toast.LENGTH_LONG).show();
                } ) {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<>();
                        headers.put("Authorization", "Bearer " + User.getAuthenticatedUser().getToken());
                        return headers;
                    }
                };
                VolleySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
            } else {
                enterPassword.setTextColor(getResources().getColor(R.color.red));
                confirmPassword.setTextColor(getResources().getColor(R.color.red));
            }
        });

        back.setOnClickListener(v -> finish());
    }

    private JSONObject getInfoInJson(String firstName, String lastName, String email, String password, String image) {
        JSONObject jsonBody = new JSONObject();
        if (!firstName.equals(User.getAuthenticatedUser().getName())) {
            try {
                jsonBody.put("name", firstName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!lastName.equals(User.getAuthenticatedUser().getLastName())) {
            try {
                jsonBody.put("lastName", lastName);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!email.equals(User.getAuthenticatedUser().getEmail())) {
            try {
                jsonBody.put("email", email);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (image.equals(User.getAuthenticatedUser().getImage())) {
            try {
                jsonBody.put("image", image);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            jsonBody.put("password", password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonBody;
    }

    private void initializeTextFields() {
        enterFirstName = (EditText) findViewById(R.id.editProfileName);
        enterLastName = (EditText) findViewById(R.id.editProfileLastName);
        enterEmail = (EditText) findViewById(R.id.editProfileEmail);
        enterImage = (EditText) findViewById(R.id.editProfileImage);
        enterPassword = (EditText) findViewById(R.id.editProfilePassword);
        confirmPassword = (EditText) findViewById(R.id.editProfileConfirmPassword);

        enterFirstName.setText(User.getAuthenticatedUser().getName());
        enterLastName.setText(User.getAuthenticatedUser().getLastName());
        enterEmail.setText(User.getAuthenticatedUser().getEmail());
        enterImage.setText(User.getAuthenticatedUser().getImage());
    }
}