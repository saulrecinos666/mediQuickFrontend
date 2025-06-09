package com.example.mediquick.ui.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentResponse;
import com.example.mediquick.services.AppointmentService;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AdminAcceptAppointmentsActivity extends AppCompatActivity {

    private static final String TAG = "ADMIN_APPOINTMENTS";

    // UI Components
    private MaterialToolbar toolbar;
    private RecyclerView recyclerAppointments;
    private TextView emptyView;
    private View loadingView;
    private TextView tvAppointmentCount;
    private MaterialButton btnRefresh, btnRefreshEmpty, btnFilter;

    // Data and Adapter
    private List<Appointment> appointments = new ArrayList<>();
    private AppointmentAdapter adapter;

    // API Service
    private AppointmentService appointmentService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_accept_appointments);

        Log.d(TAG, "=== INICIANDO ACTIVITY ===");

        initializeViews();
        setupToolbar();
        setupRecyclerView();
        setupClickListeners();
        initializeRetrofit();

        // Load appointments on start
        loadAppointments();
    }

    /**
     * Initialize all view components con manejo seguro de tipos
     */
    @SuppressLint("WrongViewCast")
    private void initializeViews() {
        Log.d(TAG, "=== INICIALIZANDO VISTAS ===");

        try {
            // Buscar RecyclerView (obligatorio)
            recyclerAppointments = findViewById(R.id.recyclerAppointments);
            if (recyclerAppointments == null) {
                Log.e(TAG, "❌ RecyclerView NOT FOUND! Check your layout file.");
                finish();
                return;
            } else {
                Log.d(TAG, "✅ RecyclerView encontrado correctamente");
            }

            // Buscar emptyView (obligatorio)
            View emptyViewCandidate = findViewById(R.id.emptyView);
            if (emptyViewCandidate instanceof TextView) {
                emptyView = (TextView) emptyViewCandidate;
                Log.d(TAG, "✅ EmptyView (TextView) encontrado correctamente");
            } else if (emptyViewCandidate != null) {
                Log.w(TAG, "⚠️ emptyView encontrado pero no es TextView, tipo: " + emptyViewCandidate.getClass().getSimpleName());
                // Crear un TextView dummy si no es del tipo correcto
                emptyView = new TextView(this);
                emptyView.setText("No hay citas pendientes");
                emptyView.setVisibility(View.GONE);
            } else {
                Log.w(TAG, "⚠️ emptyView no encontrado, creando uno dummy");
                emptyView = new TextView(this);
                emptyView.setText("No hay citas pendientes");
                emptyView.setVisibility(View.GONE);
            }

            // Buscar componentes opcionales del layout mejorado
            initializeOptionalViews();

        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing views: " + e.getMessage(), e);
            showToast("Error al inicializar la interfaz");
        }
    }

    /**
     * Inicializar vistas opcionales (pueden no existir en todos los layouts)
     */
    private void initializeOptionalViews() {
        // Toolbar (opcional)
        try {
            View toolbarCandidate = findViewById(R.id.toolbar);
            if (toolbarCandidate instanceof MaterialToolbar) {
                toolbar = (MaterialToolbar) toolbarCandidate;
                Log.d(TAG, "✅ Toolbar encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ Toolbar not found: " + e.getMessage());
        }

        // TextView para contador de citas (opcional)
        try {
            View countCandidate = findViewById(R.id.tvAppointmentCount);
            if (countCandidate instanceof TextView) {
                tvAppointmentCount = (TextView) countCandidate;
                Log.d(TAG, "✅ tvAppointmentCount encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ tvAppointmentCount not found: " + e.getMessage());
        }

        // Botones (opcionales)
        try {
            View refreshCandidate = findViewById(R.id.btnRefresh);
            if (refreshCandidate instanceof MaterialButton) {
                btnRefresh = (MaterialButton) refreshCandidate;
                Log.d(TAG, "✅ btnRefresh encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ btnRefresh not found: " + e.getMessage());
        }

        try {
            View refreshEmptyCandidate = findViewById(R.id.btnRefreshEmpty);
            if (refreshEmptyCandidate instanceof MaterialButton) {
                btnRefreshEmpty = (MaterialButton) refreshEmptyCandidate;
                Log.d(TAG, "✅ btnRefreshEmpty encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ btnRefreshEmpty not found: " + e.getMessage());
        }

        try {
            View filterCandidate = findViewById(R.id.btnFilter);
            if (filterCandidate instanceof MaterialButton) {
                btnFilter = (MaterialButton) filterCandidate;
                Log.d(TAG, "✅ btnFilter encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ btnFilter not found: " + e.getMessage());
        }

        // Vista de loading (opcional)
        try {
            loadingView = findViewById(R.id.loadingView);
            if (loadingView != null) {
                Log.d(TAG, "✅ loadingView encontrado");
            }
        } catch (Exception e) {
            Log.w(TAG, "⚠️ loadingView not found: " + e.getMessage());
        }
    }

    /**
     * Setup toolbar with navigation - Solo si existe
     */
    private void setupToolbar() {
        Log.d(TAG, "=== CONFIGURANDO TOOLBAR ===");

        if (toolbar != null) {
            try {
                setSupportActionBar(toolbar);
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                }
                toolbar.setNavigationOnClickListener(v -> onBackPressed());
                Log.d(TAG, "✅ Toolbar configured successfully");
            } catch (Exception e) {
                Log.e(TAG, "❌ Error setting up toolbar: " + e.getMessage());
            }
        } else {
            // Para layouts sin toolbar, configurar ActionBar por defecto
            try {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle("Gestión de Citas");
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    Log.d(TAG, "✅ ActionBar configured successfully");
                }
            } catch (Exception e) {
                Log.e(TAG, "❌ Error setting up ActionBar: " + e.getMessage());
            }
        }
    }

    /**
     * Setup RecyclerView with adapter and layout manager
     */
    private void setupRecyclerView() {
        Log.d(TAG, "=== CONFIGURANDO RECYCLERVIEW ===");

        try {
            Log.d(TAG, "Creando adapter con lista inicial de tamaño: " + appointments.size());

            adapter = new AppointmentAdapter(appointments, this, appointment -> {
                Log.d(TAG, "Cita clickeada: " + appointment.getId());
                showAssignAppointmentDialog(appointment);
            });

            recyclerAppointments.setAdapter(adapter);
            recyclerAppointments.setLayoutManager(new LinearLayoutManager(this));

            Log.d(TAG, "✅ Adapter y LayoutManager configurados");
            Log.d(TAG, "RecyclerView visibility: " + recyclerAppointments.getVisibility());

            // Add item decoration for better spacing (opcional)
            try {
                recyclerAppointments.addItemDecoration(new androidx.recyclerview.widget.DividerItemDecoration(
                        this, androidx.recyclerview.widget.DividerItemDecoration.VERTICAL));
                Log.d(TAG, "✅ Item decoration agregada");
            } catch (Exception e) {
                Log.w(TAG, "⚠️ Could not add item decoration: " + e.getMessage());
            }

            Log.d(TAG, "✅ RecyclerView configured successfully");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error setting up RecyclerView: " + e.getMessage());
            showToast("Error al configurar la lista de citas");
        }
    }

    /**
     * Setup click listeners for buttons - Solo para los que existen
     */
    private void setupClickListeners() {
        if (btnRefresh != null) {
            btnRefresh.setOnClickListener(v -> refreshAppointments());
        }

        if (btnRefreshEmpty != null) {
            btnRefreshEmpty.setOnClickListener(v -> refreshAppointments());
        }

        if (btnFilter != null) {
            btnFilter.setOnClickListener(v -> showFilterDialog());
        }
    }

    /**
     * Initialize Retrofit service
     */
    private void initializeRetrofit() {
        try {
            // Add logging interceptor for debugging
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

            OkHttpClient client = new OkHttpClient.Builder()
                    .addInterceptor(loggingInterceptor)
                    .build();

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BuildConfig.BACKEND_BASE_URL)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            appointmentService = retrofit.create(AppointmentService.class);
            Log.d(TAG, "✅ Retrofit initialized successfully");
        } catch (Exception e) {
            Log.e(TAG, "❌ Error initializing Retrofit: " + e.getMessage());
            showToast("Error al configurar conexión con servidor");
        }
    }

    /**
     * Load appointments from server
     */
    private void loadAppointments() {
        Log.d(TAG, "=== CARGANDO CITAS ===");
        showLoadingState();

        Log.d(TAG, "Base URL: " + BuildConfig.BACKEND_BASE_URL);

        try {
            Call<AppointmentResponse> call = appointmentService.getAllAppointments("1");
            Log.d(TAG, "Request URL: " + call.request().url().toString());

            call.enqueue(new Callback<AppointmentResponse>() {
                @Override
                public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                    Log.d(TAG, "=== RESPUESTA RECIBIDA ===");
                    runOnUiThread(() -> {
                        handleAppointmentResponse(response);
                    });
                }

                @Override
                public void onFailure(Call<AppointmentResponse> call, Throwable t) {
                    Log.e(TAG, "❌ Failed to load appointments: " + t.getMessage(), t);
                    runOnUiThread(() -> {
                        showErrorState();
                        showToast("Error al cargar las citas. Verifica tu conexión.");
                    });
                }
            });
        } catch (Exception e) {
            Log.e(TAG, "❌ Error making API call: " + e.getMessage());
            showErrorState();
            showToast("Error al realizar petición al servidor");
        }
    }

    /**
     * Handle the response from appointments API
     */
    private void handleAppointmentResponse(Response<AppointmentResponse> response) {
        Log.d(TAG, "=== PROCESANDO RESPUESTA ===");
        Log.d(TAG, "Response code: " + response.code());

        try {
            if (response.isSuccessful() && response.body() != null) {
                AppointmentResponse appointmentResponse = response.body();
                Log.d(TAG, "✅ Response successful");
                Log.d(TAG, "Success: " + appointmentResponse.isSuccess());
                Log.d(TAG, "Message: " + appointmentResponse.getMessage());

                List<com.example.mediquick.data.model.Appointment> fetchedAppointments = appointmentResponse.getData();

                if (fetchedAppointments != null) {
                    Log.d(TAG, "🔢 Total appointments fetched: " + fetchedAppointments.size());
                } else {
                    Log.e(TAG, "❌ Fetched appointments is NULL!");
                    showEmptyState();
                    return;
                }

                // Clear existing appointments
                appointments.clear();
                Log.d(TAG, "🗑️ Lista local limpiada");

                // Convert and add new appointments
                int successfullyProcessed = 0;
                for (int i = 0; i < fetchedAppointments.size(); i++) {
                    com.example.mediquick.data.model.Appointment serverAppointment = fetchedAppointments.get(i);

                    try {
                        Log.d(TAG, "--- Procesando cita " + (i + 1) + " ---");
                        Log.d(TAG, "ID: " + serverAppointment.getMedical_appointment_id());

                        // Extraer datos con logs detallados
                        String appointmentId = serverAppointment.getMedical_appointment_id();
                        Log.d(TAG, "ID extraído: " + appointmentId);

                        String appointmentDate = formatAppointmentDate(serverAppointment.getMedical_appointment_date_time());
                        Log.d(TAG, "Fecha extraída: " + appointmentDate);

                        String patientName = getPatientName(serverAppointment);
                        Log.d(TAG, "Paciente extraído: " + patientName);

                        String branchName = getBranchName(serverAppointment);
                        Log.d(TAG, "Sucursal extraída: " + branchName);

                        // Crear cita local
                        Appointment localAppointment = new Appointment(
                                appointmentId,
                                patientName,
                                branchName,
                                appointmentDate
                        );

                        appointments.add(localAppointment);
                        successfullyProcessed++;

                        Log.d(TAG, "✅ Cita " + (i + 1) + " agregada exitosamente");

                    } catch (Exception e) {
                        Log.e(TAG, "❌ Error processing appointment " + (i + 1) + ": " + e.getMessage(), e);
                    }
                }

                Log.d(TAG, "🎯 RESUMEN PROCESAMIENTO:");
                Log.d(TAG, "- Citas recibidas del servidor: " + fetchedAppointments.size());
                Log.d(TAG, "- Citas procesadas exitosamente: " + successfullyProcessed);
                Log.d(TAG, "- Citas en lista local: " + appointments.size());

                // Update UI
                updateAdapterSafely();
                updateAppointmentCount();
                showContentState();

            } else {
                Log.e(TAG, "❌ Unsuccessful response: " + response.code());
                handleApiError(response);
                showEmptyState();
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error handling API response: " + e.getMessage(), e);
            showErrorState();
        }
    }

    // Agregar este método en tu Activity, justo después de updateAdapterSafely():

    /**
     * Actualizar adapter de manera segura - VERSIÓN CON DEBUG EXTRA
     */
    private void updateAdapterSafely() {
        Log.d(TAG, "=== ACTUALIZANDO ADAPTER ===");

        try {
            if (adapter != null) {
                Log.d(TAG, "Adapter existe, intentando actualizar...");
                Log.d(TAG, "Lista actual tiene " + appointments.size() + " elementos");

                // DEBUG: Mostrar los primeros elementos de la lista
                if (!appointments.isEmpty()) {
                    Log.d(TAG, "Primeros elementos a enviar al adapter:");
                    for (int i = 0; i < Math.min(3, appointments.size()); i++) {
                        Appointment app = appointments.get(i);
                        Log.d(TAG, "  [" + i + "] ID: " + (app != null ? app.getId() : "null"));
                    }
                }

                // Verificar si el adapter tiene el método updateAppointments
                try {
                    // Si tienes el adapter mejorado, esto funcionará
                    adapter.updateAppointments(new ArrayList<>(appointments));
                    Log.d(TAG, "✅ Adapter actualizado con updateAppointments()");

                    // DEBUG EXTRA: Verificar estado del adapter después de la actualización
                    if (adapter instanceof AppointmentAdapter) {
                        ((AppointmentAdapter) adapter).logCurrentState();
                    }

                } catch (Exception e) {
                    // Si usas el adapter original, usar notifyDataSetChanged
                    Log.w(TAG, "updateAppointments() no disponible, usando notifyDataSetChanged()");
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "✅ Adapter actualizado con notifyDataSetChanged()");
                }

                // Verificar el RecyclerView después de la actualización
                Log.d(TAG, "Items en adapter después de actualización: " + adapter.getItemCount());
                Log.d(TAG, "RecyclerView visibility: " + recyclerAppointments.getVisibility());

                // AGREGADO: Forzar refresh del RecyclerView
                recyclerAppointments.post(() -> {
                    Log.d(TAG, "Forzando refresh del RecyclerView...");
                    adapter.notifyDataSetChanged();
                    Log.d(TAG, "Items después del refresh forzado: " + adapter.getItemCount());
                });

            } else {
                Log.e(TAG, "❌ Adapter es NULL!");
            }
        } catch (Exception e) {
            Log.e(TAG, "❌ Error updating adapter: " + e.getMessage(), e);
        }
    }

    /**
     * Format appointment date safely
     */
    private String formatAppointmentDate(String dateTime) {
        String result = (dateTime == null || dateTime.trim().isEmpty()) ? "Sin fecha asignada" : dateTime;
        Log.d(TAG, "Fecha formateada: " + result);
        return result;
    }

    /**
     * Get patient name safely
     */
    private String getPatientName(com.example.mediquick.data.model.Appointment appointment) {
        try {
            if (appointment.getPatient_user() != null) {
                // Construir el nombre completo
                String firstName = appointment.getPatient_user().getUser_first_name();
                String secondName = appointment.getPatient_user().getUser_second_name();
                String firstLastname = appointment.getPatient_user().getUser_first_lastname();
                String secondLastname = appointment.getPatient_user().getUser_second_lastname();

                StringBuilder fullName = new StringBuilder();
                if (firstName != null && !firstName.trim().isEmpty()) {
                    fullName.append(firstName);
                }
                if (secondName != null && !secondName.trim().isEmpty()) {
                    if (fullName.length() > 0) fullName.append(" ");
                    fullName.append(secondName);
                }
                if (firstLastname != null && !firstLastname.trim().isEmpty()) {
                    if (fullName.length() > 0) fullName.append(" ");
                    fullName.append(firstLastname);
                }
                if (secondLastname != null && !secondLastname.trim().isEmpty()) {
                    if (fullName.length() > 0) fullName.append(" ");
                    fullName.append(secondLastname);
                }

                String result = fullName.toString().trim();
                if (!result.isEmpty()) {
                    Log.d(TAG, "Nombre completo construido: " + result);
                    return result;
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting patient name: " + e.getMessage());
        }
        Log.d(TAG, "Usando nombre por defecto");
        return "Paciente no especificado";
    }

    /**
     * Get branch name safely
     */
    private String getBranchName(com.example.mediquick.data.model.Appointment appointment) {
        try {
            if (appointment.getBranch() != null &&
                    appointment.getBranch().getBranch_name() != null) {
                String result = appointment.getBranch().getBranch_name();
                Log.d(TAG, "Nombre de sucursal: " + result);
                return result;
            }
        } catch (Exception e) {
            Log.w(TAG, "Error getting branch name: " + e.getMessage());
        }
        Log.d(TAG, "Usando sucursal por defecto");
        return "Sucursal no especificada";
    }

    /**
     * Handle API errors
     */
    private void handleApiError(Response<AppointmentResponse> response) {
        try {
            String errorBody = response.errorBody() != null ?
                    response.errorBody().string() : "No error details";
            Log.e(TAG, "API Error - Code: " + response.code() + ", Body: " + errorBody);
        } catch (Exception e) {
            Log.e(TAG, "Could not read error body", e);
        }

        showToast("Error del servidor: " + response.code());
    }

    /**
     * Update appointment count display - Solo si existe el TextView
     */
    private void updateAppointmentCount() {
        if (tvAppointmentCount != null) {
            try {
                int count = appointments.size();
                String countText = count == 0 ? "No hay citas pendientes" :
                        count + (count == 1 ? " cita pendiente" : " citas pendientes");
                tvAppointmentCount.setText(countText);
                Log.d(TAG, "✅ Contador actualizado: " + countText);
            } catch (Exception e) {
                Log.w(TAG, "Error updating appointment count: " + e.getMessage());
            }
        }
    }

    /**
     * Show loading state - Solo si existe loadingView
     */
    private void showLoadingState() {
        Log.d(TAG, "=== MOSTRANDO ESTADO DE CARGA ===");
        try {
            if (loadingView != null) {
                loadingView.setVisibility(View.VISIBLE);
                Log.d(TAG, "✅ Loading view visible");
            }
            if (recyclerAppointments != null) {
                recyclerAppointments.setVisibility(View.GONE);
                Log.d(TAG, "✅ RecyclerView hidden");
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.GONE);
                Log.d(TAG, "✅ Empty view hidden");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error showing loading state: " + e.getMessage());
        }
    }

    /**
     * Show content state with appointments
     */
    private void showContentState() {
        Log.d(TAG, "=== MOSTRANDO ESTADO DE CONTENIDO ===");
        Log.d(TAG, "Appointments count: " + appointments.size());

        try {
            if (loadingView != null) {
                loadingView.setVisibility(View.GONE);
                Log.d(TAG, "✅ Loading view hidden");
            }

            if (appointments.isEmpty()) {
                Log.d(TAG, "Lista vacía, mostrando empty state");
                showEmptyState();
            } else {
                if (recyclerAppointments != null) {
                    recyclerAppointments.setVisibility(View.VISIBLE);
                    Log.d(TAG, "✅ RecyclerView visible con " + appointments.size() + " items");
                }
                if (emptyView != null) {
                    emptyView.setVisibility(View.GONE);
                    Log.d(TAG, "✅ Empty view hidden");
                }
            }
        } catch (Exception e) {
            Log.w(TAG, "Error showing content state: " + e.getMessage());
        }
    }

    /**
     * Show empty state
     */
    private void showEmptyState() {
        Log.d(TAG, "=== MOSTRANDO ESTADO VACÍO ===");
        try {
            if (loadingView != null) {
                loadingView.setVisibility(View.GONE);
            }
            if (recyclerAppointments != null) {
                recyclerAppointments.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                Log.d(TAG, "✅ Empty view visible");
            }
            updateAppointmentCount();
        } catch (Exception e) {
            Log.w(TAG, "Error showing empty state: " + e.getMessage());
        }
    }

    /**
     * Show error state
     */
    private void showErrorState() {
        Log.d(TAG, "=== MOSTRANDO ESTADO DE ERROR ===");
        try {
            if (loadingView != null) {
                loadingView.setVisibility(View.GONE);
            }
            if (recyclerAppointments != null) {
                recyclerAppointments.setVisibility(View.GONE);
            }
            if (emptyView != null) {
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("Error al cargar las citas");
                Log.d(TAG, "✅ Error view visible");
            }
        } catch (Exception e) {
            Log.w(TAG, "Error showing error state: " + e.getMessage());
        }
    }

    /**
     * Show assign appointment dialog
     */
    private void showAssignAppointmentDialog(Appointment appointment) {
        try {
            AssignAppointmentDialog dialog = new AssignAppointmentDialog(this, appointment);
            dialog.show();
        } catch (Exception e) {
            Log.e(TAG, "Error showing assign dialog: " + e.getMessage(), e);
            showToast("Error al abrir el diálogo de asignación");
        }
    }

    /**
     * Show filter dialog (placeholder)
     */
    private void showFilterDialog() {
        showToast("Función de filtrado próximamente");
    }

    /**
     * Refresh appointments with user feedback
     */
    public void refreshAppointments() {
        showToast("Actualizando citas...");
        loadAppointments();
    }

    /**
     * Show toast message
     */
    private void showToast(String message) {
        try {
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.w(TAG, "Error showing toast: " + e.getMessage());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Comentar esto para evitar llamadas dobles durante desarrollo
        // refreshAppointments();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }
}

// Activity con debug completo by Assistant - Basado en código de Moris Navas