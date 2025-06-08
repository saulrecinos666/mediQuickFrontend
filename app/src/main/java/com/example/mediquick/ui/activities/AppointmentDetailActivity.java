package com.example.mediquick.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.*;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.ui.adapters.PrescriptionItemAdapter;
import com.example.mediquick.data.model.PrescriptionDetail;
import com.example.mediquick.data.model.PrescriptionItem;

import java.util.Arrays;

public class AppointmentDetailActivity extends AppCompatActivity {

    private TextView txtProcedimiento, txtFecha, txtDoctor, txtSucursal, txtNotas, txtSinReceta;
    private RecyclerView recyclerMedicamentos;
    private LinearLayout layoutReceta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointment_detail);

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        txtProcedimiento = findViewById(R.id.txtProcedimiento);
        txtFecha = findViewById(R.id.txtFechaDetalle);
        txtDoctor = findViewById(R.id.txtDoctorDetalle);
        txtSucursal = findViewById(R.id.txtSucursalDetalle);
        txtNotas = findViewById(R.id.txtNotasGenerales);
        txtSinReceta = findViewById(R.id.txtSinReceta);
        recyclerMedicamentos = findViewById(R.id.recyclerMedicamentos);
        layoutReceta = findViewById(R.id.layoutReceta);

        String appointmentId = getIntent().getStringExtra("appointmentId");

        PrescriptionDetail receta;

        if ("1".equals(appointmentId)) {
            receta = new PrescriptionDetail(
                    appointmentId,
                    "Consulta General",
                    "06/Jun/2025 - 12:45 PM",
                    "Dr. Roberto Martínez Hernández",
                    "Clínica Central San Salvador",
                    "Tomar con alimentos",
                    Arrays.asList(
                            new PrescriptionItem("Paracetamol", "500mg", "Cada 8h", "5 días", "Tomar con agua")
                    )
            );
        } else if ("2".equals(appointmentId)) {
            receta = new PrescriptionDetail(
                    appointmentId,
                    "Pediatría",
                    "01/Jun/2025 - 08:30 AM",
                    "Dr. Ana López",
                    "Clínica Norte Soyapango",
                    "Control de fiebre",
                    Arrays.asList(
                            new PrescriptionItem("Amoxicilina", "250mg", "Cada 8h", "7 días", "")
                    )
            );
        } else {
            receta = new PrescriptionDetail(
                    appointmentId,
                    "Sin detalles",
                    "Fecha no disponible",
                    "Sin doctor asignado",
                    "Sucursal no especificada",
                    "Sin receta médica",
                    Arrays.asList()
            );
        }

        txtProcedimiento.setText(receta.getProcedure());
        txtFecha.setText(receta.getDateTime());
        txtDoctor.setText(receta.getDoctor());
        txtSucursal.setText(receta.getBranch());

        if (receta.getItems().isEmpty()) {
            layoutReceta.setVisibility(View.GONE);
            txtSinReceta.setVisibility(View.VISIBLE);
        } else {
            layoutReceta.setVisibility(View.VISIBLE);
            txtSinReceta.setVisibility(View.GONE);
            txtNotas.setText(receta.getPrescriptionNotes());

            recyclerMedicamentos.setLayoutManager(new LinearLayoutManager(this));
            recyclerMedicamentos.setAdapter(new PrescriptionItemAdapter(receta.getItems()));
        }
    }
}
