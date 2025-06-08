package com.example.mediquick.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.example.mediquick.data.model.GetUserDataResponse;
import com.example.mediquick.services.AuthService;
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

    private static final String TAG = "HomeActivity";
    private static final String FIREBASE_TAG = "FIREBASE";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private SessionManager sessionManager;

    // ✅ Loading state views
    private LinearLayout loadingContainer;
    private LinearLayout mainContent;
    private TextView loadingText;

    private String userId;
    private String userToken;
    private GetUserDataResponse currentUser;

    // ✅ Agregar este método a tu HomeActivity después de onCreate()

    /**
     * Verifica si hay mensajes de éxito para mostrar
     */
    private void checkForSuccessMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_success_message", false)) {
            String successMessage = intent.getStringExtra("success_message");

            if (successMessage != null && !successMessage.isEmpty()) {
                Log.d(TAG, "Mostrando mensaje de éxito desde intent: " + successMessage);

                // Mostrar el mensaje después de que se cargue la UI
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    showSuccessMessage(successMessage);
                }, 1000);
            }
        }
    }

    /**
     * Muestra un mensaje de éxito elegante
     */
    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Opcional: También puedes mostrar un Snackbar más elegante
        // View rootView = findViewById(android.R.id.content);
        // Snackbar.make(rootView, message, Snackbar.LENGTH_LONG)
        //     .setBackgroundTint(ContextCompat.getColor(this, R.color.colorPrimary))
        //     .setTextColor(Color.WHITE)
        //     .show();
    }

    // ✅ Y llamar este método al final de onCreate() en HomeActivity:
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        applyWindowInsets();
        FirebaseApp.initializeApp(this);

        initViews();
        initializeSessionAndUser();
        handleNotificationPermission();
        initNavigationView();
        setupDrawerMenu();

        // ✅ VERIFICAR MENSAJES DE ÉXITO
        checkForSuccessMessage();
    }

    /**
     * Inicializa las vistas
     */
    private void initViews() {
        loadingContainer = findViewById(R.id.loading_container);
        mainContent = findViewById(R.id.main_content);
        loadingText = findViewById(R.id.loading_text);

        Log.d(TAG, "Views de loading inicializadas");
    }

    /**
     * Inicializa la sesión y obtiene los datos del usuario
     */
    private void initializeSessionAndUser() {
        sessionManager = new SessionManager(this);
        userToken = sessionManager.getAuthToken();
        userId = extractUserIdFromJwt(userToken);

        Log.d(TAG, "=== INICIALIZANDO USUARIO ===");
        Log.d(TAG, "userId extraído: " + userId);
        Log.d(TAG, "Token disponible: " + (userToken != null && !userToken.isEmpty()));

        // ✅ MOSTRAR LOADING STATE
        showLoadingState("Loading your profile...");

        if (userId != null && !userId.isEmpty()) {
            fetchUserData();
        } else {
            Log.e(TAG, "❌ No se pudo extraer userId del JWT");
            // Usar rol desde JWT como fallback
            String roleFromJwt = extractUserRoleFromJwt(userToken);
            if (roleFromJwt != null) {
                updateLoadingText("Setting up your workspace...");
                // Pequeño delay para mostrar el loading
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    setupQuickAccessCardsWithRole(roleFromJwt);
                    hideLoadingState();
                }, 800);
            } else {
                setupQuickAccessCardsWithDefaultRole();
                hideLoadingState();
            }
        }
    }

    /**
     * Obtiene los datos del usuario desde la API
     */
    private void fetchUserData() {
        Log.d(TAG, "=== OBTENIENDO DATOS DEL USUARIO ===");
        Log.d(TAG, "Consultando usuario con ID: " + userId);

        updateLoadingText("Getting your information...");

        AuthService authService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL, userToken)
                .create(AuthService.class);

        authService.getUserDataResponseCall(userId)
                .enqueue(new Callback<GetUserDataResponse>() {
                    @Override
                    public void onResponse(Call<GetUserDataResponse> call, Response<GetUserDataResponse> response) {
                        handleUserDataResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetUserDataResponse> call, Throwable t) {
                        handleUserDataError(t);
                    }
                });
    }

    /**
     * Maneja la respuesta de datos del usuario
     */
    private void handleUserDataResponse(Response<GetUserDataResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE DATOS DE USUARIO ===");
        Log.d(TAG, "Response Code: " + response.code());

        if (response.isSuccessful() && response.body() != null) {
            currentUser = response.body();

            Log.i(TAG, "✅ Datos de usuario obtenidos exitosamente");
            Log.d(TAG, "Usuario: " + currentUser.getDisplayName());
            Log.d(TAG, "Email: " + currentUser.getUserEmail());
            Log.d(TAG, "Es paciente: " + currentUser.isPatient());
            Log.d(TAG, "Está activo: " + currentUser.isActive());

            updateLoadingText("Setting up your dashboard...");

            // ✅ PEQUEÑO DELAY PARA SUAVIZAR LA TRANSICIÓN
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                // Actualizar UI con los datos del usuario
                updateUIWithUserData();
                hideLoadingState();
            }, 500);

            if (!currentUser.isActive()) {
                Log.w(TAG, "⚠️ Usuario inactivo - considerar logout");
                showInactiveUserWarning();
            }

        } else {
            Log.e(TAG, "❌ Error HTTP al obtener datos de usuario: " + response.code());
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                Log.e(TAG, "Error Body: " + errorBody);
            } catch (Exception e) {
                Log.e(TAG, "Error leyendo error body", e);
            }

            updateLoadingText("Setting up default options...");

            // Pequeño delay antes de mostrar fallback
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                showError("Error al cargar datos de usuario");
                setupQuickAccessCardsWithDefaultRole();
                hideLoadingState();
            }, 800);
        }
    }

    /**
     * Maneja errores al obtener datos del usuario
     */
    private void handleUserDataError(Throwable t) {
        Log.e(TAG, "=== ERROR AL OBTENER DATOS DE USUARIO ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        updateLoadingText("Loading basic options...");

        // Pequeño delay antes de mostrar fallback
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showError("Error de conexión al cargar perfil");
            setupQuickAccessCardsWithDefaultRole();
            hideLoadingState();
        }, 800);
    }

    /**
     * ✅ MUESTRA EL LOADING STATE
     */
    private void showLoadingState(String message) {
        runOnUiThread(() -> {
            loadingContainer.setVisibility(View.VISIBLE);
            mainContent.setVisibility(View.GONE);
            loadingText.setText(message);
            Log.d(TAG, "Loading state mostrado: " + message);
        });
    }

    /**
     * ✅ OCULTA EL LOADING STATE
     */
    private void hideLoadingState() {
        runOnUiThread(() -> {
            loadingContainer.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
            Log.d(TAG, "Loading state ocultado - Contenido principal visible");
        });
    }

    /**
     * ✅ ACTUALIZA EL TEXTO DEL LOADING
     */
    private void updateLoadingText(String message) {
        runOnUiThread(() -> {
            if (loadingText != null) {
                loadingText.setText(message);
                Log.d(TAG, "Loading text actualizado: " + message);
            }
        });
    }

    /**
     * Actualiza la UI con los datos del usuario
     */
    private void updateUIWithUserData() {
        runOnUiThread(() -> {
            if (currentUser != null) {
                // Actualizar header del navigation drawer
                updateNavigationHeader();

                // Actualizar mensaje de bienvenida en home
                updateWelcomeMessage();

                // ✅ RECONFIGURAR TARJETAS BASADAS EN EL ROL
                setupQuickAccessCards();

                Log.d(TAG, "UI actualizada con datos de usuario");
            }
        });
    }

    /**
     * Actualiza el header del navigation drawer
     */
    private void updateNavigationHeader() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

