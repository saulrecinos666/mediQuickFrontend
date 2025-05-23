package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.services.ChatService;
import com.example.mediquick.data.model.AllChatsResponse;
import com.example.mediquick.ui.adapters.ChatListAdapter;
import com.example.mediquick.ui.items.ChatItem;
import com.example.mediquick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";

    private RecyclerView recyclerViewChats;
    private ChatListAdapter adapter;
    private final List<ChatItem> chatList = new ArrayList<>();

    private ChatService chatService;
    private SessionManager sessionManager;
    private String jwt;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        sessionManager = new SessionManager(this);
        jwt = sessionManager.getAuthToken();
        userId = extractUserIdFromJwt(jwt);

        setupRecyclerView();
        setupRetrofitClient();
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchChats();
    }

    private void setupRecyclerView() {
        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        adapter = new ChatListAdapter(this, chatList);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewChats.setAdapter(adapter);
    }

    private void setupRetrofitClient() {
        Retrofit retrofit = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL, jwt);
        chatService = retrofit.create(ChatService.class);
    }

    private void fetchChats() {
        if (jwt == null || userId == null) {
            Log.e(TAG, "❌ JWT o userId nulo.");
            return;
        }

        chatService.getAllChats("Bearer " + jwt).enqueue(new Callback<AllChatsResponse>() {
            @Override
            public void onResponse(Call<AllChatsResponse> call, Response<AllChatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    updateChatList(response.body().data);
                } else {
                    Log.e(TAG, "❌ Error en respuesta: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<AllChatsResponse> call, Throwable t) {
                Log.e(TAG, "❌ Fallo en la petición: ", t);
            }
        });
    }

    private void updateChatList(List<AllChatsResponse.ChatData> chats) {
        chatList.clear();

        for (AllChatsResponse.ChatData chatData : chats) {
            AllChatsResponse.User otherUser = chatData.chatUserId.equals(userId)
                    ? chatData.chatDoctor
                    : chatData.chatUser;

            String fullName = String.format("%s %s",
                    otherUser.firstName,
                    //otherUser.secondName,
                    otherUser.firstLastname
                    //otherUser.secondLastname
            ).trim();

            String lastMessageText = "(Sin mensajes)";
            String senderLabel = "";

            if (chatData.message != null) {
                lastMessageText = (chatData.message.content != null && !chatData.message.content.isEmpty())
                        ? chatData.message.content : "(Archivo)";
                senderLabel = chatData.chatUserId.equals(userId) ? "Tú"
                        : otherUser.firstName.split(" ")[0];
            }

            chatList.add(new ChatItem(
                    fullName,
                    R.drawable.persona2,
                    lastMessageText,
                    senderLabel,
                    otherUser.userId,
                    chatData.chatId
            ));
        }

        adapter.notifyDataSetChanged();
        Log.i(TAG, "✅ Chats actualizados: " + chatList.size());
    }

    private String extractUserIdFromJwt(String jwt) {
        try {
            String[] parts = jwt.split("\\.");
            if (parts.length < 2) return null;

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);
            return payload.optString("userId", null);
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e(TAG, "❌ Error al decodificar JWT", e);
            return null;
        }
    }


}
