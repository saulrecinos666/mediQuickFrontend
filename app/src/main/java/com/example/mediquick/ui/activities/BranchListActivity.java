package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.Branch;
import com.example.mediquick.data.model.GetAllBranchesByInstitutionResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.BranchAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchListActivity extends AppCompatActivity {

    private static final String TAG = "BranchList";

    // Views
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView txtNombreInstitucion;
    private TextView tvBranchCount;
    private View emptyStateLayout;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private MaterialButton btnRetry;
    private View loadingOverlay;

    // Data & Adapter
    private List<Branch> branches = new ArrayList<>();
    private BranchAdapter adapter;

    // Datos recibidos del Intent
    private String institutionId, institutionName;

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);

        receiveIntentData();
        initViews();
        setupToolbar();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();
        setupEmptyState();

        loadBranches();
    }

    private void receiveIntentData() {
        institutionId = getIntent().getStringExtra("institutionId");
        institutionName = getIntent().getStringExtra("institutionName");

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Institution ID: " + institutionId);
        Log.d(TAG, "Institution Name: " + institutionName);

        if (institutionId == null || institutionId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: institutionId está vacío o es null");
        } else {
            Log.i(TAG, "✅ institutionId válido: " + institutionId);
        }
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerSucursales);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        txtNombreInstitucion = findViewById(R.id.txtNombreInstitucion);
        tvBranchCount = findViewById(R.id.tvBranchCount);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        btnRetry = findViewById(R.id.btnRetry);
        loadingOverlay = findViewById(R.id.loadingOverlay);

        // Configurar información de la institución
        String headerText = institutionName != null ? institutionName : "Institución";
        txtNombreInstitucion.setText(headerText);

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
        adapter = new BranchAdapter(branches, this::onBranchSelected);

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
            loadBranches();
        });
    }

    private void setupEmptyState() {
        btnRetry.setOnClickListener(v -> {
            Log.d(TAG, "Retry button pressed");
            loadBranches();
        });
    }

    private void loadBranches() {
        if (institutionId == null || institutionId.isEmpty()) {
            Log.e(TAG, "No se puede cargar sucursales: institutionId es null o vacío");
            showErrorMessage("Error", "ID de institución no válido");
            return;
        }

        Log.d(TAG, "=== CARGANDO SUCURSALES DESDE API ===");
        Log.d(TAG, "Institution ID: " + institutionId);

        showLoading(true);
        hideEmptyState();

        apiService.getAllBranchesByInstitution(institutionId)
                .enqueue(new Callback<GetAllBranchesByInstitutionResponse>() {
                    @Override
                    public void onResponse(Call<GetAllBranchesByInstitutionResponse> call,
                                           Response<GetAllBranchesByInstitutionResponse> response) {
                        showLoading(false);
                        handleBranchesResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAllBranchesByInstitutionResponse> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleBranchesResponse(Response<GetAllBranchesByInstitutionResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE SUCURSALES RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());

        if (response.isSuccessful() && response.body() != null) {
            GetAllBranchesByInstitutionResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Total sucursales: " + apiResponse.getTotalBranches());

            if (apiResponse.isSuccess() && apiResponse.hasBranches()) {
                updateBranchesList(apiResponse.getData());
                showSuccessMessage("Sucursales cargadas: " + apiResponse.getTotalBranches());
            } else {
                Log.w(TAG, "No hay sucursales disponibles");
                showEmptyState("No hay sucursales disponibles",
                        "Esta institución no tiene sucursales activas en este momento");
            }
        } else {
            Log.e(TAG, "Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateBranchesList(List<GetAllBranchesByInstitutionResponse.BranchData> branchesData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE SUCURSALES ===");

        branches.clear();

        for (GetAllBranchesByInstitutionResponse.BranchData branchData : branchesData) {
            // Solo agregar sucursales activas
            if (branchData.isActive()) {
                Branch branch = convertToBranch(branchData);
                branches.add(branch);

                Log.d(TAG, "Sucursal agregada: " + branch.getBranchName());
                Log.d(TAG, "  Dirección: " + branch.getBranchFullAddress());
                Log.d(TAG, "  Descripción: " + branch.getBranchDescription());
            } else {
                Log.d(TAG, "Sucursal inactiva omitida: " + branchData.getBranchName());
            }
        }

        Log.i(TAG, "Lista actualizada con " + branches.size() + " sucursales activas");

        adapter.notifyDataSetChanged();
        updateBranchCount();
    }

    private Branch convertToBranch(GetAllBranchesByInstitutionResponse.BranchData branchData) {
        Branch branch = new Branch();
        branch.setBranchId(branchData.getBranchId());
        branch.setBranchName(branchData.getBranchName());
        branch.setBranchDescription(branchData.getBranchDescription());
        branch.setBranchFullAddress(branchData.getBranchFullAddress());

        Log.d(TAG, "Convertida sucursal: " + branchData.getDisplayName());
        return branch;
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR SUCURSALES ===");
        Log.e(TAG, "Error: " + t.getMessage());
        t.printStackTrace();

        showErrorMessage("Error de conexión", "Revisa tu conexión a internet");
        loadSimulatedBranches(); // Fallback
    }

    private void handleHttpError(Response<GetAllBranchesByInstitutionResponse> response) {
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

        loadSimulatedBranches(); // Fallback
    }

    private void loadSimulatedBranches() {
        Log.d(TAG, "=== CARGANDO SUCURSALES SIMULADAS (FALLBACK) ===");

        branches.clear();

        // Datos de ejemplo mejorados
        branches.add(createSimulatedBranch(
                "sim-1",
                "Sucursal Centro",
                "Av. Central #123, Col. Centro",
                "Sucursal principal con atención las 24 horas del día"
        ));

        branches.add(createSimulatedBranch(
                "sim-2",
                "Sucursal Norte",
                "Col. La Esperanza, Calle Ficticia #456",
                "Atención general y especialidades médicas"
        ));

        branches.add(createSimulatedBranch(
                "sim-3",
                "Sucursal Urgencias",
                "Av. Emergencias #789, Col. Salud",
                "Centro de urgencias y emergencias médicas"
        ));

        Log.i(TAG, "Datos simulados cargados: " + branches.size() + " sucursales");

        adapter.notifyDataSetChanged();
        updateBranchCount();
        showInfoMessage("Datos de ejemplo cargados");
    }

    private Branch createSimulatedBranch(String id, String name, String address, String description) {
        Branch branch = new Branch();
        branch.setBranchId(id);
        branch.setBranchName(name);
        branch.setBranchFullAddress(address);
        branch.setBranchDescription(description);
        return branch;
    }

    private void updateBranchCount() {
        int count = branches.size();
        String text = count == 1 ?
                count + " sucursal encontrada" :
                count + " sucursales encontradas";
        tvBranchCount.setText(text);
    }

    private void onBranchSelected(Branch branch) {
        Log.d(TAG, "Sucursal seleccionada: " + branch.getBranchName());

        Intent intent = new Intent(this, ProcedureListActivity.class);
        intent.putExtra("branchId", branch.getBranchId());
        intent.putExtra("branchName", branch.getBranchName());
        intent.putExtra("institutionName", institutionName);
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
                .setAction("Reintentar", v -> loadBranches())
                .show();
    }

    private void showInfoMessage(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.info))
                .show();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "Back button presionado - volviendo a instituciones");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar sucursales al volver a la actividad
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BranchListActivity destruida");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}