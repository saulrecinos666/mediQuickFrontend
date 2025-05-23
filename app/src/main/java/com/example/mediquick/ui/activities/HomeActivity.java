package com.example.mediquick.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.DeviceRequest;
import com.example.mediquick.services.DeviceService;
import com.example.mediquick.utils.SessionManager;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "FIREBASE";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private SessionManager sessionManager;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        applyWindowInsets();
        FirebaseApp.initializeApp(this);
        handleNotificationPermission();

        initNavigationView();
        setupDrawerMenu();

        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        // Puedes agregar más tarjetas reutilizando setupQuickCard
        // setupQuickCard(R.id.card2, "Citas", "Ver próximas citas", R.drawable.calendar, AppointmentsActivity.class);
    }

    /**
     * Aplica márgenes seguros al layout para respetar barras del sistema.
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Verifica y solicita permisos para notificaciones si es necesario.
     */
    private void handleNotificationPermission() {
        sessionManager = new SessionManager(this);
        String jwt = sessionManager.getAuthToken();
        userId = extractUserIdFromJwt(jwt);
        Log.i(TAG, "userId: " + userId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE
                );
                return;
            }
        }
        obtenerTokenFCM();
    }

    /**
     * Obtiene el token de Firebase Cloud Messaging.
     */
    private void obtenerTokenFCM() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(TAG, "Token FCM: " + token);

                    // Enviar al backend
                    sendDeviceToBackend(userId, token);
                });
    }

    private void sendDeviceToBackend(String userId, String token) {
        String baseUrl = BuildConfig.BACKEND_BASE_URL;
        DeviceService service = ApiClient.getUnauthenticatedClient(baseUrl).create(DeviceService.class);

        DeviceRequest request = new DeviceRequest(userId, token, "android");

        service.registerDevice(request).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    // Toast
                    Toast.makeText(HomeActivity.this, "Token FCM registrado correctamente", Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "Dispositivo registrado correctamente");
                } else {
                    Log.e(TAG, "Error al registrar dispositivo: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(TAG, "Error de red al registrar dispositivo", t);
            }
        });
    }



    /**
     * Configura el menú lateral y el botón de apertura.
     */
    private void setupDrawerMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.button);

        menuButton.setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));
    }

    /**
     * Inicializa el NavigationView y su listener.
     */
    private void initNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            if (menuItem.getItemId() == R.id.nav_logout) {
                logoutUser();
                return true;
            }
            // Agrega más ítems si es necesario
            return false;
        });
    }

    /**
     * Método reutilizable para configurar una tarjeta de acceso rápido.
     */
    private void setupQuickCard(int cardIncludeId, String titleText, String subtitleText, int iconResId, Class<?> targetActivity) {
        View quickCardView = findViewById(cardIncludeId);

        TextView title = quickCardView.findViewById(R.id.quick_card_title);
        TextView subtitle = quickCardView.findViewById(R.id.quick_card_subtitle);
        ImageView icon = quickCardView.findViewById(R.id.quick_card_icon);
        MaterialCardView card = quickCardView.findViewById(R.id.quick_access_card);

        title.setText(titleText);
        subtitle.setText(subtitleText);
        icon.setImageResource(iconResId);

        card.setOnClickListener(v -> startActivity(new Intent(HomeActivity.this, targetActivity)));
    }

    /**
     * Cierra sesión y redirige al login.
     */
    private void logoutUser() {
        new SessionManager(this).clearSession();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Borra historial
        startActivity(intent);
        finish();
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
}
