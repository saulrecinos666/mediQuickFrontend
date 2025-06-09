package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.CreateAppointmentResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.utils.SessionManager;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;

import java.text.NumberFormat;
import java.util.Locale;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConfirmAppointmentActivity extends AppCompatActivity {

    private static final String TAG = "ConfirmAppointment";

    // Views
    private MaterialToolbar toolbar;
    private TextView txtProcedureName;
    private TextView txtBranchName;
    private TextView txtInstitutionName;
    private TextView txtProcedureCost;
    private TextView txtResumenCita;
    private TextView tvLoadingMessage;
    private MaterialButton btnConfirmar;
    private MaterialButton btnCancelar;
    private View loadingOverlay;
    private View costLayout;
    private View institutionLayout;

    // Data
    private SessionManager sessionManager;
    private String procedureId, procedureName, branchId, branchName, institutionName;
    private double procedureCost = 0.0;

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_appointment);

        receiveIntentData();
        initViews();
        setupToolbar();
        setupApiService();
        setupClickListeners();
        displayAppointmentSummary();
    }

    private void receiveIntentData() {
        procedureId = getIntent().getStringExtra("procedureId");
        procedureName = getIntent().getStringExtra("procedureName");
        branchId = getIntent().getStringExtra("branchId");
        branchName = getIntent().getStringExtra("branchName");
        institutionName = getIntent().getStringExtra("institutionName");
        procedureCost = getIntent().getDoubleExtra("procedureCost", 0.0);

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Procedure ID: " + procedureId);
        Log.d(TAG, "Procedure Name: " + procedureName);
        Log.d(TAG, "Branch ID: " + branchId);
        Log.d(TAG, "Branch Name: " + branchName);
        Log.d(TAG, "Institution Name: " + institutionName);
        Log.d(TAG, "Procedure Cost: " + procedureCost);

        // Validar datos críticos
        if (procedureId == null || procedureId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: procedureId está vacío o es null");
        }
        if (branchId == null || branchId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: branchId está vacío o es null");
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        txtProcedureName = findViewById(R.id.txtProcedureName);
        txtBranchName = findViewById(R.id.txtBranchName);
        txtInstitutionName = findViewById(R.id.txtInstitutionName);
        txtProcedureCost = findViewById(R.id.txtProcedureCost);
        txtResumenCita = findViewById(R.id.txtResumenCita);
        tvLoadingMessage = findViewById(R.id.tvLoadingMessage);
        btnConfirmar = findViewById(R.id.btnConfirmarCita);
        btnCancelar = findViewById(R.id.btnCancelar);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        costLayout = findViewById(R.id.costLayout);
        institutionLayout = findViewById(R.id.institutionLayout);

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupToolbar() {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        toolbar.setNavigationOnClickListener(v -> {
            Log.d(TAG, "Navigation back pressed");
            onBackPressed();
        });
    }

    private void setupApiService() {
        sessionManager = new SessionManager(this);
        String jwt = sessionManager.getAuthToken();

        Log.d(TAG, "JWT Token presente: " + (jwt != null && !jwt.isEmpty()));

        if (jwt != null && !jwt.trim().isEmpty()) {
            apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL, jwt)
                    .create(AppointmentService.class);
            Log.d(TAG, "API Service configurado CON autenticación");
        } else {
            apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL)
                    .create(AppointmentService.class);
            Log.w(TAG, "API Service configurado SIN autenticación - Token no disponible");
        }
    }

    private void setupClickListeners() {
        btnConfirmar.setOnClickListener(v -> {
            Log.d(TAG, "Botón confirmar presionado");
            confirmarCita();
        });

        btnCancelar.setOnClickListener(v -> {
            Log.d(TAG, "Botón cancelar presionado");
            onBackPressed();
        });
    }

    private void displayAppointmentSummary() {
        // Configurar nombre del procedimiento
        if (txtProcedureName != null) {
            txtProcedureName.setText(procedureName != null ? procedureName : "Procedimiento no especificado");
        }

        // Configurar nombre de la sucursal
        if (txtBranchName != null) {
            txtBranchName.setText(branchName != null ? branchName : "Sucursal no especificada");
        }

        // Configurar nombre de la institución
        if (txtInstitutionName != null && institutionLayout != null) {
            if (institutionName != null && !institutionName.trim().isEmpty()) {
                txtInstitutionName.setText(institutionName);
                institutionLayout.setVisibility(View.VISIBLE);
            } else {
                institutionLayout.setVisibility(View.GONE);
            }
        }

        // Configurar costo
        if (txtProcedureCost != null && costLayout != null) {
            if (procedureCost > 0) {
                String formattedCost = formatCurrency(procedureCost);
                txtProcedureCost.setText(formattedCost);
                costLayout.setVisibility(View.VISIBLE);
            } else {
                costLayout.setVisibility(View.GONE);
            }
        }

        // Configurar resumen dinámico
        if (txtResumenCita != null) {
            String summary = buildDynamicSummary();
            txtResumenCita.setText(summary);
        }

        Log.d(TAG, "Resumen de cita configurado");
    }

    private String formatCurrency(double amount) {
        try {
            NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
            return formatter.format(amount);
        } catch (Exception e) {
            return "$" + String.format("%.2f", amount);
        }
    }

    private String buildDynamicSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Al confirmar, se enviará una solicitud de cita para ");

        if (procedureName != null) {
            summary.append("\"").append(procedureName).append("\"");
        } else {
            summary.append("el procedimiento seleccionado");
        }

        if (branchName != null) {
            summary.append(" en ").append(branchName);
        }

        summary.append(".\n\nRecibirás una notificación cuando el personal médico confirme tu cita.");

        return summary.toString();
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
            showErrorMessage("Error: Datos de cita incompletos");
            return;
        }

        if (!isNetworkAvailable()) {
            showErrorMessage("Error: Sin conexión a internet");
            return;
        }

        Log.d(TAG, "=== INICIANDO CREACIÓN DE CITA ===");

        // Verificar sessionManager
        if (sessionManager != null) {
            String token = sessionManager.getAuthToken();
            Log.d(TAG, "Token disponible: " + (token != null && !token.trim().isEmpty()));
        } else {
            Log.e(TAG, "❌ SessionManager es null");
            showErrorMessage("Error: Sesión no disponible");
            return;
        }

        // Mostrar loading
        showLoading(true, "Creando tu cita...");

        // Crear request bodies para multipart
        RequestBody branchIdBody = RequestBody.create(
                okhttp3.MediaType.parse("text/plain"), branchId);
        RequestBody medicalProcedureIdBody = RequestBody.create(
                okhttp3.MediaType.parse("text/plain"), procedureId);

        Log.d(TAG, "Enviando request a la API");

        // Enviar request a la API
        Call<CreateAppointmentResponse> call = apiService.createAppointment(branchIdBody, medicalProcedureIdBody);

        call.enqueue(new Callback<CreateAppointmentResponse>() {
            @Override
            public void onResponse(Call<CreateAppointmentResponse> call,
                                   Response<CreateAppointmentResponse> response) {
                showLoading(false, null);
                handleAppointmentResponse(response);
            }

            @Override
            public void onFailure(Call<CreateAppointmentResponse> call, Throwable t) {
                showLoading(false, null);
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

        if (response.isSuccessful() && response.body() != null) {
            CreateAppointmentResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());

            if (apiResponse.isSuccess()) {
                Log.i(TAG, "✅ Cita creada exitosamente");
                showSuccessAndRedirectToHome(apiResponse.getMessage());
            } else {
                Log.w(TAG, "❌ Error de la API: " + apiResponse.getMessage());
                showErrorMessage("Error: " + apiResponse.getMessage());
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void handleHttpError(Response<CreateAppointmentResponse> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);

            try {
                CreateAppointmentResponse errorResponse = new Gson()
                        .fromJson(errorBody, CreateAppointmentResponse.class);
                if (errorResponse != null && errorResponse.getMessage() != null) {
                    showErrorMessage("Error: " + errorResponse.getMessage());
                } else {
                    showErrorMessage("Error HTTP " + response.code());
                }
            } catch (Exception e) {
                showErrorMessage("Error HTTP " + response.code());
            }

        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            showErrorMessage("Error HTTP " + response.code());
        }
    }

    private void handleAppointmentError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CREAR CITA ===");
        Log.e(TAG, "Error: " + t.getMessage());
        t.printStackTrace();

        showErrorMessage("Error de conexión. Verifica tu conexión a internet");
    }

    private void showLoading(boolean show, String message) {
        if (loadingOverlay != null) {
            loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (btnConfirmar != null) {
            btnConfirmar.setEnabled(!show);
        }

        if (btnCancelar != null) {
            btnCancelar.setEnabled(!show);
        }

        if (tvLoadingMessage != null && message != null) {
            tvLoadingMessage.setText(message);
        }

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void showErrorMessage(String message) {
        Log.e(TAG, "Mostrando error: " + message);

        if (findViewById(android.R.id.content) != null) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG)
                    .setBackgroundTint(getResources().getColor(R.color.error))
                    .setTextColor(getResources().getColor(android.R.color.white))
                    .setAction("Reintentar", v -> confirmarCita())
                    .show();
        }
    }

    private void showSuccessMessage(String message) {
        if (findViewById(android.R.id.content) != null) {
            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_SHORT)
                    .setBackgroundTint(getResources().getColor(R.color.success))
                    .setTextColor(getResources().getColor(android.R.color.white))
                    .show();
        }
    }

    private void showSuccessAndRedirectToHome(String message) {
        Log.i(TAG, "🎉 Mostrando éxito y redirigiendo al Home: " + message);

        String successMessage = message != null && !message.isEmpty() ?
                message : "✅ Cita creada exitosamente. Recibirás notificación cuando esté agendada";

        showSuccessMessage(successMessage);

        // Mostrar loading de éxito
        showLoading(true, "¡Cita creada exitosamente!");

        // Redirigir al home después de un delay
        if (txtResumenCita != null) {
            txtResumenCita.postDelayed(() -> {
                Log.d(TAG, "Redirigiendo a HomeActivity después de crear cita exitosamente");
                redirectToHome();
            }, 2000);
        }
    }

    private void redirectToHome() {
        Intent intent = new Intent(ConfirmAppointmentActivity.this, HomeActivity.class);

        // Limpiar el stack de navegación
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        // Agregar extras para mostrar mensaje de éxito en Home
        intent.putExtra("show_success_message", true);
        intent.putExtra("success_message", "¡Cita creada exitosamente!");

        Log.d(TAG, "Iniciando HomeActivity con flags para limpiar stack");
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button presionado");
        super.onBackPressed();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ConfirmAppointmentActivity destruida");
    }
}