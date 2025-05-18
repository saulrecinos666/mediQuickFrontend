package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.AllChatsResponse;
import com.example.mediquick.ui.adapters.ChatListAdapter;
import com.example.mediquick.ui.items.ChatItem;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.http.GET;
import retrofit2.http.Header;

public class ChatListActivity extends AppCompatActivity {

    private static final String TAG = "ChatListActivity";
    private static final String SHARED_PREF_NAME = "auth_prefs";
    private static final String KEY_AUTH_TOKEN = "authToken";

    private RecyclerView recyclerViewChats;
    private ChatListAdapter adapter;
    private List<ChatItem> chatList;

    private ChatService chatService;
    private String jwt;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_list);

        recyclerViewChats = findViewById(R.id.recyclerViewChats);
        chatList = new ArrayList<>();
        adapter = new ChatListAdapter(this, chatList);
        recyclerViewChats.setAdapter(adapter);
        recyclerViewChats.setLayoutManager(new LinearLayoutManager(this));

        jwt = retrieveAuthToken();
        userId = extractUserIdFromJwt(jwt);

        Retrofit retrofit = ApiClient.getClient(BuildConfig.BACKEND_BASE_URL);
        chatService = retrofit.create(ChatService.class);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchChats();
    }

    private void fetchChats() {
        if (jwt == null || userId == null) {
            Log.e(TAG, "❌ JWT o userId nulo.");
            return;
        }

        chatService.getChats("Bearer " + jwt).enqueue(new Callback<AllChatsResponse>() {
            @Override
            public void onResponse(Call<AllChatsResponse> call, Response<AllChatsResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<AllChatsResponse.ChatData> chats = response.body().data;
                    chatList.clear();

                    for (AllChatsResponse.ChatData chatData : chats) {
                        AllChatsResponse.User otherUser = chatData.chatUserId.equals(userId)
                                ? chatData.chatDoctor
                                : chatData.chatUser;

                        String fullName = String.format("%s %s %s %s",
                                otherUser.firstName, otherUser.secondName,
                                otherUser.firstLastname, otherUser.secondLastname).trim();

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
                                otherUser.userId
                        ));
                    }
                    adapter.notifyDataSetChanged();
                    Log.i(TAG, "✅ Chats actualizados: " + chatList.size());
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

    private String retrieveAuthToken() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return sharedPref.getString(KEY_AUTH_TOKEN, null);
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

    public interface ChatService {
        @GET("/api/chat/get-chats")
        Call<AllChatsResponse> getChats(@Header("Authorization") String authHeader);
    }
}
