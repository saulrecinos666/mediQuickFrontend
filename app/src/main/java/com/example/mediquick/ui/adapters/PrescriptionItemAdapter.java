package com.example.mediquick.ui.adapters;

import android.util.Log;
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
    private static final String TAG = "PrescriptionAdapter";

    public PrescriptionItemAdapter(List<PrescriptionItem> items) {
        this.items = items;
        Log.d(TAG, "✅ Constructor: Recibidos " + (items != null ? items.size() : 0) + " elementos");

        // Debug cada elemento
        if (items != null) {
            for (int i = 0; i < items.size(); i++) {
                Log.d(TAG, "  Item " + i + ": " + items.get(i).getMedicationName());
            }
        }
    }

    @NonNull
    @Override
    public PrescriptionItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Log.d(TAG, "✅ onCreateViewHolder() llamado");
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_prescription, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionItemAdapter.ViewHolder holder, int position) {
        Log.d(TAG, "✅ onBindViewHolder() - posición: " + position + " de " + getItemCount());

        if (position < items.size()) {
            PrescriptionItem i = items.get(position);
            Log.d(TAG, "  Vinculando: " + i.getMedicationName());

            holder.txtMedicamento.setText("💊 " + i.getMedicationName());
            holder.txtDosis.setText(i.getDosage());
            holder.txtFrecuencia.setText(i.getFrequency());
            holder.txtDuracion.setText(i.getDuration());
            holder.txtNotas.setText(i.getNotes().isEmpty() ? "Sin notas adicionales" : i.getNotes());

            Log.d(TAG, "  ✅ Item vinculado correctamente");
        } else {
            Log.e(TAG, "❌ ERROR: posición " + position + " fuera de rango");
        }
    }

    @Override
    public int getItemCount() {
        int count = items != null ? items.size() : 0;
        Log.d(TAG, "✅ getItemCount() retorna: " + count);
        return count;
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

            Log.d("ViewHolder", "✅ ViewHolder creado");
        }
    }
}