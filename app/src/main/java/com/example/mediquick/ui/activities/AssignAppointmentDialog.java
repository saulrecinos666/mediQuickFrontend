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

import com.example.mediquick.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class AssignAppointmentDialog extends AlertDialog {

    private Spinner spinnerDoctor;
    private TextView txtFecha, txtHora;
    private Button btnAsignar;
    private Appointment appointment;
    private Context context;
    private Calendar calendar = Calendar.getInstance();

    public AssignAppointmentDialog(@NonNull Context context, Appointment appointment) {
        super(context);
        this.context = context;
        this.appointment = appointment;

        View view = LayoutInflater.from(context).inflate(R.layout.modal_assign_appointment, null);
        setView(view);
        setCancelable(true);

        spinnerDoctor = view.findViewById(R.id.spinnerDoctor);
        txtFecha = view.findViewById(R.id.txtFecha);
        txtHora = view.findViewById(R.id.txtHora);
        btnAsignar = view.findViewById(R.id.btnAsignar);

        cargarDoctoresSimulados(); //API

        txtFecha.setOnClickListener(v -> abrirDatePicker());
        txtHora.setOnClickListener(v -> abrirTimePicker());

        btnAsignar.setOnClickListener(v -> asignarCita());
    }

    private void cargarDoctoresSimulados() {
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
        Doctor seleccionado = (Doctor) spinnerDoctor.getSelectedItem();
        String fecha = txtFecha.getText().toString().trim();
        String hora = txtHora.getText().toString().trim();

        if (fecha.isEmpty() || hora.isEmpty()) {
            Toast.makeText(context, "Completa fecha y hora", Toast.LENGTH_SHORT).show();
            return;
        }

        if (fecha.equals("19/05/2025") && hora.equals("09:00")) {
            Toast.makeText(context, "El doctor ya tiene cita en ese horario", Toast.LENGTH_SHORT).show();
            return;
        }

        Toast.makeText(context, "Cita asignada a " + seleccionado.getNombreCompleto(), Toast.LENGTH_LONG).show();
        dismiss();
    }
}
// <!--Moris Navas-->