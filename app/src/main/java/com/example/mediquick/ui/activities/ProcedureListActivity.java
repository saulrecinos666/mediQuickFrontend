package com.example.mediquick.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.ui.adapters.ProcedureAdapter;
import com.example.mediquick.data.model.MedicalProcedure;

import java.util.ArrayList;
import java.util.List;

public class ProcedureListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtHeader, emptyState;
    private ProgressBar progressBar;
    private List<MedicalProcedure> procedures = new ArrayList<>();
    private ProcedureAdapter adapter;
    private String branchId, branchName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_procedure_list);

        recyclerView = findViewById(R.id.recyclerProcedimientos);
        txtHeader = findViewById(R.id.txtNombreSucursal);
        emptyState = findViewById(R.id.emptyViewProcedimientos);
        progressBar = findViewById(R.id.progressBarProcedimientos);

        branchId = getIntent().getStringExtra("branchId");
        branchName = getIntent().getStringExtra("branchName");
        txtHeader.setText("Procedimientos en: " + branchName);

        adapter = new ProcedureAdapter(procedures, selected -> {
            // Al seleccionar un procedimiento, preguntar si está seguro de agendar la cita
            Intent intent = new Intent(this, ConfirmAppointmentActivity.class);
            intent.putExtra("procedureId", selected.getProcedureId());
            intent.putExtra("procedureName", selected.getProcedureName());
            intent.putExtra("branchId", branchId);
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarProcedimientos();
    }

    private void cargarProcedimientos() {
        progressBar.setVisibility(View.VISIBLE);

        // Simulación de respuesta
        procedures.clear();

        procedures.add(new MedicalProcedure() {{
            setProcedureId("1");
            setProcedureName("Consulta General");
            setProcedureDuration("30 min");
            setProcedureCost(50.0);
            setAvailableOnline(true);
            setSpecialty("General");
        }});

        procedures.add(new MedicalProcedure() {{
            setProcedureId("2");
            setProcedureName("Radiografía de Tórax");
            setProcedureDuration("15 min");
            setProcedureCost(80.0);
            setAvailableOnline(false);
            setSpecialty("Radiología");
        }});

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(procedures.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
