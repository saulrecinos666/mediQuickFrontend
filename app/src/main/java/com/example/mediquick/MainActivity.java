package com.example.mediquick;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediquick.ui.activities.ChatActivity;
import com.example.mediquick.ui.activities.ChatListActivity;
import com.example.mediquick.ui.activities.LoginActivity;

public class MainActivity extends AppCompatActivity {

    // Declaración para el botón
    // Button buttonOpenChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_chat_list);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        Log.d("MainActivity", "App started");

        String storedToken = getSharedPreferences("auth_prefs", MODE_PRIVATE)
                .getString("authToken", null);

        if (storedToken != null) {
            startActivity(new Intent(this, ChatListActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }

        finish();
    }
}