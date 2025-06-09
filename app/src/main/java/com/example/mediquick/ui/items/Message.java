package com.example.mediquick.ui.items;

import java.util.Date;

public class Message {
    private String text;
    private boolean isSent;
    private Date timestamp;
    private String messageId;
    private String senderId;
    private String reaction;
    private boolean isDateSeparator;
    private MessageStatus status;
    private MessageType type;

    // Constructores
    public Message(String text, boolean isSent) {
        this.text = text;
        this.isSent = isSent;
        this.timestamp = new Date();
        this.messageId = generateMessageId();
        this.status = MessageStatus.SENT;
        this.type = MessageType.TEXT;
        this.isDateSeparator = false;
    }

    public Message(String text, boolean isSent, Date timestamp) {
        this.text = text;
        this.isSent = isSent;
        this.timestamp = timestamp;
        this.messageId = generateMessageId();
        this.status = MessageStatus.SENT;
        this.type = MessageType.TEXT;
        this.isDateSeparator = false;
    }

    // Constructor para separador de fecha
    public static Message createDateSeparator(String dateText) {
        Message message = new Message(dateText, false);
        message.isDateSeparator = true;
        return message;
    }

    // Getters y Setters
    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isSent() {
        return isSent;
    }

    public void setSent(boolean sent) {
        isSent = sent;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReaction() {
        return reaction;
    }

    public void setReaction(String reaction) {
        this.reaction = reaction;
    }

    public boolean hasReaction() {
        return reaction != null && !reaction.trim().isEmpty();
    }

    public boolean isDateSeparator() {
        return isDateSeparator;
    }

    public void setDateSeparator(boolean dateSeparator) {
        isDateSeparator = dateSeparator;
    }

    public MessageStatus getStatus() {
        return status;
    }

    public void setStatus(MessageStatus status) {
        this.status = status;
    }

    public MessageType getType() {
        return type;
    }

    public void setType(MessageType type) {
        this.type = type;
    }

    // Métodos auxiliares
    private String generateMessageId() {
        return "msg_" + System.currentTimeMillis() + "_" + hashCode();
    }

    public boolean isToday() {
        Date now = new Date();
        return isSameDay(timestamp, now);
    }

    public boolean isYesterday() {
        Date yesterday = new Date(System.currentTimeMillis() - 24 * 60 * 60 * 1000);
        return isSameDay(timestamp, yesterday);
    }

    private boolean isSameDay(Date date1, Date date2) {
        if (date1 == null || date2 == null) return false;

        long diff = Math.abs(date1.getTime() - date2.getTime());
        return diff < 24 * 60 * 60 * 1000;
    }

    // Enums
    public enum MessageStatus {
        PENDING,    // Enviando
        SENT,       // Enviado
        DELIVERED,  // Entregado
        READ,       // Leído
        FAILED      // Error al enviar
    }

    public enum MessageType {
        TEXT,       // Mensaje de texto
        IMAGE,      // Imagen
        AUDIO,      // Audio
        VIDEO,      // Video
        DOCUMENT,   // Documento
        LOCATION    // Ubicación
    }

    @Override
    public String toString() {
        return "Message{" +
                "text='" + text + '\'' +
                ", isSent=" + isSent +
                ", timestamp=" + timestamp +
                ", status=" + status +
                '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;

        Message message = (Message) obj;
        return messageId != null && messageId.equals(message.messageId);
    }

    @Override
    public int hashCode() {
        return messageId != null ? messageId.hashCode() : 0;
    }
}