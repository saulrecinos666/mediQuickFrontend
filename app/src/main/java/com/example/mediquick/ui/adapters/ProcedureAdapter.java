package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.MedicalProcedure;

import java.util.List;

public class ProcedureAdapter extends RecyclerView.Adapter<ProcedureAdapter.ViewHolder> {

    public interface OnProcedureClick {
        void onSelect(MedicalProcedure procedure);
    }

    private final List<MedicalProcedure> procedures;
    private final OnProcedureClick listener;

    public ProcedureAdapter(List<MedicalProcedure> procedures, OnProcedureClick listener) {
        this.procedures = procedures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProcedureAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_procedure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProcedureAdapter.ViewHolder holder, int position) {
        MedicalProcedure procedure = procedures.get(position);
        holder.txtNombre.setText("🩺 " + procedure.getProcedureName());
        holder.txtDuracion.setText("⏱ " + procedure.getProcedureDuration());
        holder.txtCosto.setText("💲 " + procedure.getProcedureCost());
        holder.txtEspecialidad.setText("📌 " + procedure.getSpecialty());

        holder.itemView.setOnClickListener(v -> listener.onSelect(procedure));
    }

    @Override
    public int getItemCount() {
        return procedures.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDuracion, txtCosto, txtEspecialidad;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreProcedimiento);
            txtDuracion = itemView.findViewById(R.id.txtDuracionProcedimiento);
            txtCosto = itemView.findViewById(R.id.txtCostoProcedimiento);
            txtEspecialidad = itemView.findViewById(R.id.txtEspecialidadProcedimiento);
        }
    }
}
