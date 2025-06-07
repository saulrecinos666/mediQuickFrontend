package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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
import com.example.mediquick.data.model.GetAllProceduresByBranchIdResponse;
import com.example.mediquick.data.model.MedicalProcedure;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.ProcedureAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcedureListActivity extends AppCompatActivity {

    private static final String TAG = "ProcedureList";

    private RecyclerView recyclerView;
    private TextView txtHeader, emptyState;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView btnBackToBranches;
    private List<MedicalProcedure> procedures = new ArrayList<>();
    private ProcedureAdapter adapter;

    // Datos recibidos del Intent
    private String branchId, branchName;

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_list);

        receiveIntentData();
        initViews();
        setupClickListeners();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();

        cargarProcedimientos();
    }

    private void receiveIntentData() {
        branchId = getIntent().getStringExtra("branchId");
        branchName = getIntent().getStringExtra("branchName");

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Branch ID: " + branchId);
        Log.d(TAG, "Branch Name: " + branchName);

        if (branchId == null || branchId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: branchId está vacío o es null");
        } else {
            Log.i(TAG, "✅ branchId válido: " + branchId);
        }
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerProcedimientos);
        txtHeader = findViewById(R.id.txtNombreSucursal);
        emptyState = findViewById(R.id.emptyViewProcedimientos);
        progressBar = findViewById(R.id.progressBarProcedimientos);
        btnBackToBranches = findViewById(R.id.btnBackToBranches);
        // swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Agregar al layout si no existe

        // Configurar header
        String headerText = branchName != null ? branchName : "Sucursal";
        txtHeader.setText(headerText);

        Log.d(TAG, "Views inicializadas. Header: " + headerText);
    }

    private void setupClickListeners() {
        // ✅ CONFIGURAR EL BOTÓN DE RETROCESO
        btnBackToBranches.setOnClickListener(v -> {
            Log.d(TAG, "Back button presionado - volviendo a sucursales");
            onBackPressed();
        });
    }

    private void setupApiService() {
        apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL).create(AppointmentService.class);

        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupRecyclerView() {
        adapter = new ProcedureAdapter(procedures, selected -> {
            Log.d(TAG, "Procedimiento seleccionado: " + selected.getProcedureName());
            Log.d(TAG, "  ID: " + selected.getProcedureId());
            Log.d(TAG, "  Costo: " + selected.getProcedureCost());

            // Al seleccionar un procedimiento, preguntar si está seguro de agendar la cita
            Intent intent = new Intent(this, ConfirmAppointmentActivity.class);
            intent.putExtra("procedureId", selected.getProcedureId());
            intent.putExtra("procedureName", selected.getProcedureName());
            intent.putExtra("branchId", branchId);
            intent.putExtra("branchName", branchName);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        Log.d(TAG, "RecyclerView configurado");
    }

    private void setupSwipeRefresh() {
        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setOnRefreshListener(() -> {
                Log.d(TAG, "Pull to refresh activado");
                cargarProcedimientos();
            });
        }
    }

    private void cargarProcedimientos() {
        if (branchId == null || branchId.isEmpty()) {
            Log.e(TAG, "No se puede cargar procedimientos: branchId es null o vacío");
            showError("Error: ID de sucursal no válido");
            return;
        }

        Log.d(TAG, "=== CARGANDO PROCEDIMIENTOS DESDE API ===");
        Log.d(TAG, "Branch ID: " + branchId);

        showLoading(true);

        apiService.getAllProceduresByBranchId(branchId)
                .enqueue(new Callback<GetAllProceduresByBranchIdResponse>() {
                    @Override
                    public void onResponse(Call<GetAllProceduresByBranchIdResponse> call, Response<GetAllProceduresByBranchIdResponse> response) {
                        showLoading(false);
                        handleProceduresResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAllProceduresByBranchIdResponse> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleProceduresResponse(Response<GetAllProceduresByBranchIdResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE PROCEDIMIENTOS RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAllProceduresByBranchIdResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());
            Log.d(TAG, "Total procedimientos recibidos: " + apiResponse.getTotalProcedures());

            if (apiResponse.isSuccess() && apiResponse.hasProcedures()) {
                updateProceduresList(apiResponse.getData());
                Toast.makeText(this, "Procedimientos cargados: " + apiResponse.getTotalProcedures(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ No hay procedimientos disponibles para esta sucursal");
                showEmptyState("No hay procedimientos disponibles para esta sucursal");
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateProceduresList(List<GetAllProceduresByBranchIdResponse.ProcedureData> proceduresData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE PROCEDIMIENTOS ===");

        procedures.clear();

        for (GetAllProceduresByBranchIdResponse.ProcedureData procedureData : proceduresData) {
            // Solo agregar procedimientos activos con slots disponibles
            if (procedureData.isActive() && procedureData.hasAvailableSlots()) {
                MedicalProcedure procedure = convertToMedicalProcedure(procedureData);
                procedures.add(procedure);

                Log.d(TAG, "Procedimiento agregado: " + procedure.getProcedureName());
                Log.d(TAG, "  Especialidad: " + procedure.getSpecialty());
                //Log.d(TAG, "  Costo: " + procedure.getFormattedCost());
                Log.d(TAG, "  Duración: " + procedure.getProcedureDuration());
                Log.d(TAG, "  Slots disponibles: " + procedureData.getMedicalProcedureAvailableSlots());
                Log.d(TAG, "  Online: " + procedure.isAvailableOnline());
                Log.d(TAG, "  Requiere confirmación: " + procedureData.requiresConfirmation());
            } else {
                String reason = !procedureData.isActive() ? "inactivo" : "sin slots disponibles";
                Log.d(TAG, "Procedimiento omitido (" + reason + "): " + procedureData.getMedicalProcedureName());
            }
        }

        Log.i(TAG, "✅ Lista actualizada con " + procedures.size() + " procedimientos disponibles");

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private MedicalProcedure convertToMedicalProcedure(GetAllProceduresByBranchIdResponse.ProcedureData procedureData) {
        MedicalProcedure procedure = new MedicalProcedure();

        procedure.setProcedureId(procedureData.getMedicalProcedureId());
        procedure.setProcedureName(procedureData.getMedicalProcedureName());
        procedure.setProcedureDuration(procedureData.getFormattedDuration());
        procedure.setSpecialty(procedureData.getSpecialtyName());
        procedure.setAvailableOnline(procedureData.isAvailableOnline());

        // Convertir costo de String a Double
        try {
            double cost = Double.parseDouble(procedureData.getMedicalProcedureCost());
            procedure.setProcedureCost(cost);
        } catch (NumberFormatException e) {
            Log.w(TAG, "Error convirtiendo costo: " + procedureData.getMedicalProcedureCost());
            procedure.setProcedureCost(0.0);
        }

        // Campos adicionales si existen en tu modelo MedicalProcedure
        if (hasAdditionalFields(procedure)) {
            setAdditionalFields(procedure, procedureData);
        }

        Log.d(TAG, "Convertido procedimiento: " + procedureData.getMedicalProcedureName());

        return procedure;
    }

    private boolean hasAdditionalFields(MedicalProcedure procedure) {
        // Verifica si tu modelo MedicalProcedure tiene campos adicionales
        // como photoUrl, requiresConfirmation, availableSlots, etc.
        return true; // Cambiar según tu modelo
    }

    private void setAdditionalFields(MedicalProcedure procedure, GetAllProceduresByBranchIdResponse.ProcedureData procedureData) {
        // Agregar campos adicionales si existen en tu modelo MedicalProcedure
        // procedure.setPhotoUrl(procedureData.getMedicalProcedurePhotoUrl());
        // procedure.setRequiresConfirmation(procedureData.requiresConfirmation());
        // procedure.setAvailableSlots(procedureData.getMedicalProcedureAvailableSlots());
        // procedure.setEstimatedDurationMinutes(Integer.parseInt(procedureData.getMedicalProcedureEstimatedDuration()));
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR PROCEDIMIENTOS ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();

        // Cargar datos simulados como fallback
        cargarProcedimientosSimulados();
    }

    private void handleHttpError(Response<GetAllProceduresByBranchIdResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
            Toast.makeText(this, "Error HTTP " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
        }

        // Cargar datos simulados como fallback
        cargarProcedimientosSimulados();
    }

    private void cargarProcedimientosSimulados() {
        Log.d(TAG, "=== CARGANDO PROCEDIMIENTOS SIMULADOS (FALLBACK) ===");

        procedures.clear();

        MedicalProcedure procedure1 = new MedicalProcedure();
        procedure1.setProcedureId("sim-1");
        procedure1.setProcedureName("Consulta General");
        procedure1.setProcedureDuration("30 minutos");
        procedure1.setProcedureCost(50.0);
        procedure1.setAvailableOnline(true);
        procedure1.setSpecialty("General");
        procedures.add(procedure1);

        MedicalProcedure procedure2 = new MedicalProcedure();
        procedure2.setProcedureId("sim-2");
        procedure2.setProcedureName("Radiografía de Tórax");
        procedure2.setProcedureDuration("15 minutos");
        procedure2.setProcedureCost(80.0);
        procedure2.setAvailableOnline(false);
        procedure2.setSpecialty("Radiología");
        procedures.add(procedure2);

        Log.i(TAG, "✅ Datos simulados cargados: " + procedures.size() + " procedimientos");

        adapter.notifyDataSetChanged();
        updateEmptyState();

        Toast.makeText(this, "Cargando datos de ejemplo", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (swipeRefreshLayout != null) {
            swipeRefreshLayout.setRefreshing(show);
        }

        if (show) {
            emptyState.setVisibility(View.GONE);
        }

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void updateEmptyState() {
        boolean isEmpty = procedures.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        Log.d(TAG, "Empty state: " + (isEmpty ? "VISIBLE" : "GONE"));
    }

    private void showEmptyState(String message) {
        procedures.clear();
        adapter.notifyDataSetChanged();

        if (emptyState instanceof TextView) {
            ((TextView) emptyState).setText(message);
        }

        updateEmptyState();
        Log.d(TAG, "Mostrando empty state: " + message);
    }

    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
        updateEmptyState();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button presionado - volviendo a la pantalla de sucursales");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar procedimientos al volver a la actividad
        // cargarProcedimientos();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ProcedureListActivity destruida");
    }
}