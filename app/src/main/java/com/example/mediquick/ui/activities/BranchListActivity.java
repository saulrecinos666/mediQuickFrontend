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
import com.example.mediquick.ui.adapters.BranchAdapter;
import com.example.mediquick.data.model.Branch;

import java.util.ArrayList;
import java.util.List;

public class BranchListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView txtHeader, emptyState;
    private ProgressBar progressBar;
    private List<Branch> branches = new ArrayList<>();
    private BranchAdapter adapter;
    private String institutionId, institutionName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_branch_list);

        recyclerView = findViewById(R.id.recyclerSucursales);
        txtHeader = findViewById(R.id.txtNombreInstitucion);
        emptyState = findViewById(R.id.emptyViewSucursales);
        progressBar = findViewById(R.id.progressBarSucursales);

        institutionId = getIntent().getStringExtra("institutionId");
        institutionName = getIntent().getStringExtra("institutionName");
        txtHeader.setText("Sucursales de: " + institutionName);

        adapter = new BranchAdapter(branches, selected -> {
            Intent intent = new Intent(this, ProcedureListActivity.class);
            intent.putExtra("branchId", selected.getBranchId());
            intent.putExtra("branchName", selected.getBranchName());
            startActivity(intent);
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        cargarSucursales();
    }

    private void cargarSucursales() {
        progressBar.setVisibility(View.VISIBLE);
        branches.clear();

        // Simulación
        branches.add(new Branch() {{
            setBranchId("1");
            setBranchName("Sucursal Centro");
            setBranchFullAddress("Av. Central #123");
            setBranchDescription("Sucursal principal en zona centro");
        }});

        branches.add(new Branch() {{
            setBranchId("2");
            setBranchName("Sucursal Norte");
            setBranchFullAddress("Col. La Esperanza, Calle Ficticia");
            setBranchDescription("Atención general y odontológica");
        }});

        adapter.notifyDataSetChanged();
        progressBar.setVisibility(View.GONE);
        emptyState.setVisibility(branches.isEmpty() ? View.VISIBLE : View.GONE);
    }
}
