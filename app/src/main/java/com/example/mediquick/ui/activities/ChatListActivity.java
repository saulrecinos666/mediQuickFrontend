package com.example.mediquick.ui.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

import com.example.mediquick.R;
import com.example.mediquick.ui.adapters.ChatListAdapter;
import com.example.mediquick.ui.items.ChatItem;

public class ChatListActivity extends AppCompatActivity {

    private RecyclerView recyclerViewChats;
    private ChatListAdapter adapter;
    private List<ChatItem> chatList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        chatList = new ArrayList<>();

        chatList.add(new ChatItem("Andrés López", R.drawable.persona1));
        chatList.add(new ChatItem("Ana Rodríguez", R.drawable.persona2));
        chatList.add(new ChatItem("Daniel Gómez", R.drawable.persona3));
        chatList.add(new ChatItem("Laura Torres", R.drawable.persona4));
        chatList.add(new ChatItem("Luis Hernández", R.drawable.persona5));
        chatList.add(new ChatItem("Javier Soto", R.drawable.persona6));
        chatList.add(new ChatItem("Mateo García", R.drawable.persona7));
        chatList.add(new ChatItem("Esteban Díaz", R.drawable.persona8));
        chatList.add(new ChatItem("Sofía Delgado", R.drawable.persona9));
        chatList.add(new ChatItem("Sergio Varela", R.drawable.persona10));
        chatList.add(new ChatItem("Tomás Calderón", R.drawable.persona11));
        chatList.add(new ChatItem("Camila Rivas", R.drawable.persona12));

        adapter = new ChatListAdapter(this, chatList);
        recyclerViewChats.setAdapter(adapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
    }
}
//    <!--Moris Navas-->
