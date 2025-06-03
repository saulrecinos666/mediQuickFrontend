package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentCard;
import com.example.mediquick.ui.adapters.PrescriptionAdapter;

import java.util.ArrayList;
import java.util.List;

public class AssignedAppointmentsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private PrescriptionAdapter adapter;
    private List<AppointmentCard> citasAsignadas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_assigned_appointments);

        recyclerView = findViewById(R.id.recyclerCitas);
        citasAsignadas = new ArrayList<>();

        // Simulación
        citasAsignadas.add(new AppointmentCard("0980dee1-6478...", "Luis Hernández Díaz", "12/04/2025 - 15:30", "Clínica Central", "Aceptada"));

        adapter = new PrescriptionAdapter(citasAsignadas, cita -> {
            Intent i = new Intent(this, CreatePrescriptionActivity.class);
            i.putExtra("appointmentId", cita.getId());
            i.putExtra("paciente", cita.getPaciente());
            startActivity(i);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
    }
}