//        if (headerView != null && currentUser != null) {
//            TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
//            TextView emailTextView = headerView.findViewById(R.id.nav_header_email);
//
//            if (nameTextView != null) {
//                nameTextView.setText(currentUser.getDisplayName());
//            }
//
//            if (emailTextView != null) {
//                emailTextView.setText(currentUser.getUserEmail());
//            }
//        }
    }

    /**
     * Actualiza el mensaje de bienvenida
     */
    private void updateWelcomeMessage() {
        // Actualizar el saludo principal (donde dice "Hi, Usuario!")
        TextView greetingTitle = findViewById(R.id.greeting_title);
        TextView greetingSubtitle = findViewById(R.id.greeting_subtitle);

        if (greetingTitle != null && currentUser != null) {
            String welcomeMessage = "Hi, " + currentUser.getDisplayName() + "!";
            greetingTitle.setText(welcomeMessage);
            Log.d(TAG, "Mensaje de bienvenida actualizado: " + welcomeMessage);
        }

        // Actualizar subtítulo con saludo dinámico según la hora
        if (greetingSubtitle != null) {
            greetingSubtitle.setText(getTimeBasedGreeting());
        }

        // También actualizar el título de la card de bienvenida
        TextView welcomeTitle = findViewById(R.id.welcome_title);
        if (welcomeTitle != null && currentUser != null) {
            welcomeTitle.setText("Welcome, " + currentUser.getUserFirstName() + "!");
        }

        // Actualizar subtítulo de la card
        TextView welcomeSubtitle = findViewById(R.id.welcome_subtitle);
        if (welcomeSubtitle != null && currentUser != null) {
            if (currentUser.isPatient()) {
                welcomeSubtitle.setText("Ready to schedule your next appointment?");
            } else {
                welcomeSubtitle.setText("Let's manage your schedule");
            }
        }
    }

    /**
     * Obtiene saludo basado en la hora del día
     */
    private String getTimeBasedGreeting() {
        java.util.Calendar calendar = java.util.Calendar.getInstance();
        int hour = calendar.get(java.util.Calendar.HOUR_OF_DAY);

        if (hour >= 5 && hour < 12) {
            return "Good morning";
        } else if (hour >= 12 && hour < 17) {
            return "Good afternoon";
        } else if (hour >= 17 && hour < 21) {
            return "Good evening";
        } else {
            return "Good night";
        }
    }

    /**
     * Configura tarjetas con rol por defecto (PACIENTE) cuando no hay datos
     */
    private void setupQuickAccessCardsWithDefaultRole() {
        Log.d(TAG, "Configurando tarjetas con rol por defecto (PACIENTE)");
        hideAllQuickCards();
        setupPatientCards();
    }

    /**
     * ✅ CONFIGURA TARJETAS CON ROL ESPECÍFICO
     */
    private void setupQuickAccessCardsWithRole(String role) {
        Log.d(TAG, "Configurando tarjetas con rol: " + role);
        hideAllQuickCards();

        switch (role.toUpperCase()) {
            case "SUPER_ADMIN":
                setupSuperAdminCards();
                break;
            case "ADMIN":
                setupAdminCards();
                break;
            case "DOCTOR":
                setupDoctorCards();
                break;
            case "PACIENTE":
            default:
                setupPatientCards();
                break;
        }
    }

    /**
     * Configura las tarjetas de acceso rápido basadas en el rol del usuario
     */
    private void setupQuickAccessCards() {
        Log.d(TAG, "=== CONFIGURANDO TARJETAS DE ACCESO RÁPIDO ===");

        // Primero ocultar todas las tarjetas
        hideAllQuickCards();

        // Obtener el rol del usuario desde JWT o API
        String userRole = getUserRole();
        Log.d(TAG, "Rol del usuario: " + userRole);

        if (userRole == null) {
            Log.w(TAG, "⚠️ Rol de usuario no encontrado, mostrando opciones básicas");
            userRole = "PACIENTE"; // Valor por defecto
        }

        setupQuickAccessCardsWithRole(userRole);
    }

    /**
     * Oculta todas las tarjetas de acceso rápido
     */
    private void hideAllQuickCards() {
        int[] cardIds = {R.id.card1, R.id.card2, R.id.card3, R.id.card4, R.id.card5};

        for (int cardId : cardIds) {
            View cardView = findViewById(cardId);
            if (cardView != null) {
                cardView.setVisibility(View.GONE);
            }
        }

        Log.d(TAG, "Todas las tarjetas ocultadas");
    }

    /**
     * Configura tarjetas para SUPER_ADMIN (acceso completo)
     */
    private void setupSuperAdminCards() {
        Log.d(TAG, "Configurando tarjetas para SUPER_ADMIN");

        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Agendar citas",
                "Administrar citas de pacientes",
                R.drawable.chat_round_dots,
                AdminAcceptAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Mis citas",
                "Citas como doctor",
                R.drawable.chat_round_dots,
                AssignedAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card4,
                "Crear cita",
                "Crear citas como paciente",
                R.drawable.chat_round_dots,
                InstitutionListActivity.class
        );

        setupQuickCard(
                R.id.card5,
                "Historial",
                "Todas mis citas",
                R.drawable.chat_round_dots,
                MyAppointmentsActivity.class
        );
    }

    /**
     * Configura tarjetas para ADMIN
     */
    private void setupAdminCards() {
        Log.d(TAG, "Configurando tarjetas para ADMIN");

        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Agendar citas",
                "Administrar citas de pacientes",
                R.drawable.chat_round_dots,
                AdminAcceptAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Historial",
                "Historial de citas",
                R.drawable.chat_round_dots,
                MyAppointmentsActivity.class
        );
    }

    /**
     * Configura tarjetas para DOCTOR
     */
    private void setupDoctorCards() {
        Log.d(TAG, "Configurando tarjetas para DOCTOR");

        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Mis citas",
                "Citas asignadas como doctor",
                R.drawable.chat_round_dots,
                AssignedAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Historial",
                "Historial de mis citas",
                R.drawable.chat_round_dots,
                MyAppointmentsActivity.class
        );
    }

    /**
     * Configura tarjetas para PACIENTE
     */
    private void setupPatientCards() {
        Log.d(TAG, "Configurando tarjetas para PACIENTE");

        setupQuickCard(
                R.id.card1,
                "Chat",
                "Empieza una conversación",
                R.drawable.chat_round_dots,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Crear cita",
                "Agendar una nueva cita médica",
                R.drawable.chat_round_dots,
                InstitutionListActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Mis citas",
                "Ver mis próximas citas",
                R.drawable.chat_round_dots,
                MyAppointmentsActivity.class
        );
    }

    /**
     * Obtiene el rol del usuario desde JWT o desde los datos de la API
     */
    private String getUserRole() {
        // Opción 1: Obtener desde JWT (más rápido)
        String roleFromJwt = extractUserRoleFromJwt(userToken);
        if (roleFromJwt != null && !roleFromJwt.isEmpty()) {
            Log.d(TAG, "Rol obtenido desde JWT: " + roleFromJwt);
            return roleFromJwt;
        }

        // Opción 2: Obtener desde datos de API (más completo)
        if (currentUser != null && currentUser.getAuthUserProfile() != null) {
            for (GetUserDataResponse.AuthUserProfile profile : currentUser.getAuthUserProfile()) {
                if (profile.getSecurityProfile() != null) {
                    String roleName = profile.getSecurityProfile().getSecurityProfileProfileName();
                    Log.d(TAG, "Rol obtenido desde API: " + roleName);
                    return roleName;
                }
            }
        }

        Log.w(TAG, "No se pudo determinar el rol del usuario");
        return null;
    }

    /**
     * Extrae el rol del usuario desde el JWT token
     */
    private String extractUserRoleFromJwt(String jwt) {
        try {
            if (jwt == null || jwt.isEmpty()) {
                Log.e(TAG, "JWT token es null o vacío");
                return null;
            }

            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                Log.e(TAG, "JWT token malformado - partes insuficientes");
                return null;
            }

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);

            // Extraer userProfiles array
            if (payload.has("userProfiles")) {
                org.json.JSONArray profiles = payload.getJSONArray("userProfiles");
                if (profiles.length() > 0) {
                    JSONObject firstProfile = profiles.getJSONObject(0);
                    String profileName = firstProfile.optString("profileName", null);
                    Log.d(TAG, "Rol extraído del JWT: " + profileName);
                    return profileName;
                }
            }

            Log.w(TAG, "No se encontró userProfiles en JWT");
            return null;

        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e(TAG, "Error al extraer rol del JWT", e);
            return null;
        }
    }

    /**
     * Método reutilizable para configurar una tarjeta de acceso rápido.
     */
    private void setupQuickCard(int cardIncludeId, String titleText, String subtitleText, int iconResId, Class<?> targetActivity) {
        View quickCardView = findViewById(cardIncludeId);

        if (quickCardView == null) {
            Log.w(TAG, "No se encontró la tarjeta con ID: " + cardIncludeId);
            return;
        }

        TextView title = quickCardView.findViewById(R.id.quick_card_title);
        TextView subtitle = quickCardView.findViewById(R.id.quick_card_subtitle);
        ImageView icon = quickCardView.findViewById(R.id.quick_card_icon);
        MaterialCardView card = quickCardView.findViewById(R.id.quick_access_card);

        if (title != null) title.setText(titleText);
        if (subtitle != null) subtitle.setText(subtitleText);
        if (icon != null) icon.setImageResource(iconResId);

        if (card != null) {
            card.setOnClickListener(v -> {
                Log.d(TAG, "Tarjeta presionada: " + titleText + " -> " + targetActivity.getSimpleName());
                startActivity(new Intent(HomeActivity.this, targetActivity));
            });
        }

        // Hacer visible la tarjeta
        quickCardView.setVisibility(View.VISIBLE);

        Log.d(TAG, "Tarjeta configurada: " + titleText);
    }

    /**
     * Muestra advertencia para usuario inactivo
     */
    private void showInactiveUserWarning() {
        Toast.makeText(this, "Su cuenta está inactiva. Contacte al administrador.", Toast.LENGTH_LONG).show();
    }

    /**
     * Muestra mensaje de error
     */
    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
                        Log.w(FIREBASE_TAG, "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    String token = task.getResult();
                    Log.d(FIREBASE_TAG, "Token FCM: " + token);

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
                    Log.i(FIREBASE_TAG, "Dispositivo registrado correctamente");
                } else {
                    Log.e(FIREBASE_TAG, "Error al registrar dispositivo: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Log.e(FIREBASE_TAG, "Error de red al registrar dispositivo", t);
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
     * Cierra sesión y redirige al login.
     */
    private void logoutUser() {
        Log.d(TAG, "Cerrando sesión de usuario");
        sessionManager.clearSession();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Extrae el userId del JWT token
     */
    private String extractUserIdFromJwt(String jwt) {
        try {
            if (jwt == null || jwt.isEmpty()) {
                Log.e(TAG, "JWT token es null o vacío");
                return null;
            }

            String[] parts = jwt.split("\\.");
            if (parts.length < 2) {
                Log.e(TAG, "JWT token malformado - partes insuficientes");
                return null;
            }

            String payloadJson = new String(Base64.decode(parts[1], Base64.URL_SAFE | Base64.NO_WRAP), "UTF-8");
            JSONObject payload = new JSONObject(payloadJson);

            String extractedUserId = payload.optString("userId", null);
            Log.d(TAG, "UserId extraído del JWT: " + extractedUserId);

            return extractedUserId;
        } catch (UnsupportedEncodingException | JSONException e) {
            Log.e(TAG, "Error al decodificar JWT", e);
            return null;
        }
    }

    /**
     * Getter para acceder a los datos del usuario desde otras partes de la app
     */
    public GetUserDataResponse getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity resumed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeActivity destruida");
    }


}