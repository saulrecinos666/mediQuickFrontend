package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.PrescriptionItem;

import java.util.List;

public class PrescriptionItemAdapter extends RecyclerView.Adapter<PrescriptionItemAdapter.ViewHolder> {

    private final List<PrescriptionItem> items;

    public PrescriptionItemAdapter(List<PrescriptionItem> items) {
        this.items = items;
    }

    @NonNull
    @Override
    public PrescriptionItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionItemAdapter.ViewHolder holder, int position) {
        PrescriptionItem i = items.get(position);
        holder.txtMedicamento.setText("💊 " + i.getMedicationName());
        holder.txtDosis.setText("Dosis: " + i.getDosage());
        holder.txtFrecuencia.setText("Frecuencia: " + i.getFrequency());
        holder.txtDuracion.setText("Duración: " + i.getDuration());
        holder.txtNotas.setText("Notas: " + (i.getNotes().isEmpty() ? "N/A" : i.getNotes()));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtMedicamento, txtDosis, txtFrecuencia, txtDuracion, txtNotas;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtMedicamento = itemView.findViewById(R.id.txtMedicamento);
            txtDosis = itemView.findViewById(R.id.txtDosis);
            txtFrecuencia = itemView.findViewById(R.id.txtFrecuencia);
            txtDuracion = itemView.findViewById(R.id.txtDuracion);
            txtNotas = itemView.findViewById(R.id.txtNotas);
        }
    }
}
