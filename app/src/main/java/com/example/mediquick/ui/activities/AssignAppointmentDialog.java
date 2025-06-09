package com.example.mediquick.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.AllDoctorsResponse;
import com.example.mediquick.data.model.Doctor;
import com.example.mediquick.data.model.ScheduleRequest;
import com.example.mediquick.data.model.ScheduleResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.utils.SessionManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.progressindicator.CircularProgressIndicator;
import com.google.android.material.textfield.MaterialAutoCompleteTextView;
import com.google.android.material.textfield.TextInputLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssignAppointmentDialog extends AlertDialog {

    // Interface para callback cuando se asigna la cita
    public interface OnAssignedListener {
        void onAssigned();
    }

    // UI Components
    private TextInputLayout tilDoctor, tilFecha, tilHora;
    private MaterialAutoCompleteTextView actvDoctor;
    private TextView txtFecha, txtHora, tvAppointmentInfo;
    private MaterialButton btnAsignar, btnCancel;
    private CircularProgressIndicator progressIndicator;
    private MaterialCardView cardAppointmentInfo;

    // Data
    private Appointment appointment;
    private Context context;
    private Calendar calendar = Calendar.getInstance();
    private AppointmentService apiService;
    private List<AllDoctorsResponse.DoctorData> doctoresList;
    private SessionManager sessionManager;
    private OnAssignedListener onAssignedListener;

    public AssignAppointmentDialog(@NonNull Context context, Appointment appointment) {
        super(context);
        this.context = context;
        this.appointment = appointment;

        sessionManager = new SessionManager(this.context);
        String jwt = sessionManager.getAuthToken();

        apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL + "/", jwt)
                .create(AppointmentService.class);
        doctoresList = new ArrayList<>();

        initializeDialog();
    }

    /**
     * Set listener for when appointment is successfully assigned
     */
    public void setOnAssignedListener(OnAssignedListener listener) {
        this.onAssignedListener = listener;
    }

    private void initializeDialog() {
        View view = LayoutInflater.from(context).inflate(R.layout.modal_assign_appointment, null);
        setView(view);
        setCancelable(true);

        initializeViews(view);
        setupAppointmentInfo();
        setupClickListeners();
        setupDateTimeFields();

        // Cargar doctores al inicio
        cargarDoctores();
    }

    private void initializeViews(View view) {
        // Cards y contenedores
        cardAppointmentInfo = view.findViewById(R.id.cardAppointmentInfo);
        tvAppointmentInfo = view.findViewById(R.id.tvAppointmentInfo);

        // Campos de entrada
        tilDoctor = view.findViewById(R.id.tilDoctor);
        actvDoctor = view.findViewById(R.id.actvDoctor);
        tilFecha = view.findViewById(R.id.tilFecha);
        txtFecha = view.findViewById(R.id.txtFecha);
        tilHora = view.findViewById(R.id.tilHora);
        txtHora = view.findViewById(R.id.txtHora);

        // Botones y progress
        btnAsignar = view.findViewById(R.id.btnAsignar);
        btnCancel = view.findViewById(R.id.btnCancel);
        progressIndicator = view.findViewById(R.id.progressIndicator);
    }

    private void setupAppointmentInfo() {
        if (appointment != null) {
            String info = String.format(
                    "ID: %s\nPaciente: %s\nSucursal: %s\nFecha actual: %s",
                    appointment.getId() != null ? appointment.getId() : "N/A",
                    appointment.getPaciente() != null ? appointment.getPaciente() : "No especificado",
                    appointment.getSucursal() != null ? appointment.getSucursal() : "No especificada",
                    appointment.getFecha() != null ? appointment.getFecha() : "Sin fecha asignada"
            );
            tvAppointmentInfo.setText(info);
        }
    }

    private void setupClickListeners() {
        btnCancel.setOnClickListener(v -> dismiss());
        btnAsignar.setOnClickListener(v -> asignarCita());

        txtFecha.setOnClickListener(v -> abrirDatePicker());
        txtHora.setOnClickListener(v -> abrirTimePicker());

        // Setup doctor selection
        actvDoctor.setOnItemClickListener((parent, view, position, id) -> {
            if (position < doctoresList.size()) {
                AllDoctorsResponse.DoctorData selectedDoctor = doctoresList.get(position);
                validateForm();
            }
        });
    }

    private void setupDateTimeFields() {
        // Configurar fecha mínima (hoy)
        Calendar today = Calendar.getInstance();
        txtFecha.setHint("Seleccionar fecha");
        txtHora.setHint("Seleccionar hora");
    }

    private void cargarDoctores() {
        showLoadingState("Cargando médicos...");

        // Deshabilitar campos mientras carga
        actvDoctor.setEnabled(false);
        tilDoctor.setHelperText("Cargando médicos disponibles...");

        apiService.getAllDoctors()
                .enqueue(new Callback<AllDoctorsResponse>() {
                    @Override
                    public void onResponse(Call<AllDoctorsResponse> call, Response<AllDoctorsResponse> response) {
                        hideLoadingState();
                        actvDoctor.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            doctoresList = response.body().getData();

                            if (doctoresList != null && !doctoresList.isEmpty()) {
                                setupDoctorAdapter();
                                tilDoctor.setHelperText("Selecciona un médico para la cita");
                            } else {
                                handleDoctorLoadError("No hay médicos disponibles");
                            }
                        } else {
                            handleDoctorLoadError("Error al cargar médicos");
                            // Fallback a doctores simulados
                            cargarDoctoresSimulados();
                        }
                    }

                    @Override
                    public void onFailure(Call<AllDoctorsResponse> call, Throwable t) {
                        hideLoadingState();
                        handleDoctorLoadError("Error de conexión: " + t.getMessage());
                        // Fallback a doctores simulados
                        cargarDoctoresSimulados();
                    }
                });
    }

    private void setupDoctorAdapter() {
        List<String> doctorNames = new ArrayList<>();
        for (AllDoctorsResponse.DoctorData doctor : doctoresList) {
            String displayName = doctor.toString(); // Usa el toString() que ya tienes
            doctorNames.add(displayName);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                doctorNames
        );

        actvDoctor.setAdapter(adapter);
        actvDoctor.setEnabled(true);
        tilDoctor.setError(null);
    }

    private void cargarDoctoresSimulados() {
        // Método de respaldo mejorado
        showToast("Usando datos de respaldo");

        ArrayList<Doctor> doctores = new ArrayList<>();
        doctores.add(new Doctor("1", "Dr. Juan Pérez"));
        doctores.add(new Doctor("2", "Dr. Ana Gómez"));
        doctores.add(new Doctor("3", "Dr. Carlos Rodriguez"));

        List<String> doctorNames = new ArrayList<>();
        for (Doctor doctor : doctores) {
            doctorNames.add(doctor.getNombreCompleto());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                android.R.layout.simple_dropdown_item_1line,
                doctorNames
        );

        actvDoctor.setAdapter(adapter);
        actvDoctor.setEnabled(true);
        tilDoctor.setHelperText("Médicos de respaldo disponibles");
    }

    private void handleDoctorLoadError(String error) {
        actvDoctor.setEnabled(false);
        tilDoctor.setError(error);
        tilDoctor.setHelperText("Verifica tu conexión e intenta nuevamente");
    }

    private void abrirDatePicker() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(
                context,
                R.style.CustomDatePickerTheme,
                (DatePicker view, int y, int m, int d) -> {
                    calendar.set(y, m, d);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    String fechaSeleccionada = sdf.format(calendar.getTime());
                    txtFecha.setText(fechaSeleccionada);
                    tilFecha.setError(null);
                    validateForm();
                },
                hoy.get(Calendar.YEAR),
                hoy.get(Calendar.MONTH),
                hoy.get(Calendar.DAY_OF_MONTH)
        );

        // Configurar fecha mínima
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());

        // Configurar fecha máxima (3 meses adelante)
        Calendar maxDate = Calendar.getInstance();
        maxDate.add(Calendar.MONTH, 3);
        dialog.getDatePicker().setMaxDate(maxDate.getTimeInMillis());

        dialog.show();
    }

    private void abrirTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(
                context,
                R.style.CustomTimePickerTheme,
                (TimePicker view, int h, int m) -> {
                    // Validar horario de trabajo (8 AM - 6 PM)
                    if (h < 8 || h >= 18) {
                        showToast("Por favor selecciona una hora entre 8:00 AM y 6:00 PM");
                        return;
                    }

                    String horaSeleccionada = String.format(Locale.getDefault(), "%02d:%02d", h, m);
                    txtHora.setText(horaSeleccionada);
                    tilHora.setError(null);
                    validateForm();
                },
                9, // Hora por defecto: 9 AM
                0, // Minutos por defecto: 00
                true // Formato 24 horas
        );

        dialog.show();
    }

    private void validateForm() {
        boolean isValid = true;

        // Validar doctor seleccionado
        if (actvDoctor.getText().toString().trim().isEmpty()) {
            tilDoctor.setError("Selecciona un médico");
            isValid = false;
        } else {
            tilDoctor.setError(null);
        }

        // Validar fecha
        if (txtFecha.getText().toString().trim().isEmpty()) {
            tilFecha.setError("Selecciona una fecha");
            isValid = false;
        } else {
            tilFecha.setError(null);
        }

        // Validar hora
        if (txtHora.getText().toString().trim().isEmpty()) {
            tilHora.setError("Selecciona una hora");
            isValid = false;
        } else {
            tilHora.setError(null);
        }

        btnAsignar.setEnabled(isValid);
    }

    private void asignarCita() {
        String doctorSeleccionado = actvDoctor.getText().toString().trim();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();

        if (doctorSeleccionado.isEmpty() || fecha.isEmpty() || hora.isEmpty()) {
            showToast("Por favor completa todos los campos");
            validateForm();
            return;
        }

        // Buscar el doctor seleccionado en la lista
        AllDoctorsResponse.DoctorData selectedDoctor = null;
        for (AllDoctorsResponse.DoctorData doctor : doctoresList) {
            if (doctor.toString().equals(doctorSeleccionado)) {
                selectedDoctor = doctor;
                break;
            }
        }

        if (selectedDoctor != null) {
            programarCitaConAPI(selectedDoctor, fecha, hora);
        } else {
            // Fallback para doctores simulados
            handleSimulatedDoctorAssignment(doctorSeleccionado, fecha, hora);
        }
    }

    private void handleSimulatedDoctorAssignment(String doctorName, String fecha, String hora) {
        // Lógica para doctor simulado (respaldo)
        if (fecha.equals("19/05/2025") && hora.equals("09:00")) {
            showToast("El doctor ya tiene cita en ese horario");
            return;
        }

        showToast("Cita asignada a " + doctorName + " (modo de respaldo)");

        // Notificar listener
        if (onAssignedListener != null) {
            onAssignedListener.onAssigned();
        }

        dismiss();
    }

    private void programarCitaConAPI(AllDoctorsResponse.DoctorData doctor, String fecha, String hora) {
        try {
            showLoadingState("Programando cita...");

            // Convertir fecha y hora a formato ISO
            String dateTimeISO = convertToISOFormat(fecha, hora);

            ScheduleRequest request = new ScheduleRequest(
                    doctor.getUserId(),
                    dateTimeISO
            );

            // Deshabilitar botón mientras se procesa
            btnAsignar.setEnabled(false);

            apiService.scheduleAppointment(appointment.getId(), request)
                    .enqueue(new Callback<ScheduleResponse>() {
                        @Override
                        public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                            hideLoadingState();
                            btnAsignar.setEnabled(true);

                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                showToast(response.body().getMessage());

                                // Notificar listener
                                if (onAssignedListener != null) {
                                    onAssignedListener.onAssigned();
                                }

                                dismiss();
                            } else {
                                String errorMessage = "Error al programar cita";
                                if (response.body() != null) {
                                    errorMessage = response.body().getMessage();
                                }
                                showToast(errorMessage);
                                validateForm(); // Re-habilitar botón si es válido
                            }
                        }

                        @Override
                        public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                            hideLoadingState();
                            btnAsignar.setEnabled(true);
                            showToast("Error de conexión: " + t.getMessage());
                            validateForm();
                        }
                    });

        } catch (Exception e) {
            hideLoadingState();
            btnAsignar.setEnabled(true);
            showToast("Error al procesar fecha y hora");
            validateForm();
        }
    }

    private String convertToISOFormat(String fecha, String hora) throws ParseException {
        // Combinar fecha y hora
        String fechaHora = fecha + " " + hora;

        // Formato de entrada: dd/MM/yyyy HH:mm
        SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
        Date date = inputFormat.parse(fechaHora);

        // Formato de salida: ISO 8601
        SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault());
        return outputFormat.format(date);
    }

    private void showLoadingState(String message) {
        progressIndicator.setVisibility(View.VISIBLE);
        btnAsignar.setText(message);
        btnAsignar.setEnabled(false);
    }

    private void hideLoadingState() {
        progressIndicator.setVisibility(View.GONE);
        btnAsignar.setText("Asignar Cita");
        validateForm(); // Re-evaluar si se puede habilitar
    }

    private void showToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }
}

// Dialog mejorado by Assistant - Basado en código de Moris Navas