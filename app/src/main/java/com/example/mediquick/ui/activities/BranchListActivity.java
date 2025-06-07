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
import com.example.mediquick.data.model.Branch;
import com.example.mediquick.data.model.GetAllBranchesByInstitutionResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.BranchAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BranchListActivity extends AppCompatActivity {

    private static final String TAG = "BranchList";

    private RecyclerView recyclerView;
    private TextView txtHeader, emptyState;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ImageView btnBackToInstitutions;
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
        setupClickListeners();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();

        cargarSucursales();
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
        recyclerView = findViewById(R.id.recyclerSucursales);
        txtHeader = findViewById(R.id.txtNombreInstitucion);
        emptyState = findViewById(R.id.emptyViewSucursales);
        progressBar = findViewById(R.id.progressBarSucursales);
        btnBackToInstitutions = findViewById(R.id.btnBackToInstitutions);
        //swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Agregar al layout si no existe

        // Configurar header
        String headerText = institutionName != null ? institutionName : "Institución";
        txtHeader.setText(headerText);

        Log.d(TAG, "Views inicializadas. Header: " + headerText);
    }

    private void setupClickListeners() {
        // ✅ CONFIGURAR EL BOTÓN DE RETROCESO
        btnBackToInstitutions.setOnClickListener(v -> {
            Log.d(TAG, "Back button presionado - volviendo a instituciones");
            onBackPressed();
        });
    }

    private void setupApiService() {
        apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL).create(AppointmentService.class);

        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupRecyclerView() {
        adapter = new BranchAdapter(branches, selected -> {
            Log.d(TAG, "Sucursal seleccionada: " + selected.getBranchName());
            Intent intent = new Intent(this, ProcedureListActivity.class);
            intent.putExtra("branchId", selected.getBranchId());
            intent.putExtra("branchName", selected.getBranchName());
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
                cargarSucursales();
            });
        }
    }

    private void cargarSucursales() {
        if (institutionId == null || institutionId.isEmpty()) {
            Log.e(TAG, "No se puede cargar sucursales: institutionId es null o vacío");
            showError("Error: ID de institución no válido");
            return;
        }

        Log.d(TAG, "=== CARGANDO SUCURSALES DESDE API ===");
        Log.d(TAG, "Institution ID: " + institutionId);

        showLoading(true);

        apiService.getAllBranchesByInstitution(institutionId)
                .enqueue(new Callback<GetAllBranchesByInstitutionResponse>() {
                    @Override
                    public void onResponse(Call<GetAllBranchesByInstitutionResponse> call, Response<GetAllBranchesByInstitutionResponse> response) {
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
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAllBranchesByInstitutionResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());
            Log.d(TAG, "Total sucursales recibidas: " + apiResponse.getTotalBranches());

            if (apiResponse.isSuccess() && apiResponse.hasBranches()) {
                updateBranchesList(apiResponse.getData());
                Toast.makeText(this, "Sucursales cargadas: " + apiResponse.getTotalBranches(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ No hay sucursales disponibles para esta institución");
                showEmptyState("No hay sucursales disponibles para esta institución");
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
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

                Log.d(TAG, "Sucursal agregada: " + branch.getBranchName() );
                Log.d(TAG, "  Dirección: " + branch.getBranchFullAddress());
                Log.d(TAG, "  Descripción: " + branch.getBranchDescription());
                Log.d(TAG, "  Institución: " + branchData.getInstitutionDisplayName());
            } else {
                Log.d(TAG, "Sucursal inactiva omitida: " + branchData.getBranchName());
            }
        }

        Log.i(TAG, "✅ Lista actualizada con " + branches.size() + " sucursales activas");

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private Branch convertToBranch(GetAllBranchesByInstitutionResponse.BranchData branchData) {
        Branch branch = new Branch();

        branch.setBranchId(branchData.getBranchId());
        branch.setBranchName(branchData.getBranchName());
        // branch.setBranchAcronym(branchData.getBranchAcronym());
        branch.setBranchDescription(branchData.getBranchDescription());
        branch.setBranchFullAddress(branchData.getBranchFullAddress());
        // branch.setBranchStatus(branchData.isBranchStatus());
        //branch.setInstitutionId(branchData.getInstitutionId());

        // Coordenadas (pueden ser null)
//        if (branchData.hasCoordinates()) {
//            branch.setBranchLatitude(branchData.getBranchLatitude());
//            branch.setBranchLongitude(branchData.getBranchLongitude());
//            Log.d(TAG, "  Coordenadas: " + branchData.getBranchLatitude() + ", " + branchData.getBranchLongitude());
//        } else {
//            Log.d(TAG, "  Sin coordenadas GPS");
//        }

        Log.d(TAG, "Convertida sucursal: " + branchData.getDisplayName());

        return branch;
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR SUCURSALES ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();

        // Cargar datos simulados como fallback
        cargarSucursalesSimuladas();
    }

    private void handleHttpError(Response<GetAllBranchesByInstitutionResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
            Toast.makeText(this, "Error HTTP " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
        }

        // Cargar datos simulados como fallback
        cargarSucursalesSimuladas();
    }

    private void cargarSucursalesSimuladas() {
        Log.d(TAG, "=== CARGANDO SUCURSALES SIMULADAS (FALLBACK) ===");

        branches.clear();

        Branch branch1 = new Branch();
        branch1.setBranchId("sim-1");
        branch1.setBranchName("Sucursal Centro");
        //branch1.setBranchAcronym("SC");
        branch1.setBranchFullAddress("Av. Central #123");
        branch1.setBranchDescription("Sucursal principal en zona centro");
        //branch1.setBranchStatus(true);
        //branch1.setInstitutionId(institutionId);
        branches.add(branch1);

        Branch branch2 = new Branch();
        branch2.setBranchId("sim-2");
        branch2.setBranchName("Sucursal Norte");
        //branch2.setBranchAcronym("SN");
        branch2.setBranchFullAddress("Col. La Esperanza, Calle Ficticia");
        branch2.setBranchDescription("Atención general y odontológica");
        // branch2.setBranchStatus(true);
        // branch2.setInstitutionId(institutionId);
        branches.add(branch2);

        Log.i(TAG, "✅ Datos simulados cargados: " + branches.size() + " sucursales");

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
        boolean isEmpty = branches.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        Log.d(TAG, "Empty state: " + (isEmpty ? "VISIBLE" : "GONE"));
    }

    private void showEmptyState(String message) {
        branches.clear();
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
        Log.d(TAG, "Back button presionado - volviendo a la pantalla de instituciones");
        super.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar sucursales al volver a la actividad
        // cargarSucursales();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "BranchListActivity destruida");
    }
}