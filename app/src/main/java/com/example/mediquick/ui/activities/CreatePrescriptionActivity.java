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
        updateValidationStatus(); // Llamar al método más completo
    }

    private void setupRecyclerView() {
        medicamentos.add(new PrescriptionForm());
        formAdapter = new PrescriptionFormAdapter(medicamentos);

        // Configurar listener para acciones del adapter
        formAdapter.setOnMedicamentoActionListener(new PrescriptionFormAdapter.OnMedicamentoActionListener() {
            @Override
            public void onEliminarMedicamento(int position) {
                Log.d(TAG, "Eliminando medicamento en posición: " + position);
                formAdapter.eliminarMedicamento(position);
                updateMedicamentosCount();
            }

            @Override
            public void onMedicamentoValidado(int position, boolean esValido) {
                Log.d(TAG, "Medicamento " + (position + 1) + " validado: " + esValido);
                updateValidationStatus();
            }
        });

        recyclerForm.setAdapter(formAdapter);
        recyclerForm.setLayoutManager(new LinearLayoutManager(this));

        // Actualizar contador inicial
        updateMedicamentosCount();
        Log.d(TAG, "RecyclerView configurado con " + medicamentos.size() + " medicamento(s) inicial(es)");
    }

    // Nuevo método para actualizar estado de validación
    private void updateValidationStatus() {
        int medicamentosValidos = formAdapter.getMedicamentosValidos();
        int totalMedicamentos = medicamentos.size();

        // Actualizar el chip con información de validación
        Chip chipMedicamentosCount = findViewById(R.id.chipMedicamentosCount);
        if (chipMedicamentosCount != null) {
            String countText = medicamentosValidos + "/" + totalMedicamentos;
            chipMedicamentosCount.setText(countText);

            // Cambiar color según validación
            if (medicamentosValidos == totalMedicamentos && totalMedicamentos > 0) {
                chipMedicamentosCount.setChipBackgroundColor(
                        android.content.res.ColorStateList.valueOf(getColor(R.color.status_confirmed)));
            } else {
                chipMedicamentosCount.setChipBackgroundColor(
                        android.content.res.ColorStateList.valueOf(getColor(R.color.colorPrimary)));
            }
        }

        // Habilitar/deshabilitar botón guardar
        btnGuardar.setEnabled(formAdapter.todosMedicamentosValidos());
    }

    private void setupClickListeners() {
        btnAgregar.setOnClickListener(v -> {
            Log.d(TAG, "Agregando nuevo medicamento. Total actual: " + medicamentos.size());
            formAdapter.agregarMedicamento();
            updateMedicamentosCount();
            Log.d(TAG, "Nuevo medicamento agregado. Total ahora: " + medicamentos.size());
        });

        btnGuardar.setOnClickListener(v -> {
            Log.d(TAG, "Intentando guardar receta...");
            guardarReceta();
        });

        // Configurar toolbar navigation
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

                        // Si hay información de unidad, extraerla o usar valor por defecto
                        String unidad = extraerUnidadDeDosis(item.getPrescriptionItemDosage());
                        form.setUnidad(unidad);

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
            updateMedicamentosCount(); // Actualizar contador y validación
            Log.i(TAG, "✅ Prescripciones existentes cargadas: " + medicamentos.size() + " medicamentos");

        } else {
            Log.d(TAG, "No hay prescripciones existentes para esta cita");
            // Mantener el medicamento vacío por defecto
            updateMedicamentosCount();
        }
    }

    private String extraerUnidadDeDosis(String dosisCompleta) {
        if (dosisCompleta == null || dosisCompleta.isEmpty()) {
            return "mg"; // Valor por defecto
        }

        // Buscar patrones comunes de unidades al final del string
        String[] unidadesComunes = {"mg", "g", "ml", "cc", "gotas", "comprimidos", "cápsulas", "tabletas", "UI", "mcg"};

        for (String unidad : unidadesComunes) {
            if (dosisCompleta.toLowerCase().contains(unidad.toLowerCase())) {
                return unidad;
            }
        }

        return "mg"; // Valor por defecto si no se encuentra
    }

    private void mostrarResumenReceta() {
        StringBuilder resumen = new StringBuilder();
        resumen.append("=== RESUMEN DE LA RECETA ===\n");
        resumen.append("Paciente: ").append(nombrePaciente).append("\n");
        resumen.append("Total medicamentos: ").append(formAdapter.getMedicamentosValidos()).append("\n\n");

        for (int i = 0; i < medicamentos.size(); i++) {
            PrescriptionForm med = medicamentos.get(i);
            if (!med.getNombre().trim().isEmpty()) {
                resumen.append("Medicamento ").append(i + 1).append(":\n");
                resumen.append("  • ").append(med.getNombre()).append("\n");
                resumen.append("  • ").append(med.getDosis()).append(" ").append(med.getUnidad()).append("\n");
                resumen.append("  • ").append(med.getFrecuencia()).append("\n");
                resumen.append("  • Duración: ").append(med.getDuracion()).append("\n");
                if (!med.getNotas().trim().isEmpty()) {
                    resumen.append("  • Notas: ").append(med.getNotas()).append("\n");
                }
                resumen.append("\n");
            }
        }

        String notasGenerales = edtNotas.getText().toString().trim();
        if (!notasGenerales.isEmpty()) {
            resumen.append("Notas generales: ").append(notasGenerales).append("\n");
        }

        Log.d(TAG, resumen.toString());
    }
    private boolean validarFormularioCompleto() {
        boolean esValido = true;
        StringBuilder errores = new StringBuilder();

        // Validar que hay al menos un medicamento
        if (medicamentos.isEmpty()) {
            errores.append("• Debe agregar al menos un medicamento\n");
            esValido = false;
        }

        // Validar cada medicamento
        for (int i = 0; i < medicamentos.size(); i++) {
            PrescriptionForm med = medicamentos.get(i);

            if (med.getNombre().trim().isEmpty()) {
                errores.append("• Medicamento ").append(i + 1).append(": Falta el nombre\n");
                esValido = false;
            }

            if (med.getDosis().trim().isEmpty()) {
                errores.append("• Medicamento ").append(i + 1).append(": Falta la dosis\n");
                esValido = false;
            }

            if (med.getUnidad().trim().isEmpty()) {
                errores.append("• Medicamento ").append(i + 1).append(": Falta la unidad\n");
                esValido = false;
            }

            if (med.getFrecuencia().trim().isEmpty()) {
                errores.append("• Medicamento ").append(i + 1).append(": Falta la frecuencia\n");
                esValido = false;
            }

            if (med.getDuracion().trim().isEmpty()) {
                errores.append("• Medicamento ").append(i + 1).append(": Falta la duración\n");
                esValido = false;
            }
        }

        if (!esValido) {
            String mensajeError = "Por favor complete los siguientes campos:\n\n" + errores.toString();

            // Mostrar dialog con errores específicos
            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Campos Incompletos")
                    .setMessage(mensajeError)
                    .setPositiveButton("Entendido", null)
                    .setIcon(R.drawable.ic_info)
                    .show();
        }

        return esValido;
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

        // Usar validación del adapter
        if (!formAdapter.todosMedicamentosValidos()) {
            Log.w(TAG, "❌ VALIDACIÓN FALLIDA: Medicamentos incompletos");
            Toast.makeText(this, "Todos los campos obligatorios de medicamentos deben completarse", Toast.LENGTH_SHORT).show();
            return;
        }

        // Validar notas
        String notas = edtNotas.getText().toString().trim();
        Log.d(TAG, "Notas adicionales: '" + notas + "'");

        Log.i(TAG, "✅ VALIDACIÓN EXITOSA: " + formAdapter.getMedicamentosValidos() + " medicamentos válidos");

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