package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.ui.adapters.MessageAdapter;
import com.example.mediquick.ui.items.Message;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {

    private static final String TAG = "ChatActivity";
    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String SOCKET_URL = "https://medquick-backend-app-953862767231.us-central1.run.app";

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private final List<Message> messageList = new ArrayList<>();
    private Socket socket;
    private String userId;
    private String currentChatRoom;
    private String sendTo = "default-room";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        initViews();
        initSocket();

        String selectedUserId = getIntent().getStringExtra("send_to");
        if (selectedUserId != null) {
            joinNewChatRoom(selectedUserId);
        }

        findViewById(R.id.btnBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageAdapter = new MessageAdapter(this, messageList);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMessages.setAdapter(messageAdapter);

        EditText messageInput = findViewById(R.id.messageInput);
        ImageView sendButton = findViewById(R.id.sendButton);

        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                sendMessageToServer(message);
                appendMessage("Yo: " + message, true);
                messageInput.setText("");
            }
        });
    }

    private void initSocket() {
        String jwt = retrieveAuthToken();
        if (jwt == null) {
            Toast.makeText(this, "Token no encontrado", Toast.LENGTH_LONG).show();
            return;
        }

        userId = extractUserIdFromJwt(jwt);
        if (userId == null) {
            Toast.makeText(this, "Token inválido", Toast.LENGTH_LONG).show();
            return;
        }

        try {
            IO.Options options = new IO.Options();
            options.query = "token=" + jwt;
            options.transports = new String[]{"websocket"};
            options.forceNew = true;
            options.reconnection = true;

            socket = IO.socket(SOCKET_URL, options);

            socket.on(Socket.EVENT_CONNECT, args ->
                    runOnUiThread(() -> {
                        Log.i(TAG, "✅ Conectado");
                        Toast.makeText(this, "Conectado al servidor", Toast.LENGTH_SHORT).show();
                    })
            );

            socket.on("mensaje-desde-servidor", args ->
                    runOnUiThread(() -> handleIncomingMessage((JSONObject) args[0]))
            );

            socket.on(Socket.EVENT_CONNECT_ERROR, args ->
                    runOnUiThread(() -> {
                        Log.e(TAG, "❌ Error de conexión: " + args[0]);
                        Toast.makeText(this, "Error al conectar al servidor", Toast.LENGTH_LONG).show();
                    })
            );

            socket.connect();

        } catch (URISyntaxException e) {
            Log.e(TAG, "URISyntaxException: " + e.getMessage(), e);
        }
    }

    private void sendMessageToServer(String message) {
        try {
            JSONObject payload = new JSONObject();
            payload.put("to", sendTo);
            payload.put("message", message);
            socket.emit("mensaje-desde-cliente", payload);
        } catch (JSONException e) {
            Log.e(TAG, "Error al enviar mensaje", e);
        }
    }

    private void handleIncomingMessage(JSONObject data) {
        try {
            String fullName = data.getString("fullName");
            String message = data.getString("message");
            String fromId = data.getString("from");

            if (!fromId.equals(userId)) {
                appendMessage(fullName + ": " + message, false);
            }
        } catch (JSONException e) {
            Log.e(TAG, "Error al recibir mensaje", e);
        }
    }

    private void appendMessage(String text, boolean isSentByUser) {
        messageList.add(new Message(text, isSentByUser));
        messageAdapter.notifyItemInserted(messageList.size() - 1);
        recyclerViewMessages.scrollToPosition(messageList.size() - 1);
    }

    private void joinNewChatRoom(String otherUserId) {
        String newChatRoom = generarChatId(userId, otherUserId);

        if (currentChatRoom != null) {
            socket.emit("salir-de-chat", currentChatRoom);
            Log.i(TAG, "Saliste de la sala: " + currentChatRoom);
        }

        try {
            JSONObject payload = new JSONObject();
            payload.put("myUserId", userId);
            payload.put("withUserId", otherUserId);
            socket.emit("unirse-a-chat", payload);

            currentChatRoom = newChatRoom;
            sendTo = otherUserId;
            Log.i(TAG, "Te uniste a chat: " + newChatRoom);
        } catch (JSONException e) {
            Log.e(TAG, "Error al unirse a chat", e);
        }
    }

    private String extractUserIdFromJwt(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);
            return payload.optString("userId", null);
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e(TAG, "Error al decodificar JWT", e);
            return null;
        }
    }

    private String generarChatId(String idA, String idB) {
        return idA.compareTo(idB) < 0 ? "chat:" + idA + ":" + idB : "chat:" + idB + ":" + idA;
    }

    private String retrieveAuthToken() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
    }
}
