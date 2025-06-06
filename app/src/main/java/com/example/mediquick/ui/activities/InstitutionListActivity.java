package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.mediquick.data.model.GetAllInstitutionsResponse;
import com.example.mediquick.data.model.Institution;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.InstitutionAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InstitutionListActivity extends AppCompatActivity {

    private static final String TAG = "InstitutionList";

    private RecyclerView recyclerView;
    private TextView emptyState;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshLayout;
    private InstitutionAdapter adapter;
    private List<Institution> lista = new ArrayList<>();

    // API
    private AppointmentService apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_list);

        initViews();
        setupApiService();
        setupRecyclerView();
        setupSwipeRefresh();

        cargarInstituciones();
    }

    private void initViews() {
        recyclerView = findViewById(R.id.recyclerInstituciones);
        emptyState = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBarInstituciones);
        //swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout); // Agregar al layout si no existe

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupApiService() {
        apiService = ApiClient.getUnauthenticatedClient(BuildConfig.BACKEND_BASE_URL).create(AppointmentService.class);

        Log.d(TAG, "API Service configurado con base URL: " + BuildConfig.BACKEND_BASE_URL);
    }

    private void setupRecyclerView() {
        adapter = new InstitutionAdapter(lista, selected -> {
            Log.d(TAG, "Institución seleccionada: " + selected.getInstitutionName());
            Intent intent = new Intent(this, BranchListActivity.class);
            intent.putExtra("institutionId", selected.getInstitutionId());
            intent.putExtra("institutionName", selected.getInstitutionName());
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
                cargarInstituciones();
            });
        }
    }

    private void cargarInstituciones() {
        Log.d(TAG, "=== CARGANDO INSTITUCIONES DESDE API ===");

        showLoading(true);

        apiService.getAllInstitutions()
                .enqueue(new Callback<GetAllInstitutionsResponse>() {
                    @Override
                    public void onResponse(Call<GetAllInstitutionsResponse> call, Response<GetAllInstitutionsResponse> response) {
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
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAllInstitutionsResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());
            Log.d(TAG, "Total instituciones recibidas: " + apiResponse.getTotalInstitutions());

            if (apiResponse.isSuccess() && apiResponse.hasInstitutions()) {
                updateInstitutionsList(apiResponse.getData());
                Toast.makeText(this, "Instituciones cargadas: " + apiResponse.getTotalInstitutions(), Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ No hay instituciones disponibles");
                showEmptyState("No hay instituciones disponibles");
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    private void updateInstitutionsList(List<GetAllInstitutionsResponse.InstitutionData> institutionsData) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA DE INSTITUCIONES ===");

        lista.clear();

        for (GetAllInstitutionsResponse.InstitutionData institutionData : institutionsData) {
            // Convertir InstitutionData a Institution (tu modelo actual)
            Institution institution = convertToInstitution(institutionData);

            lista.add(institution);
        }

        Log.i(TAG, "✅ Lista actualizada con " + lista.size() + " instituciones activas");

        adapter.notifyDataSetChanged();
        updateEmptyState();
    }

    private Institution convertToInstitution(GetAllInstitutionsResponse.InstitutionData institutionData) {
        Institution institution = new Institution();

        institution.setInstitutionId(institutionData.getInstitutionId());
        institution.setInstitutionName(institutionData.getInstitutionName());
        //.setInstitutionAcronym(institutionData.getInstitutionAcronym());
        institution.setInstitutionDescription(institutionData.getInstitutionDescription());
        institution.setInstitutionStatus(institutionData.isInstitutionStatus());
        //institution.setInstitutionLogo(institutionData.getInstitutionLogo());
        //institution.setInstitutionCreatedAt(institutionData.getInstitutionCreatedAt());
        //institution.setInstitutionUpdatedAt(institutionData.getInstitutionUpdatedAt());

        Log.d(TAG, "Convertida institución: " + institutionData.getDisplayName());

        return institution;
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR INSTITUCIONES ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();

        // Cargar datos simulados como fallback
        cargarInstitucionesSimuladas();
    }

    private void handleHttpError(Response<GetAllInstitutionsResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
            Toast.makeText(this, "Error HTTP " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
        }

        // Cargar datos simulados como fallback
        cargarInstitucionesSimuladas();
    }

    private void cargarInstitucionesSimuladas() {
        Log.d(TAG, "=== CARGANDO INSTITUCIONES SIMULADAS (FALLBACK) ===");

        lista.clear();

        Institution institution1 = new Institution();
        institution1.setInstitutionId("sim-1");
        institution1.setInstitutionName("Hospital Central");
        //institution1.setInstitutionAcronym("HC");
        institution1.setInstitutionDescription("Atención especializada y general.");
        institution1.setInstitutionStatus(true);
        lista.add(institution1);

        Institution institution2 = new Institution();
        institution2.setInstitutionId("sim-2");
        institution2.setInstitutionName("Clínica Regional");
        //institution2.setInstitutionAcronym("CR");
        institution2.setInstitutionDescription("Consultas y exámenes básicos.");
        institution2.setInstitutionStatus(true); // Cambiar a true para que aparezca
        lista.add(institution2);

        Log.i(TAG, "✅ Datos simulados cargados: " + lista.size() + " instituciones");

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
        boolean isEmpty = lista.isEmpty();
        emptyState.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        recyclerView.setVisibility(isEmpty ? View.GONE : View.VISIBLE);

        Log.d(TAG, "Empty state: " + (isEmpty ? "VISIBLE" : "GONE"));
    }

    private void showEmptyState(String message) {
        lista.clear();
        adapter.notifyDataSetChanged();

        if (emptyState instanceof TextView) {
            ((TextView) emptyState).setText(message);
        }

        updateEmptyState();
        Log.d(TAG, "Mostrando empty state: " + message);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Opcional: recargar instituciones al volver a la actividad
        // cargarInstituciones();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "InstitutionListActivity destruida");
    }
}