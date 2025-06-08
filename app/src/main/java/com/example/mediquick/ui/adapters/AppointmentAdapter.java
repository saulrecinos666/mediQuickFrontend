package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentSummary;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnClick {
        void onSelect(AppointmentSummary item);
    }

    private final List<AppointmentSummary> citas;
    private final OnClick listener;

    public AppointmentAdapter(List<AppointmentSummary> citas, OnClick listener) {
        this.citas = citas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita_paciente, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentSummary c = citas.get(position);
        holder.txtFecha.setText("📅 " + c.getDateTime());
        holder.txtEstado.setText("🔄 " + c.getStateDescription());
        holder.txtEstado.setTextColor(getColorByEstado(c.getStateDescription()));
        holder.txtDoctor.setText("👨‍⚕ " + c.getDoctorName());
        holder.txtSucursal.setText("🏥 " + c.getBranchName());

        holder.itemView.setOnClickListener(v -> listener.onSelect(c));
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtFecha, txtEstado, txtDoctor, txtSucursal;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtEstado = itemView.findViewById(R.id.txtEstado);
            txtDoctor = itemView.findViewById(R.id.txtDoctor);
            txtSucursal = itemView.findViewById(R.id.txtSucursal);
        }
    }

    private int getColorByEstado(String estado) {
        if (estado.equalsIgnoreCase("Created")) return 0xFFFFC107; // Amarillo
        if (estado.equalsIgnoreCase("Completed")) return 0xFF4CAF50; // Verde
        if (estado.equalsIgnoreCase("Canceled")) return 0xFFF44336; // Rojo
        return 0xFF888888;
    }
}
