package com.example.mediquick.ui.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.GetAppointmentResponse;
import com.example.mediquick.data.model.PrescriptionForm;
import com.example.mediquick.data.model.PrescriptionRequest;
import com.example.mediquick.data.model.ResponsePrescription;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.ui.adapters.PrescriptionFormAdapter;
import com.google.android.material.chip.Chip;
import com.google.gson.Gson;
import androidx.core.widget.NestedScrollView;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CreatePrescriptionActivity extends AppCompatActivity {

    private static final String TAG = "CreatePrescription";

    private RecyclerView recyclerForm;
    private PrescriptionFormAdapter formAdapter;
    private List<PrescriptionForm> medicamentos = new ArrayList<>();
    private EditText edtNotas;
    private Button btnGuardar, btnAgregar;
    private TextView txtPaciente, txtCitaInfo;
    private ProgressBar progressBar;
    private NestedScrollView layoutContent;
    // Datos recibidos del Intent
    private String appointmentId;
    private String nombrePaciente;

    // API
    private AppointmentService apiService;

    // Datos de la cita obtenidos de la API
    private GetAppointmentResponse.AppointmentDetailData appointmentData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_prescription);

        // Recibir e imprimir datos del Intent
        receiveAndLogIntentData();

        initViews();
        setupApiService();
        setupRecyclerView();
        setupClickListeners();

        // Configurar UI con los datos recibidos
        setupPatientInfo();

        // Buscar detalles de la cita
        if (appointmentId != null && !appointmentId.isEmpty()) {
            loadAppointmentDetails();
        } else {
            Log.e(TAG, "No se puede cargar cita: appointmentId es null o vacío");
            showError("Error: ID de cita no válido");
        }
    }

    private void receiveAndLogIntentData() {
        Log.d(TAG, "=== DATOS RECIBIDOS EN CreatePrescriptionActivity ===");

        // Recibir appointmentId
        appointmentId = getIntent().getStringExtra("appointmentId");
        Log.d(TAG, "Appointment ID recibido: " + appointmentId);

        // Recibir nombre del paciente
        nombrePaciente = getIntent().getStringExtra("paciente");
        Log.d(TAG, "Nombre del paciente recibido: " + nombrePaciente);

        // Verificar si los datos son válidos
        if (appointmentId == null || appointmentId.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: appointmentId está vacío o es null");
        } else {
            Log.i(TAG, "✅ appointmentId válido: " + appointmentId);
        }

        if (nombrePaciente == null || nombrePaciente.isEmpty()) {
            Log.w(TAG, "⚠️ WARNING: nombrePaciente está vacío o es null");
        } else {
            Log.i(TAG, "✅ nombrePaciente válido: " + nombrePaciente);
        }

        // Log de todos los extras del Intent (por si hay más datos)
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            Log.d(TAG, "--- TODOS LOS EXTRAS DEL INTENT ---");
            for (String key : extras.keySet()) {
                Object value = extras.get(key);
                Log.d(TAG, key + " = " + value + " (Tipo: " + (value != null ? value.getClass().getSimpleName() : "null") + ")");
            }
            Log.d(TAG, "--- FIN DE EXTRAS ---");
        } else {
            Log.d(TAG, "No hay extras en el Intent");
        }

        Log.d(TAG, "=== FIN DE DATOS RECIBIDOS ===");
    }

    private void initViews() {
        recyclerForm = findViewById(R.id.recyclerForm);
        edtNotas = findViewById(R.id.edtNotas);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnAgregar = findViewById(R.id.btnAgregar);
        txtPaciente = findViewById(R.id.txtPacienteHeader);
        txtCitaInfo = findViewById(R.id.txtCitaInfo);
        progressBar = findViewById(R.id.progressBar);
        layoutContent = findViewById(R.id.layoutContent);

        // Nuevos elementos
        Chip chipMedicamentosCount = findViewById(R.id.chipMedicamentosCount);
        // Configurar toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }

        Log.d(TAG, "Views inicializadas correctamente");

        Log.d(TAG, "Views inicializadas correctamente");
    }

    private void setupApiService() {
        // Configurar API service (ajusta según tu configuración)
        String baseUrl = BuildConfig.BACKEND_BASE_URL;
        apiService = ApiClient.getUnauthenticatedClient(baseUrl).create(AppointmentService.class);

        // Si necesitas autenticación:
        // String token = getTokenFromSharedPreferences();
        // apiService = ApiClient.getAuthenticatedClient(baseUrl, token).create(AppointmentApiService.class);

        Log.d(TAG, "API Service configurado");
    }

    // Nuevo método para actualizar el contador de medicamentos
    private void updateMedicamentosCount() {
        Chip chipMedicamentosCount = findViewById(R.id.chipMedicamentosCount);
        if (chipMedicamentosCount != null) {
            chipMedicamentosCount.setText(String.valueOf(medicamentos.size()));
        }
    }

    private void setupRecyclerView() {
        medicamentos.add(new PrescriptionForm());
        formAdapter = new PrescriptionFormAdapter(medicamentos);
        recyclerForm.setAdapter(formAdapter);
        recyclerForm.setLayoutManager(new LinearLayoutManager(this));

        // Actualizar contador inicial
        updateMedicamentosCount();
        Log.d(TAG, "RecyclerView configurado con " + medicamentos.size() + " medicamento(s) inicial(es)");
    }

    // También agregar el import del Toolbar para evitar conflictos
