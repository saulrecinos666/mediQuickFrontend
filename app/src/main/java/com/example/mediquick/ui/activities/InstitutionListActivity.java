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
import com.example.mediquick.ui.adapters.InstitutionAdapter;
import com.example.mediquick.data.model.Institution;

import java.util.ArrayList;
import java.util.List;

public class InstitutionListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView emptyState;
    private ProgressBar progressBar;
    private InstitutionAdapter adapter;
    private List<Institution> lista = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_institution_list);

        recyclerView = findViewById(R.id.recyclerInstituciones);
        emptyState = findViewById(R.id.emptyView);
        progressBar = findViewById(R.id.progressBarInstituciones);

        adapter = new InstitutionAdapter(lista, selected -> {
            Intent intent = new Intent(this, BranchListActivity.class);
            intent.putExtra("institutionId", selected.getInstitutionId());
            intent.putExtra("institutionName", selected.getInstitutionName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarInstituciones();
    }

    private void cargarInstituciones() {
        progressBar.setVisibility(View.VISIBLE);

        // Simulación de respuesta
        lista.clear();

        lista.add(new Institution() {{
            setInstitutionId("1");
            setInstitutionName("Hospital Central");
            setInstitutionDescription("Atención especializada y general.");
            setInstitutionStatus(true);
        }});

        lista.add(new Institution() {{
            setInstitutionId("2");
            setInstitutionName("Clínica Regional");
            setInstitutionDescription("Consultas y exámenes básicos.");
            setInstitutionStatus(false);
        }});

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(lista.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
