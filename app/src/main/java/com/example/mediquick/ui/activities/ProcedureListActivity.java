package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

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
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProcedureListActivity extends AppCompatActivity {

    private static final String TAG = "ProcedureList";

    // Views
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtNombreSucursal;
    private TextView tvProcedureCount;
    private View emptyStateLayout;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private MaterialButton btnRetry;
    private View loadingOverlay;
    private Chip chipOnline;
    private Chip chipUrgent;

    // Data & Adapter
    private List<MedicalProcedure> procedures = new ArrayList<>();
    private List<MedicalProcedure> allProcedures = new ArrayList<>();
    private ProcedureAdapter adapter;

    // Datos recibidos del Intent
    private String branchId, branchName, institutionName;

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_list);

        receiveIntentData();
        initViews();
        setupToolbar();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();
        setupFilters();
        setupEmptyState();

        loadProcedures();
    }

    private void receiveIntentData() {
        branchId = getIntent().getStringExtra("branchId");
        branchName = getIntent().getStringExtra("branchName");
        institutionName = getIntent().getStringExtra("institutionName");

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Branch ID: " + branchId);
        Log.d(TAG, "Branch Name: " + branchName);
        Log.d(TAG, "Institution Name: " + institutionName);

        if (branchId == null || branchId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: branchId está vacío o es null");
        } else {
            Log.i(TAG, "✅ branchId válido: " + branchId);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerProcedimientos);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        txtNombreSucursal = findViewById(R.id.txtNombreSucursal);
        tvProcedureCount = findViewById(R.id.tvProcedureCount);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        btnRetry = findViewById(R.id.btnRetry);
        loadingOverlay = findViewById(R.id.loadingOverlay);
        chipOnline = findViewById(R.id.chipOnline);
        chipUrgent = findViewById(R.id.chipUrgent);

        // Configurar información de la sucursal
        String headerText = branchName != null ? branchName : "Sucursal";
        txtNombreSucursal.setText(headerText);

        Log.d(TAG, "Views inicializadas. Header: " + headerText);
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
        apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL)
                .create(AppointmentService.class);
        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupRecyclerView() {
        adapter = new ProcedureAdapter(procedures, this::onProcedureSelected);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);

        Log.d(TAG, "RecyclerView configurado");
    }

    private void setupSwipeRefresh() {
        swipeRefreshLayout.setColorSchemeResources(
                R.color.primary,
                R.color.primary_dark,
                R.color.accent
        );

        swipeRefreshLayout.setOnRefreshListener(() -> {
            Log.d(TAG, "Pull to refresh activado");
            loadProcedures();
        });
    }

    private void setupFilters() {
        if (chipOnline != null) {
            chipOnline.setOnCheckedChangeListener((view, isChecked) -> {
                Log.d(TAG, "Filtro Online: " + isChecked);
                applyFilters();
            });
        }

        if (chipUrgent != null) {
            chipUrgent.setOnCheckedChangeListener((view, isChecked) -> {
                Log.d(TAG, "Filtro Urgente: " + isChecked);
                applyFilters();
            });
        }
    }

    private void setupEmptyState() {
        btnRetry.setOnClickListener(v -> {
            Log.d(TAG, "Retry button pressed");
            loadProcedures();
        });
    }

    private void loadProcedures() {
        if (branchId == null || branchId.isEmpty()) {
            Log.e(TAG, "No se puede cargar procedimientos: branchId es null o vacío");
            showErrorMessage("Error", "ID de sucursal no válido");
            return;
        }

        Log.d(TAG, "=== CARGANDO PROCEDIMIENTOS DESDE API ===");
        Log.d(TAG, "Branch ID: " + branchId);

        showLoading(true);
        hideEmptyState();

        apiService.getAllProceduresByBranchId(branchId)
                .enqueue(new Callback<GetAllProceduresByBranchIdResponse>() {
                    @Override
                    public void onResponse(Call<GetAllProceduresByBranchIdResponse> call,
                                           Response<GetAllProceduresByBranchIdResponse> response) {
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

        if (response.isSuccessful() && response.body() != null) {
            GetAllProceduresByBranchIdResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Total procedimientos: " + apiResponse.getTotalProcedures());

            if (apiResponse.isSuccess() && apiResponse.hasProcedures()) {
                updateProceduresList(apiResponse.getData());
                showSuccessMessage("Procedimientos cargados: " + apiResponse.getTotalProcedures());
            } else {
                Log.w(TAG, "No hay procedimientos disponibles");
                showEmptyState("No hay procedimientos disponibles",
                        "Esta sucursal no tiene procedimientos disponibles en este momento");
            }
        } else {
            Log.e(TAG, "Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateProceduresList(List<GetAllProceduresByBranchIdResponse.ProcedureData> proceduresData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE PROCEDIMIENTOS ===");

        allProcedures.clear();

        for (GetAllProceduresByBranchIdResponse.ProcedureData procedureData : proceduresData) {
            // Solo agregar procedimientos activos con slots disponibles
            if (procedureData.isActive() && procedureData.hasAvailableSlots()) {
                MedicalProcedure procedure = convertToMedicalProcedure(procedureData);
                allProcedures.add(procedure);

                Log.d(TAG, "Procedimiento agregado: " + procedure.getProcedureName());
                Log.d(TAG, "  Especialidad: " + procedure.getSpecialty());
                Log.d(TAG, "  Costo: " + procedure.getProcedureCost());
                Log.d(TAG, "  Duración: " + procedure.getProcedureDuration());
                Log.d(TAG, "  Online: " + procedure.isAvailableOnline());
            } else {
                String reason = !procedureData.isActive() ? "inactivo" : "sin slots disponibles";
                Log.d(TAG, "Procedimiento omitido (" + reason + "): " + procedureData.getMedicalProcedureName());
            }
        }

        Log.i(TAG, "Lista actualizada con " + allProcedures.size() + " procedimientos disponibles");

        // Aplicar filtros actuales
        applyFilters();
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

        Log.d(TAG, "Convertido procedimiento: " + procedureData.getMedicalProcedureName());
        return procedure;
    }

    private void applyFilters() {
        procedures.clear();

        boolean filterOnline = chipOnline != null && chipOnline.isChecked();
        boolean filterUrgent = chipUrgent != null && chipUrgent.isChecked();

        for (MedicalProcedure procedure : allProcedures) {
            boolean includeItem = true;

            if (filterOnline && !procedure.isAvailableOnline()) {
                includeItem = false;
            }

            if (filterUrgent && !isUrgentProcedure(procedure)) {
                includeItem = false;
            }

            if (includeItem) {
                procedures.add(procedure);
            }
        }

        adapter.notifyDataSetChanged();
        updateProcedureCount();

        if (procedures.isEmpty() && !allProcedures.isEmpty()) {
            showEmptyState("Sin resultados",
                    "No hay procedimientos que coincidan con los filtros seleccionados");
        } else {
            hideEmptyState();
        }

        Log.d(TAG, "Filtros aplicados. Mostrando " + procedures.size() + " de " + allProcedures.size() + " procedimientos");
    }

    private boolean isUrgentProcedure(MedicalProcedure procedure) {
        // Lógica para determinar si un procedimiento es urgente
        String name = procedure.getProcedureName().toLowerCase();
        String specialty = procedure.getSpecialty() != null ? procedure.getSpecialty().toLowerCase() : "";

        return name.contains("urgencia") || name.contains("emergencia") ||
                specialty.contains("urgencia") || specialty.contains("emergencia");
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR PROCEDIMIENTOS ===");
        Log.e(TAG, "Error: " + t.getMessage());
        t.printStackTrace();

        showErrorMessage("Error de conexión", "Revisa tu conexión a internet");
        loadSimulatedProcedures(); // Fallback
    }

    private void handleHttpError(Response<GetAllProceduresByBranchIdResponse> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);

            showErrorMessage("Error del servidor",
                    "Código: " + response.code() + " - Intenta más tarde");
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            showErrorMessage("Error del servidor", "Código: " + response.code());
        }

        loadSimulatedProcedures(); // Fallback
    }

    private void loadSimulatedProcedures() {
        Log.d(TAG, "=== CARGANDO PROCEDIMIENTOS SIMULADOS (FALLBACK) ===");

        allProcedures.clear();

        // Datos de ejemplo mejorados
        allProcedures.add(createSimulatedProcedure(
                "sim-1",
                "Consulta General",
                "30 minutos",
                50.0,
                true,
                "Medicina General"
        ));

        allProcedures.add(createSimulatedProcedure(
                "sim-2",
                "Radiografía de Tórax",
                "15 minutos",
                80.0,
                false,
                "Radiología"
        ));

        allProcedures.add(createSimulatedProcedure(
                "sim-3",
                "Consulta de Cardiología",
                "45 minutos",
                150.0,
                true,
                "Cardiología"
        ));

        allProcedures.add(createSimulatedProcedure(
                "sim-4",
                "Limpieza Dental",
                "60 minutos",
                120.0,
                false,
                "Odontología"
        ));

        Log.i(TAG, "Datos simulados cargados: " + allProcedures.size() + " procedimientos");

        applyFilters();
        showInfoMessage("Datos de ejemplo cargados");
    }

    private MedicalProcedure createSimulatedProcedure(String id, String name, String duration,
                                                      double cost, boolean online, String specialty) {
        MedicalProcedure procedure = new MedicalProcedure();
        procedure.setProcedureId(id);
        procedure.setProcedureName(name);
        procedure.setProcedureDuration(duration);
        procedure.setProcedureCost(cost);
        procedure.setAvailableOnline(online);
        procedure.setSpecialty(specialty);
        return procedure;
    }

    private void updateProcedureCount() {
        int count = procedures.size();
        String text = count == 1 ?
                count + " procedimiento disponible" :
                count + " procedimientos disponibles";
        tvProcedureCount.setText(text);
    }

    private void onProcedureSelected(MedicalProcedure procedure) {
        Log.d(TAG, "Procedimiento seleccionado: " + procedure.getProcedureName());
        Log.d(TAG, "  ID: " + procedure.getProcedureId());
        Log.d(TAG, "  Costo: " + procedure.getProcedureCost());

        Intent intent = new Intent(this, ConfirmAppointmentActivity.class);
        intent.putExtra("procedureId", procedure.getProcedureId());
        intent.putExtra("procedureName", procedure.getProcedureName());
        intent.putExtra("branchId", branchId);
        intent.putExtra("branchName", branchName);
        intent.putExtra("institutionName", institutionName);
        intent.putExtra("procedureCost", procedure.getProcedureCost());
        startActivity(intent);
    }

    private void showLoading(boolean show) {
        loadingOverlay.setVisibility(show ? View.VISIBLE : View.GONE);
        swipeRefreshLayout.setRefreshing(false); // Siempre ocultar el refresh

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void showEmptyState(String title, String subtitle) {
        tvEmptyTitle.setText(title);
        tvEmptySubtitle.setText(subtitle);
        emptyStateLayout.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);

        Log.d(TAG, "Mostrando empty state: " + title);
    }

    private void hideEmptyState() {
        emptyStateLayout.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
    }

    private void showSuccessMessage(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.success))
                .show();
    }

    private void showErrorMessage(String title, String message) {
        Snackbar.make(recyclerView, title + ": " + message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getResources().getColor(R.color.error))
                .setAction("Reintentar", v -> loadProcedures())
                .show();
    }

    private void showInfoMessage(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.info))
                .show();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button presionado - volviendo a sucursales");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar procedimientos al volver a la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "ProcedureListActivity destruida");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}