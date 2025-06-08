package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.GetAllMyAppointments;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.AppointmentAdapter;
import com.example.mediquick.data.model.AppointmentSummary;
import com.example.mediquick.utils.SessionManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "MyAppointments";

    private RecyclerView recycler;
    private TextView txtEmpty;
    private ProgressBar progressBar;
    private List<AppointmentSummary> citas = new ArrayList<>();
    private AppointmentAdapter adapter;

    // API y sesión
    private AppointmentService apiService;
    private SessionManager sessionManager;
    private String userId;
    private String userToken;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        initViews();
        setupSession();
        setupRecyclerView();
        setupApiService();
        cargarCitas();
    }

    private void initViews() {
        recycler = findViewById(R.id.recyclerCitasPaciente);
        txtEmpty = findViewById(R.id.emptyCitas);
        progressBar = findViewById(R.id.progressBarPaciente);

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupSession() {
        sessionManager = new SessionManager(this);
        userToken = sessionManager.getAuthToken();
        userId = extractUserIdFromJwt(userToken);

        Log.d(TAG, "=== CONFIGURANDO SESIÓN ===");
        Log.d(TAG, "UserId extraído: " + userId);
        Log.d(TAG, "Token disponible: " + (userToken != null && !userToken.isEmpty()));
    }

    private void setupRecyclerView() {
        adapter = new AppointmentAdapter(citas, cita -> {
            Log.d(TAG, "Cita seleccionada: " + cita.getAppointmentId());
            Intent intent = new Intent(this, AppointmentDetailActivity.class);
            intent.putExtra("appointmentId", cita.getAppointmentId());
            Toast.makeText(this, "Ver detalles de cita", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "RecyclerView configurado");
    }

    private void setupApiService() {
        if (userToken != null && !userToken.trim().isEmpty()) {
            apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL, userToken)
                    .create(AppointmentService.class);
            Log.d(TAG, "API Service configurado CON autenticación");
        } else {
            apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL)
                    .create(AppointmentService.class);
            Log.w(TAG, "API Service configurado SIN autenticación - Token no disponible");
        }

        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void cargarCitas() {
        if (userId == null || userId.isEmpty()) {
            Log.e(TAG, "No se puede cargar citas: userId es null o vacío");
            showError("Error: Usuario no identificado");
            cargarCitasSimuladas();
            return;
        }

        Log.d(TAG, "=== CARGANDO CITAS DEL USUARIO ===");
        Log.d(TAG, "UserId: " + userId);

        showLoading(true);

        apiService.getAllMyAppointments(userId)
                .enqueue(new Callback<GetAllMyAppointments>() {
                    @Override
                    public void onResponse(Call<GetAllMyAppointments> call, Response<GetAllMyAppointments> response) {
                        showLoading(false);
                        handleAppointmentsResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAllMyAppointments> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleAppointmentsResponse(Response<GetAllMyAppointments> response) {
        Log.d(TAG, "=== RESPUESTA DE CITAS RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAllMyAppointments apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());
            Log.d(TAG, "Total citas recibidas: " + apiResponse.getTotalAppointments());

            if (apiResponse.isSuccess() && apiResponse.hasAppointments()) {
                updateAppointmentsList(apiResponse.getData());
                Toast.makeText(this, "Citas cargadas: " + apiResponse.getTotalAppointments(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ No hay citas disponibles para este usuario");
                showEmptyState("No tienes citas registradas");
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateAppointmentsList(List<GetAllMyAppointments.AppointmentData> appointmentsData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE CITAS ===");

        citas.clear();

        for (GetAllMyAppointments.AppointmentData appointmentData : appointmentsData) {
            AppointmentSummary appointment = convertToAppointmentSummary(appointmentData);
            citas.add(appointment);

            Log.d(TAG, "Cita agregada:");
            Log.d(TAG, "  ID: " + appointment.getAppointmentId());
//            Log.d(TAG, "  Estado: " + appointment.getStatus());
//            Log.d(TAG, "  Sucursal: " + appointment.getClinic());
            Log.d(TAG, "  Fecha: " + appointment.getDateTime());
        }

        Log.i(TAG, "✅ Lista actualizada con " + citas.size() + " citas");

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private AppointmentSummary convertToAppointmentSummary(GetAllMyAppointments.AppointmentData appointmentData) {
        String appointmentId = appointmentData.getMedicalAppointmentId();

        // Formatear fecha y hora
        String dateTime = formatDateTime(appointmentData);

        // Estado de la cita
        String status = appointmentData.getDisplayState();

        // Doctor asignado
        String doctor = getDoctorName(appointmentData);

        // Clínica/Sucursal
        String clinic = getClinicName(appointmentData);

        Log.d(TAG, "Convirtiendo cita: " + appointmentId + " - " + status);

        return new AppointmentSummary(appointmentId, dateTime, status, doctor, clinic);
    }

    private String formatDateTime(GetAllMyAppointments.AppointmentData appointmentData) {
        if (appointmentData.hasScheduledDateTime()) {
            return formatFecha(appointmentData.getMedicalAppointmentDateTime());
        } else {
            // Si no tiene fecha programada, mostrar fecha de creación
            return "Creada: " + formatFecha(appointmentData.getMedicalAppointmentCreatedAt());
        }
    }

    private String getDoctorName(GetAllMyAppointments.AppointmentData appointmentData) {
        if (appointmentData.getDoctorUser() != null) {
            GetAllMyAppointments.DoctorUser doctor = appointmentData.getDoctorUser();

            String doctorDisplayName = doctor.getDisplayName();

            Log.d(TAG, "Doctor asignado: " + doctorDisplayName);
            return doctorDisplayName;
        } else {
            Log.d(TAG, "Sin doctor asignado para esta cita");
            return "Sin doctor asignado";
        }
    }

    private String getClinicName(GetAllMyAppointments.AppointmentData appointmentData) {
        if (appointmentData.getBranch() != null) {
            GetAllMyAppointments.Branch branch = appointmentData.getBranch();
            String branchName = branch.getBranchName();
            String institutionAcronym = "";

            if (branch.getInstitution() != null) {
                institutionAcronym = " (" + branch.getInstitution().getInstitutionAcronym() + ")";
            }

            return branchName + institutionAcronym;
        } else {
            return "Clínica no especificada";
        }
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR CITAS ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();

        // Cargar datos simulados como fallback
        cargarCitasSimuladas();
    }

    private void handleHttpError(Response<GetAllMyAppointments> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
            Toast.makeText(this, "Error HTTP " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
        }

        // Cargar datos simulados como fallback
        cargarCitasSimuladas();
    }

    private void cargarCitasSimuladas() {
        Log.d(TAG, "=== CARGANDO CITAS SIMULADAS (FALLBACK) ===");

        citas.clear();
        citas.add(new AppointmentSummary("sim-1", formatFecha("2025-06-06T12:45:00"), "Created", "Dr. Roberto Martínez", "Clínica Central San Salvador (MINSAL)"));
        citas.add(new AppointmentSummary("sim-2", formatFecha("2025-06-01T08:30:00"), "Completed", "Dr. Ana López", "Clínica Norte Soyapango"));
        citas.add(new AppointmentSummary("sim-3", formatFecha("2025-05-30T10:00:00"), "Canceled", "Sin doctor asignado", "Clínica Mejicanos"));

        Log.i(TAG, "✅ Datos simulados cargados: " + citas.size() + " citas");

        adapter.notifyDataSetChanged();
        updateEmptyState();

        Toast.makeText(this, "Cargando datos de ejemplo", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        progressBar.setVisibility(show ? View.VISIBLE : View.GONE);

        if (show) {
            txtEmpty.setVisibility(View.GONE);
        }

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void updateEmptyState() {
        boolean isEmpty = citas.isEmpty();
        txtEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recycler.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        Log.d(TAG, "Empty state: " + (isEmpty ? "VISIBLE" : "GONE"));
    }

    private void showEmptyState(String message) {
        citas.clear();
        adapter.notifyDataSetChanged();

        txtEmpty.setText(message);
        updateEmptyState();

        Log.d(TAG, "Mostrando empty state: " + message);
    }

    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        updateEmptyState();
    }

    private String formatFecha(String isoDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MMM/yyyy - hh:mm a", Locale.getDefault());
            Date date = input.parse(isoDate);
            return output.format(date);
        } catch (Exception e) {
            Log.w(TAG, "Error formateando fecha: " + isoDate + " - " + e.getMessage());
            return isoDate;
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

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar citas al volver a la actividad
        Log.d(TAG, "MyAppointmentsActivity resumed");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "MyAppointmentsActivity destruida");
    }
}