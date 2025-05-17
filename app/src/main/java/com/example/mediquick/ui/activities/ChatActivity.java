package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;

import java.util.ArrayList;
import java.util.List;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageList = new ArrayList<>();

        // Mensajes de prueba
        messageList.add(new Message("Hola, ¿cómo estás?", false));
        messageList.add(new Message("Todo bien, ¿y tú?", true));
        messageList.add(new Message("Muy bien también. ¿Qué haces?", false));
        messageList.add(new Message("Trabajando en un proyecto de Android 😄", true));
        messageList.add(new Message("¡Qué bien! Suerte con eso", false));

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));


        View btnBackView = findViewById(R.id.btnBack);
        if (btnBackView != null) {
            if (btnBackView instanceof ImageView) {
                ImageView btnBack = (ImageView) btnBackView;
                btnBack.setOnClickListener(v -> finish());
            } else {

            }
        }
    }
}
//    <!--Moris Navas-->