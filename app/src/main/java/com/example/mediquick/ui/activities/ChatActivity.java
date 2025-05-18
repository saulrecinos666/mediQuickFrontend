package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.mediquick.R;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Intent intent = getIntent();
        String contactName = intent.getStringExtra("contact_name");
        int imageResId = intent.getIntExtra("image_res", R.drawable.ic_person_placeholder);

        TextView nameTextView = findViewById(R.id.textContactName);
        ImageView photoImageView = findViewById(R.id.contactPhoto);
        nameTextView.setText(contactName);
        photoImageView.setImageResource(imageResId);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageList = new ArrayList<>();
        messageList.add(new Message("Hola, ¿cómo estás?", false));
        messageList.add(new Message("Todo bien, ¿y tú?", true));
        messageList.add(new Message("Muy bien también. ¿Qué haces?", false));
        messageList.add(new Message("Trabajando en un proyecto de Android 😄", true));
        messageList.add(new Message("¡Qué bien! Suerte con eso", false));

        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        LinearLayout btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());
    }
}
//    <!--Moris Navas-->
