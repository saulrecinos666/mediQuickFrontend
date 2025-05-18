package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediquick.R;

public class MainChatActivity extends AppCompatActivity {

    private Button btnOpenChat;
    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String TAG = "MainChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnOpenChat = findViewById(R.id.btnOpenChat);

        String storedToken = retrieveAuthToken();
        if (storedToken != null) {
            Log.d(TAG, "Token recuperado en MainChatActivity: " + storedToken);
        } else {
            Log.d(TAG, "No se encontró token en MainChatActivity");
            startActivity(new Intent(MainChatActivity.this, LoginActivity.class));
            finish();
        }

        btnOpenChat.setOnClickListener(v -> {
            startActivity(new Intent(MainChatActivity.this, ChatActivity.class));
        });
    }

    private String retrieveAuthToken() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
    }
}