// Y corregir setupClickListeners():
    private void setupClickListeners() {
        btnAgregar.setOnClickListener(v -> {
            Log.d(TAG, "Agregando nuevo medicamento. Total actual: " + medicamentos.size());
            medicamentos.add(new PrescriptionForm());
            formAdapter.notifyItemInserted(medicamentos.size() - 1);
            updateMedicamentosCount();
            Log.d(TAG, "Nuevo medicamento agregado. Total ahora: " + medicamentos.size());
        });

        btnGuardar.setOnClickListener(v -> {
            Log.d(TAG, "Intentando guardar receta...");
            guardarReceta();
        });

        // Configurar toolbar navigation - USAR androidx.appcompat.widget.Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());
    }

    private void setupPatientInfo() {
        String headerText = "Receta para: " + (nombrePaciente != null ? nombrePaciente : "Paciente no especificado");
        txtPaciente.setText(headerText);

        Log.d(TAG, "Header del paciente configurado: " + headerText);
        Log.d(TAG, "Appointment ID almacenado: " + appointmentId);
    }

    private void loadAppointmentDetails() {
        Log.d(TAG, "=== CARGANDO DETALLES DE LA CITA ===");
        Log.d(TAG, "Buscando cita con ID: " + appointmentId);

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
                        handleAppointmentError(t);
                    }
                });
    }

    private void handleAppointmentResponse(Response<GetAppointmentResponse> response) {
        Log.d(TAG, "=== RESPUESTA DE CITA RECIBIDA ===");
        Log.d(TAG, "Response Code: " + response.code());
        Log.d(TAG, "Request URL: " + response.raw().request().url().toString());

        if (response.isSuccessful() && response.body() != null) {
            GetAppointmentResponse apiResponse = response.body();
            Log.d(TAG, "API Success: " + apiResponse.isSuccess());
            Log.d(TAG, "Message: " + apiResponse.getMessage());

            if (apiResponse.isSuccess() && apiResponse.getData() != null) {
                appointmentData = apiResponse.getData();
                displayAppointmentDetails();
                loadExistingPrescriptions();
                Log.i(TAG, "✅ Detalles de cita cargados exitosamente");
            } else {
                Log.w(TAG, "❌ API respondió con error: " + apiResponse.getMessage());
                showError("Error: " + apiResponse.getMessage());
            }
        } else {
            Log.e(TAG, "❌ Error HTTP: " + response.code());
            try {
                String errorBody = response.errorBody() != null ? response.errorBody().string() : "Sin detalles";
                Log.e(TAG, "Error Body: " + errorBody);
                showError("Error HTTP " + response.code() + ": " + errorBody);
            } catch (Exception e) {
                showError("Error HTTP " + response.code());
            }
        }
    }

    private void handleAppointmentError(Throwable t) {
        Log.e(TAG, "=== ERROR AL CARGAR CITA ===");
        Log.e(TAG, "Error Type: " + t.getClass().getSimpleName());
        Log.e(TAG, "Error Message: " + t.getMessage());
        t.printStackTrace();

        showError("Error de conexión: " + t.getMessage());
    }

    private void displayAppointmentDetails() {
        Log.d(TAG, "=== MOSTRANDO DETALLES DE LA CITA ===");

        // Actualizar información del paciente
        String patientName = appointmentData.getPatientFullName();
        String doctorName = appointmentData.getDoctorFullName();
        String procedureName = appointmentData.getProcedureName();
        String branchName = appointmentData.getBranchName();
        String appointmentDate = appointmentData.getFormattedDateTime();
        String appointmentState = appointmentData.getMedicalAppointmentState() != null ?
                appointmentData.getMedicalAppointmentState().getMedicalAppointmentStateDescription() : "Sin estado";

        Log.d(TAG, "Paciente: " + patientName);
        Log.d(TAG, "Doctor: " + doctorName);
        Log.d(TAG, "Procedimiento: " + procedureName);
        Log.d(TAG, "Sucursal: " + branchName);
        Log.d(TAG, "Fecha: " + appointmentDate);
        Log.d(TAG, "Estado: " + appointmentState);

        // Actualizar UI
        txtPaciente.setText("Receta para: " + patientName);

        if (txtCitaInfo != null) {
            String citaInfo = "Dr. " + doctorName + " • " + procedureName + "\n" +
                    branchName + " • " + appointmentDate + "\n" +
                    "Estado: " + appointmentState;
            txtCitaInfo.setText(citaInfo);
            txtCitaInfo.setVisibility(View.VISIBLE);
        }
    }

    // Actualizar loadExistingPrescriptions() para incluir el contador
    private void loadExistingPrescriptions() {
        Log.d(TAG, "=== CARGANDO PRESCRIPCIONES EXISTENTES ===");

        if (appointmentData.hasPrescriptions()) {
            Log.d(TAG, "Encontradas " + appointmentData.getTotalPrescriptions() + " prescripción(es)");

            // Limpiar medicamentos actuales
            medicamentos.clear();

            for (GetAppointmentResponse.PrescriptionDetail prescription : appointmentData.getPrescription()) {
                Log.d(TAG, "--- Prescripción ---");
                Log.d(TAG, "Notas: " + prescription.getPrescriptionNotes());
                Log.d(TAG, "Fecha emisión: " + prescription.getPrescriptionFechaEmision());
                Log.d(TAG, "Total medicamentos: " + prescription.getTotalMedicamentos());

                // Llenar notas de la prescripción
                if (prescription.getPrescriptionNotes() != null && !prescription.getPrescriptionNotes().isEmpty()) {
                    edtNotas.setText(prescription.getPrescriptionNotes());
                }

                // Cargar medicamentos existentes
                if (prescription.hasItems()) {
                    for (GetAppointmentResponse.PrescriptionItemDetail item : prescription.getPrescriptionItem()) {
                        PrescriptionForm form = new PrescriptionForm();
                        form.setNombre(item.getPrescriptionItemMedicationName());
                        form.setDosis(item.getPrescriptionItemDosage());
                        form.setFrecuencia(item.getPrescriptionItemFrequency());
                        form.setDuracion(item.getPrescriptionItemDuration());
                        form.setNotas(item.getPrescriptionItemItemNotes());

                        medicamentos.add(form);

                        Log.d(TAG, "Medicamento cargado: " + item.getMedicationSummary());
                    }
                }
            }

            // Si no hay medicamentos, agregar uno vacío
            if (medicamentos.isEmpty()) {
                medicamentos.add(new PrescriptionForm());
            }

            formAdapter.notifyDataSetChanged();
            updateMedicamentosCount(); // Actualizar contador
            Log.i(TAG, "✅ Prescripciones existentes cargadas: " + medicamentos.size() + " medicamentos");

        } else {
            Log.d(TAG, "No hay prescripciones existentes para esta cita");
            // Mantener el medicamento vacío por defecto
            updateMedicamentosCount();
        }
    }

    private void showLoading(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }

        if (layoutContent != null) {
            layoutContent.setVisibility(show ? View.GONE : View.VISIBLE);
        }

        btnGuardar.setEnabled(!show);
        btnAgregar.setEnabled(!show);
    }

    private void showError(String message) {
        Log.e(TAG, "Mostrando error: " + message);
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Permitir al usuario seguir usando la app incluso si falla la carga
        if (layoutContent != null) {
            layoutContent.setVisibility(View.VISIBLE);
        }
    }

    private void guardarReceta() {
        Log.d(TAG, "=== INICIANDO PROCESO DE GUARDADO ===");
        Log.d(TAG, "Appointment ID para guardar: " + appointmentId);
        Log.d(TAG, "Paciente: " + nombrePaciente);
        Log.d(TAG, "Total de medicamentos a validar: " + medicamentos.size());

        // Validar medicamentos
        boolean todosCamposCompletos = true;
        for (int i = 0; i < medicamentos.size(); i++) {
            PrescriptionForm m = medicamentos.get(i);
            Log.d(TAG, "--- Medicamento " + (i + 1) + " ---");
            Log.d(TAG, "  Nombre: '" + m.getNombre() + "'");
            Log.d(TAG, "  Dosis: '" + m.getDosis() + "'");
            Log.d(TAG, "  Frecuencia: '" + m.getFrecuencia() + "'");
            Log.d(TAG, "  Duración: '" + m.getDuracion() + "'");

            if (m.getNombre().isEmpty() || m.getDosis().isEmpty() ||
                    m.getFrecuencia().isEmpty() || m.getDuracion().isEmpty()) {
                Log.w(TAG, "❌ Medicamento " + (i + 1) + " tiene campos vacíos");
                todosCamposCompletos = false;
            } else {
                Log.i(TAG, "✅ Medicamento " + (i + 1) + " está completo");
            }
        }

        // Validar notas
        String notas = edtNotas.getText().toString().trim();
        Log.d(TAG, "Notas adicionales: '" + notas + "'");

        if (!todosCamposCompletos) {
            Log.w(TAG, "❌ VALIDACIÓN FALLIDA: Campos incompletos");
            Toast.makeText(this, "Todos los campos obligatorios deben completarse", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.i(TAG, "✅ VALIDACIÓN EXITOSA: Todos los campos están completos");

        // Crear request para la API
        PrescriptionRequest request = PrescriptionRequest.fromFormData(appointmentId, notas, medicamentos);
        Log.d(TAG, "Request creado: " + new Gson().toJson(request));

        // Enviar a la API
        enviarPrescripcionAPI(request);
    }

    // Actualizar el método enviarPrescripcionAPI() para el nuevo botón
    private void enviarPrescripcionAPI(PrescriptionRequest request) {
        Log.d(TAG, "Enviando prescripción a API...");

        // Deshabilitar botón mientras se procesa
        btnGuardar.setEnabled(false);
        btnGuardar.setText("Guardando...");
        //btnGuardar.setIcon(null); // Quitar icono temporalmente

        apiService.createPrescription(request)
                .enqueue(new Callback<ResponsePrescription>() {
                    @Override
                    public void onResponse(Call<ResponsePrescription> call, Response<ResponsePrescription> response) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar Receta");
                       // btnGuardar.setIcon(ContextCompat.getDrawable(CreatePrescriptionActivity.this, R.drawable.ic_save));

                        Log.d(TAG, "Respuesta recibida - Código: " + response.code());

                        if (response.isSuccessful() && response.body() != null) {
                            ResponsePrescription apiResponse = response.body();
                            Log.d(TAG, "API Response: " + apiResponse.toString());

                            if (apiResponse.isSuccess()) {
                                ResponsePrescription.PrescriptionData data = apiResponse.getData();
                                Log.i(TAG, "✅ Prescripción creada exitosamente");
                                Log.i(TAG, "ID de prescripción: " + data.getPrescriptionId());

                                Toast.makeText(CreatePrescriptionActivity.this,
                                        "✅ " + apiResponse.getMessage() + " (ID: " + data.getPrescriptionId() + ")",
                                        Toast.LENGTH_LONG).show();
                                finish();
                            } else {
                                Log.w(TAG, "❌ Error de la API: " + apiResponse.getMessage());
                                Toast.makeText(CreatePrescriptionActivity.this,
                                        "Error: " + apiResponse.getMessage(),
                                        Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Log.e(TAG, "❌ Error HTTP: " + response.code());
                            Toast.makeText(CreatePrescriptionActivity.this,
                                    "Error HTTP " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponsePrescription> call, Throwable t) {
                        btnGuardar.setEnabled(true);
                        btnGuardar.setText("Guardar Receta");
                       // btnGuardar.setIcon(ContextCompat.getDrawable(CreatePrescriptionActivity.this, R.drawable.ic_save));

                        Log.e(TAG, "❌ Error de conexión: " + t.getMessage());
                        Toast.makeText(CreatePrescriptionActivity.this,
                                "Error de conexión: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Método para obtener token de SharedPreferences (si necesitas autenticación)
    private String getTokenFromSharedPreferences() {
        SharedPreferences prefs = getSharedPreferences("MediQuick", Context.MODE_PRIVATE);
        return prefs.getString("auth_token", "");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "CreatePrescriptionActivity destruida");
    }
}