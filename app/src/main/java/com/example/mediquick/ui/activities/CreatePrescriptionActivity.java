package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.PrescriptionForm;
import com.example.mediquick.ui.adapters.PrescriptionFormAdapter;

import java.util.ArrayList;
import java.util.List;

public class CreatePrescriptionActivity extends AppCompatActivity {

    private RecyclerView recyclerForm;
    private PrescriptionFormAdapter formAdapter;
    private List<PrescriptionForm> medicamentos = new ArrayList<>();
    private EditText edtNotas;
    private Button btnGuardar, btnAgregar;
    private TextView txtPaciente;
    private String appointmentId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_prescription);

        recyclerForm = findViewById(R.id.recyclerForm);
        edtNotas = findViewById(R.id.edtNotas);
        btnGuardar = findViewById(R.id.btnGuardar);
        btnAgregar = findViewById(R.id.btnAgregar);
        txtPaciente = findViewById(R.id.txtPacienteHeader);

        appointmentId = getIntent().getStringExtra("appointmentId");
        String nombrePaciente = getIntent().getStringExtra("paciente");
        txtPaciente.setText("Receta para: " + nombrePaciente);

        medicamentos.add(new PrescriptionForm());
        formAdapter = new PrescriptionFormAdapter(medicamentos);
        recyclerForm.setAdapter(formAdapter);
        recyclerForm.setLayoutManager(new LinearLayoutManager(this));

        btnAgregar.setOnClickListener(v -> {
            medicamentos.add(new PrescriptionForm());
            formAdapter.notifyItemInserted(medicamentos.size() - 1);
        });

        btnGuardar.setOnClickListener(v -> guardarReceta());
    }

    private void guardarReceta() {
        for (PrescriptionForm m : medicamentos) {
            if (m.getNombre().isEmpty() || m.getDosis().isEmpty() || m.getFrecuencia().isEmpty() || m.getDuracion().isEmpty()) {
                Toast.makeText(this, "Todos los campos obligatorios deben completarse", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        Toast.makeText(this, "Receta guardada", Toast.LENGTH_LONG).show();
        finish();
    }
}
