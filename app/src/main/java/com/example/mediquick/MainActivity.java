package com.example.mediquick;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.mediquick.ui.activities.HomeActivity;
import com.example.mediquick.ui.activities.LoginActivity;
import com.example.mediquick.ui.activities.TemporalHome;

public class MainActivity extends AppCompatActivity {

    // Declaración para el botón
    // Button buttonOpenChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        startActivity(new Intent(this, LoginActivity.class));
        finish();
//        startActivity(new Intent(this, TemporalHome.class));
//        finish();
    }

    // Override para el llamado de la activity
    //     @Override
    //    protected void onCreate(Bundle savedInstanceState) {
    //        super.onCreate(savedInstanceState);
    //        setContentView(R.layout.activity_main);
    //
    //        buttonOpenChat = findViewById(R.id.buttonOpenChat);
    //
    //        buttonOpenChat.setOnClickListener(new View.OnClickListener() {
    //            @Override
    //            public void onClick(View v) {
    //                Intent intent = new Intent(MainActivity.this, ChatActivity.class);
    //                startActivity(intent);
    //            }
    //        });
    //    }

}