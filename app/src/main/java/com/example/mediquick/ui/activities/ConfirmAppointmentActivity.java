package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.CreateAppointmentRequest;
import com.example.mediquick.data.model.CreateAppointmentResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.utils.SessionManager;
import com.google.gson.Gson;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmAppointment";

    private TextView txtResumen;
    private Button btnConfirmar;
    private ProgressBar progressBar;
    private SessionManager sessionManager;
    // Datos recibidos del Intent
    private String procedureId, procedureName, branchId, branchName;

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        receiveIntentData();
        initViews();
        setupApiService();
        setupClickListeners();

        displayAppointmentSummary();
    }

    private void receiveIntentData() {
        procedureId = getIntent().getStringExtra("procedureId");
        procedureName = getIntent().getStringExtra("procedureName");
        branchId = getIntent().getStringExtra("branchId");
        branchName = getIntent().getStringExtra("branchName");

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Procedure ID: " + procedureId);
        Log.d(TAG, "Procedure Name: " + procedureName);
        Log.d(TAG, "Branch ID: " + branchId);
        Log.d(TAG, "Branch Name: " + branchName);

        // Validar datos recibidos
        if (procedureId == null || procedureId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: procedureId está vacío o es null");
        } else {
            Log.i(TAG, "✅ procedureId válido: " + procedureId);
        }

        if (branchId == null || branchId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: branchId está vacío o es null");
        } else {
            Log.i(TAG, "✅ branchId válido: " + branchId);
        }
    }

    private void initViews() {
        txtResumen = findViewById(R.id.txtResumenCita);
        btnConfirmar = findViewById(R.id.btnConfirmarCita);
        progressBar = findViewById(R.id.progressBarConfirmar);

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupApiService() {
        // ✅ AGREGAR ESTA LÍNEA - Inicializar SessionManager
        sessionManager = new SessionManager(this);

        String jwt = sessionManager.getAuthToken();

        Log.d(TAG, "JWT Token presente: " + (jwt != null && !jwt.isEmpty()));

        // Usar cliente autenticado si hay token
        if (jwt != null && !jwt.trim().isEmpty()) {
            apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL, jwt).create(AppointmentService.class);
            Log.d(TAG, "API Service configurado CON autenticación");
        } else {
            apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL).create(AppointmentService.class);
            Log.w(TAG, "API Service configurado SIN autenticación - Token no disponible");
        }

        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupClickListeners() {
        btnConfirmar.setOnClickListener(v -> {
            Log.d(TAG, "Botón confirmar presionado");
            confirmarCita();
        });
    }

    private void displayAppointmentSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("¿Deseas agendar una cita para:\n\n");
        summary.append("📋 Procedimiento: ").append(procedureName != null ? procedureName : "No especificado").append("\n");
        if (branchName != null && !branchName.isEmpty()) {
            summary.append("🏥 Sucursal: ").append(branchName).append("\n");
        }
        summary.append("\n¡Recibirás una notificación cuando esté agendada!");

        txtResumen.setText(summary.toString());

        Log.d(TAG, "Resumen mostrado: " + summary.toString());
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean connected = activeNetworkInfo != null && activeNetworkInfo.isConnected();

        Log.d(TAG, "Red disponible: " + connected);
        return connected;
    }

    private void confirmarCita() {
        // Validar datos antes de enviar
        if (!isDataValid()) {
            showError("Error: Datos de cita incompletos");
            return;
        }

        if (!isNetworkAvailable()) {
            showError("Error: Sin conexión a internet");
            return;
        }

        Log.d(TAG, "=== INICIANDO CREACIÓN DE CITA ===");
        Log.d(TAG, "Procedure ID: '" + procedureId + "' (length: " + procedureId.length() + ")");
        Log.d(TAG, "Branch ID: '" + branchId + "' (length: " + branchId.length() + ")");

        // Verificar sessionManager
        if (sessionManager != null) {
            String token = sessionManager.getAuthToken();
            Log.d(TAG, "Token disponible: " + (token != null && !token.trim().isEmpty()));
            if (token != null) {
                Log.d(TAG, "Token length: " + token.length());
            }
        } else {
            Log.e(TAG, "❌ SessionManager es null - Esto es un error crítico");
            showError("Error: Sesión no disponible");
            return;
        }

        // Mostrar loading
        showLoading(true);

        Log.d(TAG, "=== ENVIANDO MULTIPART FORM DATA ===");
        Log.d(TAG, "Usando @Multipart con @Part");

        // ✅ CREAR REQUEST BODIES PARA MULTIPART
        RequestBody branchIdBody = RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                branchId
        );

        RequestBody medicalProcedureIdBody = RequestBody.create(
                okhttp3.MediaType.parse("text/plain"),
                procedureId
        );

        Log.d(TAG, "RequestBodies creados correctamente");

        // Enviar request a la API usando multipart data
        Call<CreateAppointmentResponse> call = apiService.createAppointment(branchIdBody, medicalProcedureIdBody);

        // Log del request antes de enviarlo
        Log.d(TAG, "Call creado: " + call.getClass().getSimpleName());
        Log.d(TAG, "Request URL: " + call.request().url());
        Log.d(TAG, "Request Method: " + call.request().method());

        call.enqueue(new Callback<CreateAppointmentResponse>() {
            @Override
            public void onResponse(Call<CreateAppointmentResponse> call, Response<CreateAppointmentResponse> response) {
                Log.d(TAG, "=== HEADERS DE LA REQUEST ENVIADA ===");
                Log.d(TAG, "Final Content-Type: " + call.request().header("Content-Type"));
                Log.d(TAG, "Final Authorization: " + call.request().header("Authorization"));

                showLoading(false);
                handleAppointmentResponse(response);
            }

            @Override
            public void onFailure(Call<CreateAppointmentResponse> call, Throwable t) {
                Log.e(TAG, "=== REQUEST FAILURE ===");
                Log.e(TAG, "Request that failed: " + call.request().url());
                showLoading(false);
                handleAppointmentError(t);
            }
        });
    }

    private boolean isDataValid() {
        boolean isValid = procedureId != null && !procedureId.trim().isEmpty() &&
                branchId != null && !branchId.trim().isEmpty();

        Log.d(TAG, "Validación de datos: " + (isValid ? "VÁLIDA" : "INVÁLIDA"));
        return isValid;
    }

    private void handleAppointmentResponse(Response<CreateAppointmentResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE CREACIÓN DE CITA RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            CreateAppointmentResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());

            if (apiResponse.isSuccess()) {
                // ✅ ÉXITO - REDIRIGIR AL HOME
                Log.i(TAG, "✅ Cita creada exitosamente");
                showSuccessAndRedirectToHome(apiResponse.getMessage());
            } else {
                // API respondió pero con error
                Log.w(TAG, "❌ Error de la API: " + apiResponse.getMessage());
                showError("Error: " + apiResponse.getMessage());
            }
        } else {
            // Error HTTP
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void handleHttpError(Response<CreateAppointmentResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);

            // Intentar parsear el error body como JSON
            try {
                CreateAppointmentResponse errorResponse = new Gson().fromJson(errorBody, CreateAppointmentResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null) {
                    showError("Error: " + errorResponse.getMessage());
                } else {
                    showError("Error HTTP " + response.code() + ": " + errorBody);
                }
            } catch (Exception e) {
                showError("Error HTTP " + response.code() + ": " + errorBody);
            }

        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            showError("Error HTTP " + response.code());
        }
    }

    private void handleAppointmentError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CREAR CITA ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        showError("Error de conexión: " + t.getMessage());
    }

    private void showLoading(boolean show) {
        btnConfirmar.setEnabled(!show);
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            btnConfirmar.setText("Creando cita...");
        } else {
            btnConfirmar.setText("Confirmar Cita");
        }

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * ✅ NUEVO MÉTODO: Muestra éxito y redirige al HomeActivity
     */
    private void showSuccessAndRedirectToHome(String message) {
        Log.i(TAG, "🎉 Mostrando éxito y redirigiendo al Home: " + message);

        String successMessage = message != null && !message.isEmpty() ?
                message : "✅ Cita creada exitosamente. Recibirás notificación cuando esté agendada";

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // ✅ REDIRIGIR AL HOME DESPUÉS DE UN BREVE DELAY
        txtResumen.postDelayed(() -> {
            Log.d(TAG, "Redirigiendo a HomeActivity después de crear cita exitosamente");
            redirectToHome();
        }, 1500);
    }

    /**
     * ✅ NUEVO MÉTODO: Redirige al HomeActivity limpiando el stack
     */
    private void redirectToHome() {
        Intent intent = new Intent(ConfirmAppointmentActivity.this, HomeActivity.class);

        // ✅ LIMPIAR TODO EL STACK DE NAVEGACIÓN
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // ✅ OPCIONAL: Agregar extra para mostrar mensaje de éxito en Home
        intent.putExtra("show_success_message", true);
        intent.putExtra("success_message", "¡Cita creada exitosamente!");

        Log.d(TAG, "Iniciando HomeActivity con flags para limpiar stack");
        startActivity(intent);

        // Finalizar esta actividad
        finish();
    }

    /**
     * ✅ MÉTODO LEGACY: Mantener para casos donde solo se cierre sin redirigir
     */
    private void showSuccessAndFinish(String message) {
        Log.i(TAG, "🎉 Mostrando éxito y cerrando: " + message);

        String successMessage = message != null && !message.isEmpty() ?
                message : "✅ Cita creada. Recibirás notificación cuando esté agendada";

        Toast.makeText(this, successMessage, Toast.LENGTH_LONG).show();

        // Cerrar la actividad después de un breve delay para que el usuario vea el mensaje
        txtResumen.postDelayed(() -> {
            Log.d(TAG, "Cerrando ConfirmAppointmentActivity");
            finish();
        }, 1500);
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button presionado");
        super.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ConfirmAppointmentActivity destruida");
    }
}