package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.GetAppointmentResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.PrescriptionItemAdapter;
import com.example.mediquick.data.model.PrescriptionItem;
import com.example.mediquick.utils.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppointmentDetailActivity extends AppCompatActivity {

    private static final String TAG = "AppointmentDetail";

    private TextView txtProcedimiento, txtFecha, txtDoctor, txtSucursal, txtNotas, txtEstado;
    private RecyclerView recyclerMedicamentos;
    private LinearLayout layoutReceta, loadingContainer;
    private ProgressBar progressBar;
    private androidx.cardview.widget.CardView cardSinReceta; // ✅ CAMBIO: CardView en lugar de TextView

    // API y sesión
    private AppointmentService apiService;
    private SessionManager sessionManager;
    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        initViews();
        setupSession();
        setupApiService();
        receiveIntentData();
        loadAppointmentDetails();
    }

    private void initViews() {
        txtProcedimiento = findViewById(R.id.txtProcedimiento);
        txtFecha = findViewById(R.id.txtFechaDetalle);
        txtDoctor = findViewById(R.id.txtDoctorDetalle);
        txtSucursal = findViewById(R.id.txtSucursalDetalle);
        txtNotas = findViewById(R.id.txtNotasGenerales);
        txtEstado = findViewById(R.id.txtEstado);

        // ✅ AGREGAR ESTA LÍNEA:
        cardSinReceta = findViewById(R.id.txtSinReceta);

        recyclerMedicamentos = findViewById(R.id.recyclerMedicamentos);
        layoutReceta = findViewById(R.id.layoutReceta);

        // Loading views
        loadingContainer = findViewById(R.id.loading_container);
        progressBar = findViewById(R.id.progress_bar);

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupSession() {
        sessionManager = new SessionManager(this);
        Log.d(TAG, "Sesión configurada");
    }

    private void setupApiService() {
        String userToken = sessionManager.getAuthToken();

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

    private void receiveIntentData() {
        appointmentId = getIntent().getStringExtra("appointmentId");

        Log.d(TAG, "=== DATOS RECIBIDOS ===");
        Log.d(TAG, "Appointment ID: " + appointmentId);

        if (appointmentId == null || appointmentId.isEmpty()) {
            Log.e(TAG, "⚠️ WARNING: appointmentId está vacío o es null");
            showError("Error: ID de cita no válido");
        } else {
            Log.i(TAG, "✅ appointmentId válido: " + appointmentId);
        }
    }

    private void loadAppointmentDetails() {
        if (appointmentId == null || appointmentId.isEmpty()) {
            Log.e(TAG, "No se puede cargar detalles: appointmentId es null o vacío");
            showError("Error: ID de cita no válido");
            loadFallbackData();
            return;
        }

        Log.d(TAG, "=== CARGANDO DETALLES DE LA CITA ===");
        Log.d(TAG, "Appointment ID: " + appointmentId);

        showLoading(true);

        apiService.getAppointmentById(appointmentId)
                .enqueue(new Callback<GetAppointmentResponse>() {
                    @Override
                    public void onResponse(Call<GetAppointmentResponse> call, Response<GetAppointmentResponse> response) {
                        showLoading(false);
                        handleAppointmentResponse(response);
                    }

                    @Override
                    public void onFailure(Call<GetAppointmentResponse> call, Throwable t) {
                        showLoading(false);
                        handleApiError(t);
                    }
                });
    }

    private void handleAppointmentResponse(Response<GetAppointmentResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE DETALLES RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAppointmentResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());

            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                updateUIWithAppointmentDetails(apiResponse.getData());
                Toast.makeText(this, "Detalles cargados correctamente", Toast.LENGTH_SHORT).show();
            } else {
                Log.w(TAG, "❌ No hay datos disponibles para esta cita");
                showError("No se encontraron detalles para esta cita");
                loadFallbackData();
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            handleHttpError(response);
        }
    }

    // En el método updateUIWithAppointmentDetails(), también agrega el manejo del cardSinReceta:
    private void updateUIWithAppointmentDetails(GetAppointmentResponse.AppointmentDetailData appointmentData) {
        Log.d(TAG, "=== ACTUALIZANDO UI CON DETALLES ===");

        // Información básica de la cita
        txtProcedimiento.setText(appointmentData.getProcedureName());
        txtFecha.setText(formatDateTime(appointmentData.getMedicalAppointmentDateTime()));
        txtDoctor.setText(getDoctorDisplayName(appointmentData));
        txtSucursal.setText(getBranchDisplayName(appointmentData));

        // Estado de la cita
        if (txtEstado != null && appointmentData.getMedicalAppointmentState() != null) {
            String estado = appointmentData.getMedicalAppointmentState().getMedicalAppointmentStateDescription();
            txtEstado.setText(estado);
            setStatusBadgeStyle(estado);
        }

        // Manejar prescripciones
        handlePrescriptions(appointmentData);

        Log.i(TAG, "✅ UI actualizada con detalles de la cita");
    }

    private void handlePrescriptions(GetAppointmentResponse.AppointmentDetailData appointmentData) {
        if (appointmentData.hasPrescriptions()) {
            Log.d(TAG, "Cita tiene " + appointmentData.getTotalPrescriptions() + " prescripciones");

            layoutReceta.setVisibility(View.VISIBLE);
            if (cardSinReceta != null) {
                cardSinReceta.setVisibility(View.GONE);
            }

            // ✅ CAMBIO: Procesar TODAS las prescripciones, no solo la primera
            List<PrescriptionItem> allMedicamentos = new ArrayList<>();
            StringBuilder allNotes = new StringBuilder();

            // Iterar por todas las prescripciones
            for (int i = 0; i < appointmentData.getPrescription().size(); i++) {
                GetAppointmentResponse.PrescriptionDetail prescription = appointmentData.getPrescription().get(i);

                Log.d(TAG, "Procesando prescripción " + (i + 1) + " de " + appointmentData.getPrescription().size());

                // Agregar notas de esta prescripción
                if (prescription.getPrescriptionNotes() != null && !prescription.getPrescriptionNotes().isEmpty()) {
                    if (allNotes.length() > 0) {
                        allNotes.append("\n\n"); // Separador entre prescripciones
                    }
                    allNotes.append("Prescripción ").append(i + 1).append(": ").append(prescription.getPrescriptionNotes());
                }

                // Agregar todos los medicamentos de esta prescripción
                List<PrescriptionItem> medicamentosDeEstaPrescripcion = convertPrescriptionItems(prescription);
                allMedicamentos.addAll(medicamentosDeEstaPrescripcion);
            }

            // Mostrar todas las notas combinadas
            if (allNotes.length() > 0) {
                txtNotas.setText(allNotes.toString());
            } else {
                txtNotas.setText("Sin notas adicionales");
            }

            // Configurar RecyclerView con TODOS los medicamentos
            recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this));
            recyclerMedicamentos.setAdapter(new PrescriptionItemAdapter(allMedicamentos));

            fixRecyclerViewHeight(recyclerMedicamentos);

            Log.d(TAG, "Total de medicamentos mostrados: " + allMedicamentos.size());

        } else {
            Log.d(TAG, "Cita sin prescripciones");
            layoutReceta.setVisibility(View.GONE);
            if (cardSinReceta != null) {
                cardSinReceta.setVisibility(View.VISIBLE);
            }
        }
    }

    private List<PrescriptionItem> convertPrescriptionItems(GetAppointmentResponse.PrescriptionDetail prescription) {
        List<PrescriptionItem> items = new ArrayList<>();

        if (prescription.hasItems()) {
            for (GetAppointmentResponse.PrescriptionItemDetail item : prescription.getPrescriptionItem()) {
                PrescriptionItem prescriptionItem = new PrescriptionItem(
                        item.getPrescriptionItemMedicationName(),
                        item.getPrescriptionItemDosage(),
                        item.getPrescriptionItemFrequency(),
                        item.getPrescriptionItemDuration(),
                        item.getPrescriptionItemItemNotes()
                );
                items.add(prescriptionItem);

                Log.d(TAG, "Medicamento agregado: " + item.getPrescriptionItemMedicationName());
            }
        }

        return items;
    }

    private String getDoctorDisplayName(GetAppointmentResponse.AppointmentDetailData appointmentData) {
        if (appointmentData.getDoctorUser() != null) {
            return "Dr. " + appointmentData.getDoctorUser().getFullName();
        } else {
            return "Sin doctor asignado";
        }
    }

    private String getBranchDisplayName(GetAppointmentResponse.AppointmentDetailData appointmentData) {
        if (appointmentData.getBranch() != null) {
            GetAppointmentResponse.BranchDetail branch = appointmentData.getBranch();
            String branchName = branch.getBranchName();

            if (branch.getInstitution() != null) {
                branchName += " (" + branch.getInstitution().getInstitutionAcronym() + ")";
            }

            return branchName;
        } else {
            return "Sucursal no especificada";
        }
    }

    private String formatDateTime(String isoDateTime) {
        if (isoDateTime == null || isoDateTime.isEmpty()) {
            return "Fecha no programada";
        }

        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MMM/yyyy - hh:mm a", Locale.getDefault());
            Date date = input.parse(isoDateTime);
            return output.format(date);
        } catch (Exception e) {
            Log.w(TAG, "Error formateando fecha: " + isoDateTime + " - " + e.getMessage());
            return isoDateTime;
        }
    }

    private void handleApiError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR DETALLES ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        Toast.makeText(this, "Error de conexión: " + t.getMessage(), Toast.LENGTH_LONG).show();
        loadFallbackData();
    }

    private void handleHttpError(Response<GetAppointmentResponse> response) {
        try {
            String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
            Log.e(TAG, "Error Body: " + errorBody);
            Toast.makeText(this, "Error HTTP " + response.code() + ": " + errorBody, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            Log.e(TAG, "Error leyendo error body", e);
            Toast.makeText(this, "Error HTTP " + response.code(), Toast.LENGTH_SHORT).show();
        }

        loadFallbackData();
    }

    private void loadFallbackData() {
        Log.d(TAG, "=== CARGANDO DATOS DE FALLBACK ===");

        // ✅ CREAR directamente la lista de PrescriptionItem (para el adapter)
        List<PrescriptionItem> fallbackItems = new ArrayList<>();
        String fallbackNotes = "";

        if ("sim-1".equals(appointmentId) || "1".equals(appointmentId)) {
            fallbackItems.add(new PrescriptionItem("Paracetamol", "500mg", "Cada 8h", "5 días", "Tomar con agua"));
            fallbackNotes = "Tomar con alimentos";

            txtProcedimiento.setText("Consulta General");
            txtFecha.setText("06/Jun/2025 - 12:45 PM");
            txtDoctor.setText("Dr. Roberto Martínez Hernández");
            txtSucursal.setText("Clínica Central San Salvador");

        } else if ("sim-2".equals(appointmentId) || "2".equals(appointmentId)) {
            fallbackItems.add(new PrescriptionItem("Amoxicilina", "250mg", "Cada 8h", "7 días", ""));
            fallbackNotes = "Control de fiebre";

            txtProcedimiento.setText("Pediatría");
            txtFecha.setText("01/Jun/2025 - 08:30 AM");
            txtDoctor.setText("Dr. Ana López");
            txtSucursal.setText("Clínica Norte Soyapango");

        } else {
            txtProcedimiento.setText("Sin detalles");
            txtFecha.setText("Fecha no disponible");
            txtDoctor.setText("Sin doctor asignado");
            txtSucursal.setText("Sucursal no especificada");
            fallbackNotes = "Sin receta médica";
        }

        // ✅ ACTUALIZAR UI con datos simulados
        if (fallbackItems.isEmpty()) {
            layoutReceta.setVisibility(View.GONE);
            if (cardSinReceta != null) {
                cardSinReceta.setVisibility(View.VISIBLE);
            }
        } else {
            layoutReceta.setVisibility(View.VISIBLE);
            if (cardSinReceta != null) {
                cardSinReceta.setVisibility(View.GONE);
            }
            txtNotas.setText(fallbackNotes);

            recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this));
            recyclerMedicamentos.setAdapter(new PrescriptionItemAdapter(fallbackItems));
        }

        Toast.makeText(this, "Mostrando datos de ejemplo", Toast.LENGTH_SHORT).show();
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (loadingContainer != null) {
            loadingContainer.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        Log.d(TAG, "Loading state: " + (show ? "VISIBLE" : "GONE"));
    }

    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();
    }

    /**
     * ✅ NUEVO: Establece el estilo del badge según el estado
     */
    private void setStatusBadgeStyle(String estado) {
        if (txtEstado == null) return;

        int backgroundRes;
        switch (estado.toLowerCase()) {
            case "created":
                backgroundRes = R.drawable.bg_status_created; // Naranja
                break;
            case "scheduled":
                backgroundRes = R.drawable.bg_status_scheduled; // Azul
                break;
            case "completed":
                backgroundRes = R.drawable.bg_status_completed; // Verde
                break;
            case "cancelled":
            case "canceled":
                backgroundRes = R.drawable.bg_status_cancelled; // Rojo
                break;
            default:
                backgroundRes = R.drawable.bg_status_badge; // Verde por defecto
                break;
        }

        txtEstado.setBackgroundResource(backgroundRes);
        Log.d(TAG, "Badge actualizado para estado: " + estado);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "AppointmentDetailActivity destruida");
    }

// Agrega este método a tu AppointmentDetailActivity:

    private void fixRecyclerViewHeight(RecyclerView recyclerView) {
        // Forzar que el RecyclerView calcule correctamente su altura
        recyclerView.post(() -> {
            recyclerView.getLayoutManager().onLayoutCompleted(null);

            // Calcular altura total de todos los items
            int totalHeight = 0;
            RecyclerView.Adapter adapter = recyclerView.getAdapter();
            if (adapter != null) {
                for (int i = 0; i < adapter.getItemCount(); i++) {
                    View item = recyclerView.getLayoutManager().findViewByPosition(i);
                    if (item != null) {
                        item.measure(
                                View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                        );
                        totalHeight += item.getMeasuredHeight();
                    }
                }

                // Establecer altura mínima
                ViewGroup.LayoutParams params = recyclerView.getLayoutParams();
                params.height = Math.max(totalHeight, 200); // Mínimo 200dp
                recyclerView.setLayoutParams(params);

                Log.d(TAG, "RecyclerView height fijada a: " + totalHeight + "px");
            }
        });
    }


}