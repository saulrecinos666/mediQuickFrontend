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
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HomeActivity extends AppCompatActivity {

    private static final String TAG = "HomeActivity";
    private static final String FIREBASE_TAG = "FIREBASE";
    private static final int NOTIFICATION_PERMISSION_CODE = 1;

    // Animation delays
    private static final int LOADING_TRANSITION_DELAY = 500;
    private static final int SUCCESS_MESSAGE_DELAY = 1000;
    private static final int ERROR_FALLBACK_DELAY = 800;

    private DrawerLayout drawerLayout;
    private ImageButton menuButton;
    private SessionManager sessionManager;

    // Loading state views
    private LinearLayout loadingContainer;
    private LinearLayout mainContent;
    private TextView loadingText;

    private String userId;
    private String userToken;
    private GetUserDataResponse currentUser;

    // Cache para contadores de tarjetas
    private Map<String, Integer> cardCounters = new HashMap<>();

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
        checkForSuccessMessage();
    }

    /**
     * Inicializa las vistas y configura listeners básicos
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

        showLoadingState("Cargando tu perfil...");

        if (userId != null && !userId.isEmpty()) {
            fetchUserData();
        } else {
            Log.e(TAG, "❌ No se pudo extraer userId del JWT");
            handleFallbackRole();
        }
    }

    /**
     * Maneja el rol cuando no se puede obtener el userId
     */
    private void handleFallbackRole() {
        String roleFromJwt = extractUserRoleFromJwt(userToken);
        if (roleFromJwt != null) {
            updateLoadingText("Configurando tu espacio de trabajo...");
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                setupQuickAccessCardsWithRole(roleFromJwt);
                hideLoadingState();
            }, ERROR_FALLBACK_DELAY);
        } else {
            setupQuickAccessCardsWithDefaultRole();
            hideLoadingState();
        }
    }

    /**
     * Obtiene los datos del usuario desde la API
     */
    private void fetchUserData() {
        Log.d(TAG, "=== OBTENIENDO DATOS DEL USUARIO ===");
        Log.d(TAG, "Consultando usuario con ID: " + userId);

        updateLoadingText("Obteniendo tu información...");

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
            logUserInfo();

            updateLoadingText("Configurando tu dashboard...");

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                updateUIWithUserData();
                hideLoadingState();
            }, LOADING_TRANSITION_DELAY);

            if (!currentUser.isActive()) {
                Log.w(TAG, "⚠️ Usuario inactivo - considerar logout");
                showInactiveUserWarning();
            }

        } else {
            handleUserDataResponseError(response);
        }
    }

    /**
     * Registra información del usuario para debugging
     */
    private void logUserInfo() {
        Log.i(TAG, "✅ Datos de usuario obtenidos exitosamente");
        Log.d(TAG, "Usuario: " + currentUser.getDisplayName());
        Log.d(TAG, "Email: " + currentUser.getUserEmail());
        Log.d(TAG, "Es paciente: " + currentUser.isPatient());
        Log.d(TAG, "Está activo: " + currentUser.isActive());
    }

    /**
     * Maneja errores en la respuesta de datos del usuario
     */
    private void handleUserDataResponseError(Response<GetUserDataResponse> response) {
        Log.e(TAG, "❌ Error HTTP al obtener datos de usuario: " + response.code());
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
        }

        updateLoadingText("Configurando opciones predeterminadas...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showError("Error al cargar datos de usuario");
            setupQuickAccessCardsWithDefaultRole();
            hideLoadingState();
        }, ERROR_FALLBACK_DELAY);
    }

    /**
     * Maneja errores al obtener datos del usuario
     */
    private void handleUserDataError(Throwable t) {
        Log.e(TAG, "=== ERROR AL OBTENER DATOS DE USUARIO ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        updateLoadingText("Cargando opciones básicas...");

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            showError("Error de conexión al cargar perfil");
            setupQuickAccessCardsWithDefaultRole();
            hideLoadingState();
        }, ERROR_FALLBACK_DELAY);
    }

    /**
     * Muestra el loading state
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
     * Oculta el loading state
     */
    private void hideLoadingState() {
        runOnUiThread(() -> {
            loadingContainer.setVisibility(View.GONE);
            mainContent.setVisibility(View.VISIBLE);
            Log.d(TAG, "Loading state ocultado - Contenido principal visible");
        });
    }

    /**
     * Actualiza el texto del loading
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
                updateNavigationHeader();
                updateWelcomeMessage();
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

        // Descomenta cuando tengas las vistas en el header
        /*
        if (headerView != null && currentUser != null) {
            TextView nameTextView = headerView.findViewById(R.id.nav_header_name);
            TextView emailTextView = headerView.findViewById(R.id.nav_header_email);

            if (nameTextView != null) {
                nameTextView.setText(currentUser.getDisplayName());
            }

            if (emailTextView != null) {
                emailTextView.setText(currentUser.getUserEmail());
            }
        }
        */
    }

    /**
     * Actualiza el mensaje de bienvenida
     */
    private void updateWelcomeMessage() {
        TextView greetingTitle = findViewById(R.id.greeting_title);
        TextView greetingSubtitle = findViewById(R.id.greeting_subtitle);

        if (greetingTitle != null && currentUser != null) {
            String welcomeMessage = "Hola, " + currentUser.getDisplayName() + "!";
            greetingTitle.setText(welcomeMessage);
            Log.d(TAG, "Mensaje de bienvenida actualizado: " + welcomeMessage);
        }

        if (greetingSubtitle != null) {
            greetingSubtitle.setText(getTimeBasedGreeting());
        }

        TextView welcomeTitle = findViewById(R.id.welcome_title);
        if (welcomeTitle != null && currentUser != null) {
            welcomeTitle.setText("Bienvenido, " + currentUser.getUserFirstName() + "!");
        }

        TextView welcomeSubtitle = findViewById(R.id.welcome_subtitle);
        if (welcomeSubtitle != null && currentUser != null) {
            if (currentUser.isPatient()) {
                welcomeSubtitle.setText("¿Listo para agendar tu próxima cita?");
            } else {
                welcomeSubtitle.setText("Administremos tu agenda del día");
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
            return "Buenos días";
        } else if (hour >= 12 && hour < 19) {
            return "Buenas tardes";
        } else {
            return "Buenas noches";
        }
    }

    /**
     * Configura tarjetas con rol por defecto (PACIENTE)
     */
    private void setupQuickAccessCardsWithDefaultRole() {
        Log.d(TAG, "Configurando tarjetas con rol por defecto (PACIENTE)");
        hideAllQuickCards();
        setupPatientCards();
    }

    /**
     * Configura tarjetas con rol específico
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

        hideAllQuickCards();

        String userRole = getUserRole();
        Log.d(TAG, "Rol del usuario: " + userRole);

        if (userRole == null) {
            Log.w(TAG, "⚠️ Rol de usuario no encontrado, mostrando opciones básicas");
            userRole = "PACIENTE";
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
     * Configura tarjetas para SUPER_ADMIN
     */
    private void setupSuperAdminCards() {
        Log.d(TAG, "Configurando tarjetas para SUPER_ADMIN");

        setupQuickCard(
                R.id.card1,
                "Mensajes",
                "2 conversaciones activas",
                R.drawable.ic_chat_bubble_outline,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Gestión de Citas",
                "5 pendientes de aprobar",
                R.drawable.ic_event_available,
                AdminAcceptAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Mi Agenda",
                "3 citas hoy",
                R.drawable.ic_calendar_today,
                AssignedAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card4,
                "Nueva Cita",
                "Agendar como paciente",
                R.drawable.ic_add_circle_outline,
                InstitutionListActivity.class
        );

        setupQuickCard(
                R.id.card5,
                "Historial Médico",
                "Ver todo",
                R.drawable.ic_history,
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
                "Mensajes",
                "Sin mensajes nuevos",
                R.drawable.ic_chat_bubble_outline,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Gestión de Citas",
                "3 por confirmar",
                R.drawable.ic_event_available,
                AdminAcceptAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Historial",
                "Registro completo",
                R.drawable.ic_history,
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
                "Mensajes",
                "1 mensaje nuevo",
                R.drawable.ic_chat_bubble_outline,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Mi Agenda",
                "4 pacientes hoy",
                R.drawable.ic_calendar_today,
                AssignedAppointmentsActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Historial",
                "Últimas consultas",
                R.drawable.ic_history,
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
                "Mensajes",
                "Contacta a tu médico",
                R.drawable.ic_chat_bubble_outline,
                ChatListActivity.class
        );

        setupQuickCard(
                R.id.card2,
                "Agendar Cita",
                "Encuentra especialistas",
                R.drawable.ic_add_circle_outline,
                InstitutionListActivity.class
        );

        setupQuickCard(
                R.id.card3,
                "Mis Citas",
                "Próxima: " + getNextAppointmentDate(),
                R.drawable.ic_event_note,
                MyAppointmentsActivity.class
        );
    }

    /**
     * Método mejorado para configurar tarjetas con más opciones
     */
    private void setupQuickCard(int cardIncludeId, String titleText, String subtitleText,
                                int iconResId, Class<?> targetActivity) {
        View quickCardView = findViewById(cardIncludeId);

        if (quickCardView == null) {
            Log.w(TAG, "No se encontró la tarjeta con ID: " + cardIncludeId);
            return;
        }

        // Referencias a las vistas
        TextView title = quickCardView.findViewById(R.id.quick_card_title);
        TextView subtitle = quickCardView.findViewById(R.id.quick_card_subtitle);
        TextView dateView = quickCardView.findViewById(R.id.quick_card_date);
        TextView totalNumber = quickCardView.findViewById(R.id.quick_card_total_number);
        TextView totalLabel = quickCardView.findViewById(R.id.quick_card_total_label);
        ImageView icon = quickCardView.findViewById(R.id.quick_card_icon);
        MaterialCardView card = quickCardView.findViewById(R.id.quick_access_card);
        ImageButton menuButton = quickCardView.findViewById(R.id.quick_card_menu);

        // Configurar textos
        if (title != null) title.setText(titleText);
        if (subtitle != null) subtitle.setText(subtitleText);

        // Configurar fecha actual
        if (dateView != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy", new Locale("es", "ES"));
            dateView.setText(sdf.format(new Date()));
        }

        // Configurar ícono
        if (icon != null) icon.setImageResource(iconResId);

        // Configurar color de fondo
//        if (card != null && bgColor != null) {
//            try {
//                card.setCardBackgroundColor(android.graphics.Color.parseColor(bgColor));
//            } catch (Exception e) {
//                Log.e(TAG, "Error al parsear color: " + bgColor);
//            }
//        }

        // Configurar contador
        if (totalNumber != null && totalLabel != null) {
            int count = getCounterForCard(titleText);
            totalNumber.setText(String.valueOf(count));
            totalLabel.setText(getLabelForCard(titleText));

            View totalContainer = quickCardView.findViewById(R.id.quick_card_total_container);
            if (totalContainer != null) {
                totalContainer.setVisibility(View.VISIBLE);
            }
        } else {
            View totalContainer = quickCardView.findViewById(R.id.quick_card_total_container);
            if (totalContainer != null) {
                totalContainer.setVisibility(View.GONE);
            }
        }

        // Configurar click listener
        if (card != null) {
            card.setOnClickListener(v -> {
                Log.d(TAG, "Tarjeta presionada: " + titleText + " -> " + targetActivity.getSimpleName());
                startActivity(new Intent(HomeActivity.this, targetActivity));
            });
        }

        // Configurar menú contextual
        if (menuButton != null) {
            menuButton.setOnClickListener(v -> showCardContextMenu(titleText));
        }

        // Hacer visible la tarjeta
        quickCardView.setVisibility(View.VISIBLE);

        Log.d(TAG, "Tarjeta configurada: " + titleText);
    }

    /**
     * Obtiene el contador para cada tipo de tarjeta
     */
    private int getCounterForCard(String cardTitle) {
        // Aquí podrías hacer llamadas a la API para obtener contadores reales
        // Por ahora retornamos valores de ejemplo
        switch (cardTitle) {
            case "Mensajes":
                return 2;
            case "Gestión de Citas":
                return 5;
            case "Mi Agenda":
                return 3;
            case "Mis Citas":
                return 1;
            default:
                return 0;
        }
    }

    /**
     * Obtiene la etiqueta del contador según el tipo de tarjeta
     */
    private String getLabelForCard(String cardTitle) {
        switch (cardTitle) {
            case "Mensajes":
                return "Sin leer";
            case "Gestión de Citas":
                return "Pendientes";
            case "Mi Agenda":
                return "Citas hoy";
            case "Mis Citas":
                return "Esta semana";
            default:
                return "Total";
        }
    }

    /**
     * Obtiene la fecha de la próxima cita (placeholder)
     */
    private String getNextAppointmentDate() {
        // Aquí deberías obtener la fecha real de la próxima cita
        // Por ahora retornamos un ejemplo
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", new Locale("es", "ES"));
        return sdf.format(new Date());
    }

    /**
     * Muestra menú contextual para la tarjeta
     */
    private void showCardContextMenu(String cardTitle) {
        // Aquí podrías implementar un menú contextual
        Log.d(TAG, "Menú contextual para: " + cardTitle);
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
     * Verifica si hay mensajes de éxito para mostrar
     */
    private void checkForSuccessMessage() {
        Intent intent = getIntent();
        if (intent != null && intent.getBooleanExtra("show_success_message", false)) {
            String successMessage = intent.getStringExtra("success_message");

            if (successMessage != null && !successMessage.isEmpty()) {
                Log.d(TAG, "Mostrando mensaje de éxito desde intent: " + successMessage);

                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    showSuccessMessage(successMessage);
                }, SUCCESS_MESSAGE_DELAY);
            }
        }
    }

    /**
     * Muestra un mensaje de éxito elegante
     */
    private void showSuccessMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
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
     * Aplica márgenes seguros al layout
     */
    private void applyWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }

    /**
     * Verifica y solicita permisos para notificaciones
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
     * Obtiene el token de Firebase Cloud Messaging
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

                    sendDeviceToBackend(userId, token);
                });
    }

    /**
     * Envía el dispositivo al backend para notificaciones push
     */
    private void sendDeviceToBackend(String userId, String token) {
        if (userId == null || token == null) {
            Log.w(FIREBASE_TAG, "No se puede registrar dispositivo: userId o token null");
            return;
        }

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
     * Configura el menú lateral y el botón de apertura
     */
    private void setupDrawerMenu() {
        drawerLayout = findViewById(R.id.drawer_layout);
        menuButton = findViewById(R.id.button);

        menuButton.setOnClickListener(v -> {
            if (!drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });
    }

    /**
     * Inicializa el NavigationView y su listener
     */
    private void initNavigationView() {
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(menuItem -> {
            int itemId = menuItem.getItemId();

//            if (itemId == R.id.nav_logout) {
//                logoutUser();
//                return true;
//            } else if (itemId == R.id.nav_profile) {
//                // Navegar a perfil
//                //startActivity(new Intent(this, ProfileActivity.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            } else if (itemId == R.id.nav_settings) {
//                // Navegar a configuración
//                startActivity(new Intent(this, SettingsActivity.class));
//                drawerLayout.closeDrawer(GravityCompat.START);
//                return true;
//            }

            return false;
        });
    }

    /**
     * Cierra sesión y redirige al login
     */
    private void logoutUser() {
        Log.d(TAG, "Cerrando sesión de usuario");

        // Limpiar token FCM del backend antes de cerrar sesión
        clearDeviceFromBackend();

        sessionManager.clearSession();

        Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Limpia el dispositivo del backend antes de cerrar sesión
     */
    private void clearDeviceFromBackend() {
        // Implementar si el backend tiene endpoint para eliminar dispositivo
        Log.d(TAG, "Limpiando dispositivo del backend");
    }

    /**
     * Getter para acceder a los datos del usuario
     */
    public GetUserDataResponse getCurrentUser() {
        return currentUser;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "HomeActivity resumed");

        // Actualizar contadores cuando se vuelve a la actividad
        updateCardCounters();
    }

    /**
     * Actualiza los contadores de las tarjetas
     */
    private void updateCardCounters() {
        // Aquí podrías hacer llamadas a la API para actualizar contadores
        Log.d(TAG, "Actualizando contadores de tarjetas");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "HomeActivity paused");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "HomeActivity destruida");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(TAG, "Permiso de notificaciones concedido");
                obtenerTokenFCM();
            } else {
                Log.w(TAG, "Permiso de notificaciones denegado");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}