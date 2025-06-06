package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.Institution;

import java.util.List;

public class InstitutionAdapter extends RecyclerView.Adapter<InstitutionAdapter.ViewHolder> {

    public interface OnInstitutionClick {
        void onSelect(Institution institution);
    }

    private final List<Institution> institutions;
    private final OnInstitutionClick listener;

    public InstitutionAdapter(List<Institution> institutions, OnInstitutionClick listener) {
        this.institutions = institutions;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_institution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Institution inst = institutions.get(position);
        holder.txtNombre.setText("🏛 " + inst.getInstitutionName());
        holder.txtDescripcion.setText("📌 " + inst.getInstitutionDescription());
        holder.txtEstado.setText(inst.isInstitutionStatus() ? "✅ Activo" : "❌ Inactivo");

        holder.itemView.setOnClickListener(v -> listener.onSelect(inst));
    }

    @Override
    public int getItemCount() {
        return institutions.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDescripcion, txtEstado;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreInstitucion);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionInstitucion);
            txtEstado = itemView.findViewById(R.id.txtEstadoInstitucion);
        }
    }
}
