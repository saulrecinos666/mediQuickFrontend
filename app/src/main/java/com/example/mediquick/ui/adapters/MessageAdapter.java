package com.example.mediquick.ui.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.ui.items.Message;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MessageAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context context;
    private List<Message> messageList;
    private static final int VIEW_TYPE_SENT = 1;
    private static final int VIEW_TYPE_RECEIVED = 2;
    private static final int VIEW_TYPE_DATE_SEPARATOR = 3;

    private SimpleDateFormat timeFormat;
    private SimpleDateFormat dateFormat;

    public MessageAdapter(Context context, List<Message> messageList) {
        this.context = context;
        this.messageList = messageList;
        this.timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        this.dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
    }

    @Override
    public int getItemViewType(int position) {
        Message message = messageList.get(position);
        if (message.isDateSeparator()) {
            return VIEW_TYPE_DATE_SEPARATOR;
        }
        return message.isSent() ? VIEW_TYPE_SENT : VIEW_TYPE_RECEIVED;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case VIEW_TYPE_SENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_sent, parent, false);
                return new SentMessageViewHolder(view);
            case VIEW_TYPE_RECEIVED:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
            case VIEW_TYPE_DATE_SEPARATOR:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_date_separator, parent, false);
                return new DateSeparatorViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_message_received, parent, false);
                return new ReceivedMessageViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Message message = messageList.get(position);

        if (holder instanceof SentMessageViewHolder) {
            SentMessageViewHolder sentHolder = (SentMessageViewHolder) holder;
            bindSentMessage(sentHolder, message);
        } else if (holder instanceof ReceivedMessageViewHolder) {
            ReceivedMessageViewHolder receivedHolder = (ReceivedMessageViewHolder) holder;
            bindReceivedMessage(receivedHolder, message);
        } else if (holder instanceof DateSeparatorViewHolder) {
            DateSeparatorViewHolder dateHolder = (DateSeparatorViewHolder) holder;
            dateHolder.dateText.setText(message.getText());
        }
    }

    private void bindSentMessage(SentMessageViewHolder holder, Message message) {
        holder.messageText.setText(message.getText());

        // Configurar hora del mensaje
        String currentTime = timeFormat.format(new Date());
        holder.timeText.setText(currentTime);

        // Configurar estado del mensaje (enviado, entregado, leído)
        configureMessageStatus(holder, message);

        // Configurar reacciones si las hay
        if (message.hasReaction()) {
            holder.reactionText.setVisibility(View.VISIBLE);
            holder.reactionText.setText(message.getReaction());
        } else {
            holder.reactionText.setVisibility(View.GONE);
        }
    }

    private void bindReceivedMessage(ReceivedMessageViewHolder holder, Message message) {
        holder.messageText.setText(message.getText());

        // Configurar hora del mensaje
        String currentTime = timeFormat.format(new Date());
        holder.timeText.setText(currentTime);

        // Configurar reacciones si las hay
        if (message.hasReaction()) {
            holder.reactionText.setVisibility(View.VISIBLE);
            holder.reactionText.setText(message.getReaction());
        } else {
            holder.reactionText.setVisibility(View.GONE);
        }
    }

    private void configureMessageStatus(SentMessageViewHolder holder, Message message) {
        // Simular diferentes estados del mensaje
        int statusIcon = R.drawable.ic_check; // Por defecto: enviado
        int statusColor = context.getColor(R.color.message_status_sent);

        // En una implementación real, esto vendría del modelo Message
        MessageStatus status = getMessageStatus(message);

        switch (status) {
            case SENT:
                statusIcon = R.drawable.ic_check;
                statusColor = context.getColor(R.color.message_status_sent);
                break;
            case DELIVERED:
                statusIcon = R.drawable.ic_check_double;
                statusColor = context.getColor(R.color.message_status_delivered);
                break;
            case READ:
                statusIcon = R.drawable.ic_check_double;
                statusColor = context.getColor(R.color.message_status_read);
                break;
            case PENDING:
                statusIcon = R.drawable.ic_schedule;
                statusColor = context.getColor(R.color.message_status_pending);
                break;
        }

        holder.statusIcon.setImageResource(statusIcon);
        holder.statusIcon.setColorFilter(statusColor);
    }

    private MessageStatus getMessageStatus(Message message) {
        // Lógica para determinar el estado del mensaje
        // En una implementación real, esto vendría del servidor
        return MessageStatus.DELIVERED; // Placeholder
    }

    @Override
    public int getItemCount() {
        return messageList.size();
    }

    // Método para agregar separador de fecha
    public void addDateSeparator(String date) {
        Message dateSeparator = new Message(date, false);
        dateSeparator.setDateSeparator(true);
        messageList.add(dateSeparator);
        notifyItemInserted(messageList.size() - 1);
    }

    // Método para scroll suave al último mensaje
    public void scrollToBottom(RecyclerView recyclerView) {
        if (messageList.size() > 0) {
            recyclerView.smoothScrollToPosition(messageList.size() - 1);
        }
    }

    // ViewHolder para mensajes enviados
    static class SentMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView reactionText;
        ImageView statusIcon;

        SentMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            reactionText = itemView.findViewById(R.id.text_reaction);
            statusIcon = itemView.findViewById(R.id.icon_message_status);
        }
    }

    // ViewHolder para mensajes recibidos
    static class ReceivedMessageViewHolder extends RecyclerView.ViewHolder {
        TextView messageText;
        TextView timeText;
        TextView reactionText;

        ReceivedMessageViewHolder(View itemView) {
            super(itemView);
            messageText = itemView.findViewById(R.id.text_message_body);
            timeText = itemView.findViewById(R.id.text_message_time);
            reactionText = itemView.findViewById(R.id.text_reaction);
        }
    }

    // ViewHolder para separadores de fecha
    static class DateSeparatorViewHolder extends RecyclerView.ViewHolder {
        TextView dateText;

        DateSeparatorViewHolder(View itemView) {
            super(itemView);
            dateText = itemView.findViewById(R.id.text_date);
        }
    }

    // Enum para estados de mensaje
    private enum MessageStatus {
        PENDING, SENT, DELIVERED, READ
    }
}