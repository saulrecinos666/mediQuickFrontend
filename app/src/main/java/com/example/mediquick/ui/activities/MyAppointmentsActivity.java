package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.ui.adapters.AppointmentAdapter;
import com.example.mediquick.data.model.AppointmentSummary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MyAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private TextView txtEmpty;
    private ProgressBar progressBar;
    private List<AppointmentSummary> citas = new ArrayList<>();
    private AppointmentAdapter adapter;
    private String userId = "12345"; // Simula sesión

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_appointments);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        recycler = findViewById(R.id.recyclerCitasPaciente);
        txtEmpty = findViewById(R.id.emptyCitas);
        progressBar = findViewById(R.id.progressBarPaciente);

        adapter = new AppointmentAdapter(citas, cita -> {
            Intent intent = new Intent(this, AppointmentDetailActivity.class);
            intent.putExtra("appointmentId", cita.getAppointmentId());
            Toast.makeText(this, "Enviando ID: " + cita.getAppointmentId(), Toast.LENGTH_SHORT).show();
            startActivity(intent);
        });

        recycler.setAdapter(adapter);
        recycler.setLayoutManager(new LinearLayoutManager(this));

        cargarCitas();
    }

    private void cargarCitas() {
        progressBar.setVisibility(View.VISIBLE);

        // Simulación de citas
        citas.clear();
        citas.add(new AppointmentSummary("1", formatFecha("2025-06-06T12:45:00"), "Created", "Dr. Roberto Martínez Hernández", "Clínica Central San Salvador (MINSAL)"));
        citas.add(new AppointmentSummary("2", formatFecha("2025-06-01T08:30:00"), "Completed", "Dr. Ana López", "Clínica Norte Soyapango"));
        citas.add(new AppointmentSummary("3", formatFecha("2025-05-30T10:00:00"), "Canceled", "Sin doctor asignado", "Clínica Mejicanos"));

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        txtEmpty.setVisibility(citas.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String formatFecha(String isoDate) {
        try {
            SimpleDateFormat input = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault());
            SimpleDateFormat output = new SimpleDateFormat("dd/MMM/yyyy - hh:mm a", Locale.getDefault());
            Date date = input.parse(isoDate);
            return output.format(date);
        } catch (Exception e) {
            return isoDate;
        }
    }
}
