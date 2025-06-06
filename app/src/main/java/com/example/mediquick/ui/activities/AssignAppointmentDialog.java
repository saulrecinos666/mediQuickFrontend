package com.example.mediquick.ui.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.mediquick.BuildConfig;
import com.example.mediquick.R;
import com.example.mediquick.data.api.ApiClient;
import com.example.mediquick.data.model.AllDoctorsResponse;
import com.example.mediquick.data.model.ScheduleRequest;
import com.example.mediquick.data.model.ScheduleResponse;
import com.example.mediquick.services.AppointmentService;
import com.example.mediquick.utils.SessionManager;

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

    private Spinner spinnerDoctor;
    private TextView txtFecha, txtHora;
    private Button btnAsignar;
    private Appointment appointment;
    private Context context;
    private Calendar calendar = Calendar.getInstance();
    private AppointmentService apiService;
    private List<AllDoctorsResponse.DoctorData> doctoresList;
    private SessionManager sessionManager;
    public AssignAppointmentDialog(@NonNull Context context, Appointment appointment) {
        super(context);
        this.context = context;
        this.appointment = appointment;

        sessionManager = new SessionManager(this.context);

        String jwt = sessionManager.getAuthToken();

        apiService = ApiClient.getAuthenticatedClient(BuildConfig.BACKEND_BASE_URL + "/", jwt).create(AppointmentService.class);
        doctoresList = new ArrayList<>();

        View view = LayoutInflater.from(context).inflate(R.layout.modal_assign_appointment, null);
        setView(view);
        setCancelable(true);

        spinnerDoctor = view.findViewById(R.id.spinnerDoctor);
        txtFecha = view.findViewById(R.id.txtFecha);
        txtHora = view.findViewById(R.id.txtHora);
        btnAsignar = view.findViewById(R.id.btnAsignar);

        cargarDoctores(); // Cargar desde API

        txtFecha.setOnClickListener(v -> abrirDatePicker());
        txtHora.setOnClickListener(v -> abrirTimePicker());

        btnAsignar.setOnClickListener(v -> asignarCita());
    }

    private void cargarDoctores() {
        // Mostrar loading o deshabilitar spinner mientras carga
        spinnerDoctor.setEnabled(false);

        apiService.getAllDoctors()
                .enqueue(new Callback<AllDoctorsResponse>() {
                    @Override
                    public void onResponse(Call<AllDoctorsResponse> call, Response<AllDoctorsResponse> response) {
                        spinnerDoctor.setEnabled(true);

                        if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                            doctoresList = response.body().getData();

                            if (doctoresList != null && !doctoresList.isEmpty()) {
                                ArrayAdapter<AllDoctorsResponse.DoctorData> adapter = new ArrayAdapter<>(
                                        context,
                                        android.R.layout.simple_spinner_item,
                                        doctoresList
                                );
                                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                spinnerDoctor.setAdapter(adapter);
                            } else {
                                Toast.makeText(context, "No hay doctores disponibles", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(context, "Error al cargar doctores", Toast.LENGTH_SHORT).show();
                            // Fallback a doctores simulados si falla la API
                            cargarDoctoresSimulados();
                        }
                    }

                    @Override
                    public void onFailure(Call<AllDoctorsResponse> call, Throwable t) {
                        spinnerDoctor.setEnabled(true);
                        Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        // Fallback a doctores simulados si falla la conexión
                        cargarDoctoresSimulados();
                    }
                });
    }

    private void cargarDoctoresSimulados() {
        // Método de respaldo en caso de error en la API
        ArrayList<Doctor> doctores = new ArrayList<>();
        doctores.add(new Doctor("1", "Dr. Juan Pérez"));
        doctores.add(new Doctor("2", "Dr. Ana Gómez"));

        ArrayAdapter<Doctor> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, doctores);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerDoctor.setAdapter(adapter);
    }

    private void abrirDatePicker() {
        Calendar hoy = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(context, (DatePicker view, int y, int m, int d) -> {
            calendar.set(y, m, d);
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            txtFecha.setText(sdf.format(calendar.getTime()));
        }, hoy.get(Calendar.YEAR), hoy.get(Calendar.MONTH), hoy.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMinDate(System.currentTimeMillis());
        dialog.show();
    }

    private void abrirTimePicker() {
        TimePickerDialog dialog = new TimePickerDialog(context, (TimePicker view, int h, int m) -> {
            txtHora.setText(String.format(Locale.getDefault(), "%02d:%02d", h, m));
        }, 9, 0, true);
        dialog.show();
    }

    private void asignarCita() {
        Object seleccionado = spinnerDoctor.getSelectedItem();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();

        if (seleccionado == null) {
            Toast.makeText(context, "Selecciona un doctor", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(context, "Completa fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        // Verificar si es doctor de API o simulado
        if (seleccionado instanceof AllDoctorsResponse.DoctorData) {
            programarCitaConAPI((AllDoctorsResponse.DoctorData) seleccionado, fecha, hora);
        } else if (seleccionado instanceof Doctor) {
            // Lógica para doctor simulado (fallback)
            Doctor doctor = (Doctor) seleccionado;
            if (fecha.equals("19/05/2025") && hora.equals("09:00")) {
                Toast.makeText(context, "El doctor ya tiene cita en ese horario", Toast.LENGTH_SHORT).show();
                return;
            }
            Toast.makeText(context, "Cita asignada a " + doctor.getNombreCompleto(), Toast.LENGTH_LONG).show();
            dismiss();
        }
    }

    private void programarCitaConAPI(AllDoctorsResponse.DoctorData doctor, String fecha, String hora) {
        try {
            // Convertir fecha y hora a formato ISO
            String dateTimeISO = convertToISOFormat(fecha, hora);

            ScheduleRequest request = new ScheduleRequest(
                    doctor.getUserId(),
                    dateTimeISO
            );

            // Deshabilitar botón mientras se procesa
            btnAsignar.setEnabled(false);
            btnAsignar.setText("Programando...");

            apiService.scheduleAppointment(appointment.getId(), request)
                    .enqueue(new Callback<ScheduleResponse>() {
                        @Override
                        public void onResponse(Call<ScheduleResponse> call, Response<ScheduleResponse> response) {
                            btnAsignar.setEnabled(true);
                            btnAsignar.setText("Asignar");

                            if (response.isSuccessful() && response.body() != null && response.body().isSuccess()) {
                                Toast.makeText(context, response.body().getMessage(), Toast.LENGTH_LONG).show();
                                //TODO: Al hacer la programacion de la cita recargar la lista de consultas pendientes
                                dismiss();
                            } else {
                                String errorMessage = "Error al programar cita";
                                if (response.body() != null) {
                                    errorMessage = response.body().getMessage();
                                }
                                Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<ScheduleResponse> call, Throwable t) {
                            btnAsignar.setEnabled(true);
                            btnAsignar.setText("Asignar");
                            Toast.makeText(context, "Error de conexión: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

        } catch (Exception e) {
            btnAsignar.setEnabled(true);
            btnAsignar.setText("Asignar");
            Toast.makeText(context, "Error al procesar fecha y hora", Toast.LENGTH_SHORT).show();
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
}