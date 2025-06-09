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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.example.mediquick.R;
import com.example.mediquick.ui.activities.ChatActivity;
import com.example.mediquick.ui.items.ChatItem;

public class ChatListAdapter extends RecyclerView.Adapter<ChatListAdapter.ChatViewHolder> {

    private List<ChatItem> chatList;
    private Context context;
    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    public ChatListAdapter(Context context, List<ChatItem> chatList) {
        this.context = context;
        this.chatList = chatList;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());
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

        // Configurar nombre del contacto
        holder.name.setText(chat.getName());
        holder.imageProfile.setImageResource(chat.getImageResId());

        // Configurar mensaje con mejor formato
        configureLastMessage(holder, chat);

        // Configurar tiempo del mensaje
        configureMessageTime(holder, chat);

        // Configurar indicadores visuales
        configureMessageIndicators(holder, chat);

        // Click listener con animación
        holder.itemView.setOnClickListener(v -> {
            // Animación de click
            v.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() -> {
                        v.animate()
                                .scaleX(1.0f)
                                .scaleY(1.0f)
                                .setDuration(100)
                                .start();

                        // Iniciar actividad
                        Intent intent = new Intent(context, ChatActivity.class);
                        intent.putExtra("contact_name", chat.getName());
                        intent.putExtra("image_res", chat.getImageResId());
                        intent.putExtra("send_to", chat.getUserId());
                        intent.putExtra("chat_id", String.valueOf(chat.getChatId()));
                        context.startActivity(intent);
                    })
                    .start();
        });
    }

    private void configureLastMessage(ChatViewHolder holder, ChatItem chat) {
        String lastMessage = chat.getLastMessage();
        String senderLabel = chat.getSenderLabel();

        if (lastMessage == null || lastMessage.trim().isEmpty()) {
            holder.lastMessage.setText("No hay mensajes");
            holder.lastMessage.setTextColor(holder.itemView.getContext().getColor(R.color.text_hint));
            holder.lastMessage.setAlpha(0.7f);
        } else {
            // Formato mejorado del mensaje
            if (senderLabel != null && !senderLabel.isEmpty()) {
                if (senderLabel.equals("Tú")) {
                    // Mensaje propio con icono
                    holder.lastMessage.setText("Tú: " + lastMessage);
                    holder.iconMessageStatus.setVisibility(View.VISIBLE);
                } else {
                    // Mensaje de otro usuario
                    holder.lastMessage.setText(lastMessage);
                    holder.iconMessageStatus.setVisibility(View.GONE);
                }
            } else {
                holder.lastMessage.setText(lastMessage);
                holder.iconMessageStatus.setVisibility(View.GONE);
            }

            holder.lastMessage.setTextColor(holder.itemView.getContext().getColor(R.color.text_secondary));
            holder.lastMessage.setAlpha(1.0f);
        }
    }

    private void configureMessageTime(ChatViewHolder holder, ChatItem chat) {
        // Simular tiempo del mensaje (en una implementación real vendría del modelo)
        Date now = new Date();
        String timeText = timeFormat.format(now);
        holder.textTime.setText(timeText);
    }

    private void configureMessageIndicators(ChatViewHolder holder, ChatItem chat) {
        // Configurar badge de mensajes no leídos
        int unreadCount = getUnreadCount(chat); // Método ficticio
        if (unreadCount > 0) {
            holder.badgeUnread.setVisibility(View.VISIBLE);
            holder.badgeUnread.setText(unreadCount > 99 ? "99+" : String.valueOf(unreadCount));
        } else {
            holder.badgeUnread.setVisibility(View.GONE);
        }

        // Configurar indicador de estado en línea
        boolean isOnline = isUserOnline(chat); // Método ficticio
        holder.onlineIndicator.setVisibility(isOnline ? View.VISIBLE : View.GONE);
    }

    // Métodos auxiliares (implementar según tu lógica de negocio)
    private int getUnreadCount(ChatItem chat) {
        // Implementar lógica para obtener mensajes no leídos
        return 0; // Placeholder
    }

    private boolean isUserOnline(ChatItem chat) {
        // Implementar lógica para verificar estado en línea
        return false; // Placeholder
    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    public void updateData(List<ChatItem> newChatList) {
        this.chatList.clear();
        this.chatList.addAll(newChatList);
        notifyDataSetChanged();
    }

    static class ChatViewHolder extends RecyclerView.ViewHolder {
        TextView name;
        TextView lastMessage;
        TextView textTime;
        TextView badgeUnread;
        ImageView imageProfile;
        ImageView iconMessageStatus;
        View onlineIndicator;

        ChatViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.textContactName);
            lastMessage = itemView.findViewById(R.id.textLastMessage);
            textTime = itemView.findViewById(R.id.textTime);
            badgeUnread = itemView.findViewById(R.id.badgeUnread);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            iconMessageStatus = itemView.findViewById(R.id.iconMessageStatus);
            onlineIndicator = itemView.findViewById(R.id.onlineIndicator);
        }
    }
}