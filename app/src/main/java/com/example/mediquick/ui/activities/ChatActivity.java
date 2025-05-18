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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {

    private RecyclerView recyclerViewMessages;
    private MessageAdapter messageAdapter;
    private List<Message> messageList;
    private Socket socket;
    private String userId;
    private String sendTo = "default-room"; // se puede sobrescribir con el intent

    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";
    private static final String TAG = "MainChatActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        recyclerViewMessages = findViewById(R.id.recyclerViewMessages);
        messageList = new ArrayList<>();
        messageAdapter = new MessageAdapter(messageList);
        recyclerViewMessages.setAdapter(messageAdapter);
        recyclerViewMessages.setLayoutManager(new LinearLayoutManager(this));

        EditText messageInput = findViewById(R.id.messageInput);
        ImageView sendButton = findViewById(R.id.sendButton);

        String jwt = retrieveAuthToken();
        sendTo = "57a14478-5d98-4afc-bcfc-8aa75816af41";
        if (sendTo == null) sendTo = "default-room";

        userId = extractUserIdFromJwt(jwt);
        Log.i("JWT", "✅ userId extraído: " + userId);

        try {
            IO.Options options = new IO.Options();
            options.query = "token=" + jwt;
            options.transports = new String[]{"websocket"};
            options.forceNew = true;
            options.reconnection = true;

            socket = IO.socket("https://medquick-backend-app-953862767231.us-central1.run.app", options);
            //socket = IO.socket("http://192.168.0.11:8080", options);

            socket.on(Socket.EVENT_CONNECT, args -> runOnUiThread(() -> {
                Log.i("SOCKET", "✅ Conectado");
                socket.emit("unirse-a-sala", userId);
            }));

            socket.on("mensaje-desde-servidor", args -> {
                runOnUiThread(() -> {
                    try {
                        JSONObject data = (JSONObject) args[0];
                        String fullName = data.getString("fullName");
                        String message = data.getString("message");
                        String fromId = data.getString("from");

                        // ✅ Evitar duplicado si el mensaje viene del propio usuario
                        if (!fromId.equals(userId)) {
                            messageList.add(new Message(fullName + ": " + message, false));
                            messageAdapter.notifyItemInserted(messageList.size() - 1);
                            recyclerViewMessages.scrollToPosition(messageList.size() - 1);
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });
            });


            socket.on(Socket.EVENT_CONNECT_ERROR, args -> runOnUiThread(() -> {
                Log.e("SOCKET", "❌ Error de conexión: " + args[0]);
                Toast.makeText(ChatActivity.this, "No se pudo conectar al servidor", Toast.LENGTH_LONG).show();
            }));

            socket.connect();

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        sendButton.setOnClickListener(view -> {
            String message = messageInput.getText().toString().trim();
            if (!message.isEmpty()) {
                try {
                    JSONObject payload = new JSONObject();
                    payload.put("to", sendTo);
                    payload.put("message", message);

                    socket.emit("mensaje-desde-cliente", payload);

                    messageList.add(new Message("Yo: " + message, true));
                    messageAdapter.notifyItemInserted(messageList.size() - 1);
                    recyclerViewMessages.scrollToPosition(messageList.size() - 1);

                    messageInput.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

        View btnBackView = findViewById(R.id.btnBack);
        if (btnBackView instanceof ImageView) {
            ((ImageView) btnBackView).setOnClickListener(v -> finish());
        }
    }

    private String extractUserIdFromJwt(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);
            return payload.getString("userId");
        } catch (UnsupportedEncodingException | JSONException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (socket != null) {
            socket.disconnect();
            socket.off();
        }
    }

    private String retrieveAuthToken() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
    }
}
