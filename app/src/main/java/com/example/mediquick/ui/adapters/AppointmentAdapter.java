package com.example.mediquick.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

        // Configurar fecha - buscar la view correcta
        if (holder.txtFechaCompleta != null) {
            holder.txtFechaCompleta.setText(c.getDateTime());
        }

        // Configurar estado - puede ser Chip o TextView
        if (holder.chipEstado != null) {
            holder.chipEstado.setText(c.getStateDescription());
            holder.chipEstado.setBackgroundColor(getColorByEstado(c.getStateDescription()));
        }

        // Configurar doctor y sucursal
        if (holder.txtDoctor != null) {
            holder.txtDoctor.setText(c.getDoctorName());
        }
        if (holder.txtSucursal != null) {
            holder.txtSucursal.setText(c.getBranchName());
        }

        // Configurar indicador de estado
        if (holder.statusIndicator != null) {
            holder.statusIndicator.setBackgroundColor(getColorByEstado(c.getStateDescription()));
        }

        // Configurar botón
        if (holder.btnAccion != null) {
            setupActionButton(holder.btnAccion, c.getStateDescription());
            holder.btnAccion.setOnClickListener(v -> listener.onSelect(c));
        }

        // Click listener principal
        holder.itemView.setOnClickListener(v -> listener.onSelect(c));
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        View statusIndicator;
        TextView chipEstado, txtFechaCompleta, txtDoctor, txtSucursal;
        Button btnAccion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            // Buscar las views que existan en el layout
            statusIndicator = itemView.findViewById(R.id.statusIndicator);
            chipEstado = itemView.findViewById(R.id.chipEstado);
            txtFechaCompleta = itemView.findViewById(R.id.txtFechaCompleta);
            txtDoctor = itemView.findViewById(R.id.txtDoctor);
            txtSucursal = itemView.findViewById(R.id.txtSucursal);
            btnAccion = itemView.findViewById(R.id.btnAccion);

            // Si no encuentra txtFechaCompleta, buscar otras alternativas
            if (txtFechaCompleta == null) {
                txtFechaCompleta = itemView.findViewById(R.id.txtFecha);
            }
        }
    }

    private int getColorByEstado(String estado) {
        switch (estado.toLowerCase()) {
            case "created":
            case "pendiente":
                return Color.parseColor("#FF9800"); // Naranja
            case "completed":
            case "completada":
                return Color.parseColor("#4CAF50"); // Verde
            case "canceled":
            case "cancelada":
                return Color.parseColor("#F44336"); // Rojo
            case "confirmed":
            case "confirmada":
                return Color.parseColor("#2196F3"); // Azul
            default:
                return Color.parseColor("#9E9E9E"); // Gris
        }
    }

    private void setupActionButton(Button button, String status) {
        switch (status.toLowerCase()) {
            case "created":
            case "pendiente":
                button.setText("Ver");
                break;
            case "completed":
            case "completada":
                button.setText("Detalles");
                break;
            case "canceled":
            case "cancelada":
                button.setText("Ver");
                break;
            default:
                button.setText("Ver");
                break;
        }
    }
}