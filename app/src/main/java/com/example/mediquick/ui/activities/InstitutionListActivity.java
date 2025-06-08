package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.mediquick.data.model.GetAllInstitutionsResponse;
import com.example.mediquick.data.model.Institution;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.InstitutionAdapter;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstitutionListActivity extends AppCompatActivity {

    private static final String TAG = "InstitutionList";
    private static final int SEARCH_DELAY_MS = 500;

    // Views
    private MaterialToolbar toolbar;
    private RecyclerView recyclerView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextInputEditText etSearch;
    private TextView tvInstitutionCount;
    private View emptyStateLayout;
    private TextView tvEmptyTitle;
    private TextView tvEmptySubtitle;
    private MaterialButton btnRetry;
    private View loadingOverlay;

    // Data & Adapter
    private InstitutionAdapter adapter;
    private List<Institution> institutionsList = new ArrayList<>();

    // API
    private AppointmentService apiService;

    // Search
    private Handler searchHandler = new Handler(Looper.getMainLooper());
    private Runnable searchRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_list);

        initViews();
        setupToolbar();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();
        setupSearch();
        setupEmptyState();

        loadInstitutions();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        recyclerView = findViewById(R.id.recyclerInstituciones);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        etSearch = findViewById(R.id.etSearch);
        tvInstitutionCount = findViewById(R.id.tvInstitutionCount);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
        tvEmptyTitle = findViewById(R.id.tvEmptyTitle);
        tvEmptySubtitle = findViewById(R.id.tvEmptySubtitle);
        btnRetry = findViewById(R.id.btnRetry);
        loadingOverlay = findViewById(R.id.loadingOverlay);

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
        apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL)
                .create(AppointmentService.class);
        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupRecyclerView() {
        adapter = new InstitutionAdapter(institutionsList, this::onInstitutionSelected);

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
            loadInstitutions();
        });
    }

    private void setupSearch() {
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                // Cancelar búsqueda anterior
                if (searchRunnable != null) {
                    searchHandler.removeCallbacks(searchRunnable);
                }

                // Programar nueva búsqueda con delay
                searchRunnable = () -> {
                    String query = s.toString().trim();
                    filterInstitutions(query);
                };

                searchHandler.postDelayed(searchRunnable, SEARCH_DELAY_MS);
            }
        });
    }

    private void setupEmptyState() {
        btnRetry.setOnClickListener(v -> {
            Log.d(TAG, "Retry button pressed");
            loadInstitutions();
        });
    }

    private void loadInstitutions() {
        Log.d(TAG, "=== CARGANDO INSTITUCIONES DESDE API ===");

        showLoading(true);
        hideEmptyState();

        apiService.getAllInstitutions()
                .enqueue(new Callback<GetAllInstitutionsResponse>() {
                    @Override
                    public void onResponse(Call<GetAllInstitutionsResponse> call,
                                           Response<GetAllInstitutionsResponse> response) {
                        showLoading(false);
                        handleInstitutionsResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAllInstitutionsResponse> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleInstitutionsResponse(Response<GetAllInstitutionsResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE INSTITUCIONES RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());

        if (response.isSuccessful() && response.body() != null) {
            GetAllInstitutionsResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Total instituciones: " + apiResponse.getTotalInstitutions());

            if (apiResponse.isSuccess() && apiResponse.hasInstitutions()) {
                updateInstitutionsList(apiResponse.getData());
                showSuccessMessage("Instituciones cargadas: " + apiResponse.getTotalInstitutions());
            } else {
                Log.w(TAG, "No hay instituciones disponibles");
                showEmptyState("No hay instituciones disponibles",
                        "No se encontraron instituciones médicas en este momento");
            }
        } else {
            Log.e(TAG, "Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateInstitutionsList(List<GetAllInstitutionsResponse.InstitutionData> institutionsData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE INSTITUCIONES ===");

        institutionsList.clear();

        for (GetAllInstitutionsResponse.InstitutionData institutionData : institutionsData) {
            Institution institution = convertToInstitution(institutionData);
            institutionsList.add(institution);
        }

        Log.i(TAG, "Lista actualizada con " + institutionsList.size() + " instituciones");

        adapter.updateData(institutionsList);
        updateInstitutionCount();

        // Aplicar filtro actual si existe
        String currentQuery = etSearch.getText().toString().trim();
        if (!currentQuery.isEmpty()) {
            filterInstitutions(currentQuery);
        }
    }

    private Institution convertToInstitution(GetAllInstitutionsResponse.InstitutionData institutionData) {
        Institution institution = new Institution();
        institution.setInstitutionId(institutionData.getInstitutionId());
        institution.setInstitutionName(institutionData.getInstitutionName());
        institution.setInstitutionDescription(institutionData.getInstitutionDescription());
        institution.setInstitutionStatus(institutionData.isInstitutionStatus());

        Log.d(TAG, "Convertida institución: " + institutionData.getDisplayName());
        return institution;
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR INSTITUCIONES ===");
        Log.e(TAG, "Error: " + t.getMessage());
        t.printStackTrace();

        showErrorMessage("Error de conexión", "Revisa tu conexión a internet");
        loadSimulatedInstitutions(); // Fallback
    }

    private void handleHttpError(Response<GetAllInstitutionsResponse> response) {
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

        loadSimulatedInstitutions(); // Fallback
    }

    private void loadSimulatedInstitutions() {
        Log.d(TAG, "=== CARGANDO DATOS SIMULADOS (FALLBACK) ===");

        institutionsList.clear();

        // Datos de ejemplo mejorados
        institutionsList.add(createSimulatedInstitution(
                "sim-1",
                "Hospital General Central",
                "Centro médico de alta complejidad con atención 24/7 y todas las especialidades médicas"
        ));

        institutionsList.add(createSimulatedInstitution(
                "sim-2",
                "Clínica San Rafael",
                "Atención ambulatoria especializada en medicina familiar y consultas generales"
        ));

        institutionsList.add(createSimulatedInstitution(
                "sim-3",
                "Centro Médico Aurora",
                "Diagnóstico por imágenes, laboratorio clínico y consultas especializadas"
        ));

        Log.i(TAG, "Datos simulados cargados: " + institutionsList.size() + " instituciones");

        adapter.updateData(institutionsList);
        updateInstitutionCount();
        showInfoMessage("Datos de ejemplo cargados");
    }

    private Institution createSimulatedInstitution(String id, String name, String description) {
        Institution institution = new Institution();
        institution.setInstitutionId(id);
        institution.setInstitutionName(name);
        institution.setInstitutionDescription(description);
        institution.setInstitutionStatus(true);
        return institution;
    }

    private void filterInstitutions(String query) {
        Log.d(TAG, "Filtrando instituciones con query: '" + query + "'");

        adapter.filter(query);
        updateInstitutionCount();

        // Mostrar estado vacío si no hay resultados después del filtro
        if (adapter.getFilteredCount() == 0 && !institutionsList.isEmpty()) {
            showEmptyState("Sin resultados",
                    "No se encontraron instituciones que coincidan con '" + query + "'");
        } else {
            hideEmptyState();
        }
    }

    private void updateInstitutionCount() {
        int count = adapter.getFilteredCount();
        String text = count == 1 ?
                count + " institución encontrada" :
                count + " instituciones encontradas";
        tvInstitutionCount.setText(text);
    }

    private void onInstitutionSelected(Institution institution) {
        Log.d(TAG, "Institución seleccionada: " + institution.getInstitutionName());

        Intent intent = new Intent(this, BranchListActivity.class);
        intent.putExtra("institutionId", institution.getInstitutionId());
        intent.putExtra("institutionName", institution.getInstitutionName());
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
                .setAction("Reintentar", v -> loadInstitutions())
                .show();
    }

    private void showInfoMessage(String message) {
        Snackbar.make(recyclerView, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.info))
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar si es necesario
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (searchHandler != null && searchRunnable != null) {
            searchHandler.removeCallbacks(searchRunnable);
        }
        Log.d(TAG, "InstitutionListActivity destruida");
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}