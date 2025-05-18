package com.example.mediquick.ui.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;

import com.example.mediquick.R;
import com.example.mediquick.ui.activities.ChatActivity;
import com.example.mediquick.ui.items.ChatItem;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;
    private Context context;

    public ChatListAdapter(Context context, List<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
    }

    @NonNull
    @Override
    public ChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_chat, parent, false);
        return new ChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatViewHolder holder, int position) {
        ChatItem chat = chatList.get(position);
        holder.name.setText(chat.getName());
        holder.imageProfile.setImageResource(chat.getImageResId());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ChatActivity.class);
            intent.putExtra("contact_name", chat.getName());
            intent.putExtra("image_res", chat.getImageResId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        ImageView imageProfile;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textContactName);
            imageProfile = itemView.findViewById(R.id.imageProfile);
        }
    }
}
//    <!--Moris Navas-->
