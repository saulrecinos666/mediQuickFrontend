package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.AppointmentCard;
import com.example.mediquick.data.model.GetAssignedAppointmentsResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.PrescriptionAdapter;
import com.example.mediquick.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignedAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private List<AppointmentCard> citasAsignadas;
    private AppointmentService apiService;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SessionManager sessionManager;

    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_appointments);

        sessionManager = new SessionManager(this);


        initViews();
        setupRecyclerView();
        setupApiService();
        loadAssignedAppointments();
        setupSwipeRefresh();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerCitas);
        progressBar = findViewById(R.id.progressBar); // Agregar al layout si no existe
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Agregar al layout si no existe
        citasAsignadas = new ArrayList<>();

        // Nuevos elementos
        TextView chipCount = findViewById(R.id.chipCount);
        LinearLayout layoutEmptyState = findViewById(R.id.layoutEmptyState);
        FloatingActionButton fabRefresh = findViewById(R.id.fabRefresh);
        MaterialButton btnRefresh = findViewById(R.id.btnRefresh);

        citasAsignadas = new ArrayList<>();

        // Configurar listeners
        fabRefresh.setOnClickListener(v -> loadAssignedAppointments());
        btnRefresh.setOnClickListener(v -> loadAssignedAppointments());
    }

    private void setupRecyclerView() {
        adapter = new PrescriptionAdapter(citasAsignadas, cita -> {
            Intent i = new Intent(this, CreatePrescriptionActivity.class);
            i.putExtra("appointmentId", cita.getId());
            i.putExtra("paciente", cita.getPaciente());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    private void setupApiService() {

        String jwt = sessionManager.getAuthToken();

        // Opción 1: Sin autenticación
        apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL + "/", jwt).create(AppointmentService.class);

        // Opción 2: Con autenticación (si es necesario)
        // String token = getTokenFromSharedPreferences();
        // apiService = ApiClient.getAuthenticatedClient(baseUrl, token).create(AppointmentApiService.class);
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                loadAssignedAppointments();
            });
        }
    }

    private void loadAssignedAppointments() {
        showLoading(true);
        String jwt = sessionManager.getAuthToken();

        userId = extractUserIdFromJwt(jwt);
        if (userId == null) {
            showToast("Token inválido");
            finish();
            return;
        }
        // Opción 1: Obtener todas las citas asignadas (sin filtro de doctor)
        apiService.getAssignedAppointments(userId)
                .enqueue(new Callback<GetAssignedAppointmentsResponse>() {
                    @Override
                    public void onResponse(Call<GetAssignedAppointmentsResponse> call, Response<GetAssignedAppointmentsResponse> response) {
                        showLoading(false);
                        handleAppointmentsResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAssignedAppointmentsResponse> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleAppointmentsResponse(Response<GetAssignedAppointmentsResponse> response) {
        Log.d("ASSIGNED_APPOINTMENTS", "Response Code: " + response.code());

        if (response.isSuccessful() && response.body() != null) {
            GetAssignedAppointmentsResponse apiResponse = response.body();
            Log.d("ASSIGNED_APPOINTMENTS", "API Success: " + apiResponse.isSuccess());
            Log.d("ASSIGNED_APPOINTMENTS", "Message: " + apiResponse.getMessage());

            if (apiResponse.isSuccess()) {
                List<GetAssignedAppointmentsResponse.AssignedAppointmentData> appointments = apiResponse.getData();
                if (appointments != null && !appointments.isEmpty()) {
                    updateAppointmentsList(appointments);
                    Toast.makeText(this, "Citas cargadas: " + appointments.size(), Toast.LENGTH_SHORT).show();
                } else {
                    showEmptyState();
                }
            } else {
                Toast.makeText(this, "Error: " + apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                loadSimulatedData(); // Fallback
            }
        } else {
            Log.e("ASSIGNED_APPOINTMENTS", "Response not successful");
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                Log.e("ASSIGNED_APPOINTMENTS", "Error Body: " + errorBody);
                Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Log.e("ASSIGNED_APPOINTMENTS", "Error reading error body", e);
            }
            loadSimulatedData(); // Fallback
        }
    }

    private void updateAppointmentsList(List<GetAssignedAppointmentsResponse.AssignedAppointmentData> appointments) {
        citasAsignadas.clear();

        for (GetAssignedAppointmentsResponse.AssignedAppointmentData appointment : appointments) {
            try {
                // Convertir los datos de la API al formato de AppointmentCard
                AppointmentCard card = new AppointmentCard(
                        appointment.getMedicalAppointmentId(),
                        appointment.getPatientFullName(),
                        formatDateTime(appointment.getMedicalAppointmentDateTime()),
                        appointment.getBranch() != null ? appointment.getBranch().getBranchName() : "Sin sucursal",
                        appointment.getMedicalAppointmentState() != null ?
                                appointment.getMedicalAppointmentState().getMedicalAppointmentStateDescription() : "Sin estado"
                );

                citasAsignadas.add(card);

                TextView chipCount = findViewById(R.id.chipCount);
                LinearLayout layoutEmptyState = findViewById(R.id.layoutEmptyState);

                chipCount.setText(String.valueOf(citasAsignadas.size()));
                layoutEmptyState.setVisibility(citasAsignadas.isEmpty() ? View.VISIBLE : View.GONE);
                recyclerView.setVisibility(citasAsignadas.isEmpty() ? View.GONE : View.VISIBLE);

                // Log para debug
                Log.d("APPOINTMENT_CARD", "ID: " + card.getId());
                Log.d("APPOINTMENT_CARD", "Paciente: " + card.getPaciente());
                Log.d("APPOINTMENT_CARD", "Fecha: " + card.getFecha());

            } catch (Exception e) {
                Log.e("ASSIGNED_APPOINTMENTS", "Error converting appointment: " + e.getMessage());
            }
        }

        adapter.notifyDataSetChanged();
    }

    private String formatDateTime(String isoDateTime) {
        try {
            // Formato de entrada: 2025-06-06T12:45:00.000Z
            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            Date date = inputFormat.parse(isoDateTime);

            // Formato de salida: 12/06/2025 - 12:45
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy - HH:mm", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            Log.e("DATE_FORMAT", "Error formatting date: " + isoDateTime, e);
            return isoDateTime; // Devolver original si falla
        }
    }

    private void handleApiError(Throwable t) {
        Log.e("ASSIGNED_APPOINTMENTS", "API Error: " + t.getClass().getSimpleName());
        Log.e("ASSIGNED_APPOINTMENTS", "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
        loadSimulatedData(); // Fallback a datos simulados
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(show);
        }
    }

    private void showEmptyState() {
        citasAsignadas.clear();
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "No hay citas asignadas", Toast.LENGTH_SHORT).show();
    }

    private void loadSimulatedData() {
        // Método de respaldo con datos simulados
        Log.d("ASSIGNED_APPOINTMENTS", "Loading simulated data as fallback");
        citasAsignadas.clear();
        citasAsignadas.add(new AppointmentCard(
                "0980dee1-6478...",
                "Luis Hernández Díaz",
                "12/04/2025 - 15:30",
                "Clínica Central",
                "Aceptada"
        ));
        adapter.notifyDataSetChanged();
        Toast.makeText(this, "Cargando datos de ejemplo", Toast.LENGTH_SHORT).show();
    }

    // Método para obtener token de SharedPreferences (si necesitas autenticación)
    private String getTokenFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("MediQuick", Context.MODE_PRIVATE);
        return prefs.getString("auth_token", "");
    }

    // Método para obtener ID del doctor (si necesitas filtrar por doctor)
    private String getDoctorIdFromSession() {
        SharedPreferences prefs = getSharedPreferences("MediQuick", Context.MODE_PRIVATE);
        return prefs.getString("doctor_id", "");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Recargar citas cuando se regresa a la actividad
        loadAssignedAppointments();
    }
    private static final String TAG = "AssignedAppointmentsActivity";

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

    private void showToast(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}