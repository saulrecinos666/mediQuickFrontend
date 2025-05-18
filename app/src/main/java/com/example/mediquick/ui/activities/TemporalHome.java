package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediquick.R;

public class TemporalHome extends AppCompatActivity {

    private Button buttonOpenChat;
    private EditText inputJwt, inputSendToId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        inputJwt = findViewById(R.id.inputJwt);
//
//        inputSendToId = findViewById(R.id.inputSendToId);
//        buttonOpenChat = findViewById(R.id.btnOpenChat);
//
//        buttonOpenChat.setOnClickListener(v -> {
//            String jwt = inputJwt.getText().toString().trim();
//            String sendToId = inputSendToId.getText().toString().trim();
//
//            Intent intent = new Intent(TemporalHome.this, ChatActivity.class);
//            intent.putExtra("jwt_token", jwt);
//            intent.putExtra("send_to_id", sendToId);
//            startActivity(intent);
//        });
    }
}
