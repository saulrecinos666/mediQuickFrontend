package com.example.mediquick.ui.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentCard;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

public class PrescriptionAdapter extends RecyclerView.Adapter<PrescriptionAdapter.ViewHolder> {

    public interface OnClick {
        void select(AppointmentCard cita);
    }

    private final List<AppointmentCard> citas;
    private final OnClick listener;

    public PrescriptionAdapter(List<AppointmentCard> citas, OnClick listener) {
        this.citas = citas;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cita_card, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AppointmentCard item = citas.get(position);

        // Configurar elementos básicos (siempre presentes)
        if (holder.txtPaciente != null) {
            holder.txtPaciente.setText(item.getPaciente());
        }

        if (holder.txtFecha != null) {
            holder.txtFecha.setText(item.getFecha());
        }

        if (holder.txtSucursal != null) {
            holder.txtSucursal.setText(item.getSucursal());
        }

        // Configurar estado (puede ser TextView o Chip)
        if (holder.txtEstado != null) {
            // Layout antiguo con TextView
            holder.txtEstado.setText("Estado: " + item.getEstado());
        } else if (holder.chipEstado != null) {
            // Layout nuevo con Chip
            holder.chipEstado.setText(item.getEstado());
            setupStatusChip(holder.chipEstado, item.getEstado());
        }

        // Configurar botones (solo en layout nuevo)
        if (holder.btnVerDetalles != null) {
            holder.btnVerDetalles.setOnClickListener(v -> listener.select(item));
        }

        if (holder.btnCrearReceta != null) {
            holder.btnCrearReceta.setOnClickListener(v -> listener.select(item));
        }

        // Click en toda la card (funciona para ambos layouts)
        holder.itemView.setOnClickListener(v -> listener.select(item));
    }

    private void setupStatusChip(Chip chip, String estado) {
        if (chip == null) return;

        // Configurar colores según el estado
        int backgroundColor;
        int textColor = Color.WHITE;

        switch (estado.toLowerCase()) {
            case "aceptada":
            case "confirmada":
                backgroundColor = Color.parseColor("#4CAF50"); // Verde
                break;
            case "pendiente":
                backgroundColor = Color.parseColor("#FF9800"); // Naranja
                break;
            case "cancelada":
                backgroundColor = Color.parseColor("#F44336"); // Rojo
                break;
            case "completada":
                backgroundColor = Color.parseColor("#2196F3"); // Azul
                break;
            case "en progreso":
                backgroundColor = Color.parseColor("#9C27B0"); // Púrpura
                break;
            default:
                backgroundColor = Color.parseColor("#9E9E9E"); // Gris
                break;
        }

        chip.setChipBackgroundColor(android.content.res.ColorStateList.valueOf(backgroundColor));
        chip.setTextColor(textColor);
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        // Elementos del layout antiguo
        TextView txtPaciente, txtFecha, txtSucursal, txtEstado;

        // Elementos del layout nuevo (opcionales)
        Chip chipEstado;
        MaterialButton btnVerDetalles, btnCrearReceta;

        ViewHolder(View itemView) {
            super(itemView);

            // Buscar elementos básicos (presentes en ambos layouts)
            txtPaciente = itemView.findViewById(R.id.txtPaciente);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtSucursal = itemView.findViewById(R.id.txtSucursal);

            // Buscar elementos del layout antiguo (opcional)
            txtEstado = itemView.findViewById(R.id.txtEstado);

            // Buscar elementos del layout nuevo (opcional)
            chipEstado = itemView.findViewById(R.id.chipEstado);
            btnVerDetalles = itemView.findViewById(R.id.btnVerDetalles);
            btnCrearReceta = itemView.findViewById(R.id.btnCrearReceta);
        }
    }

    // Métodos adicionales para compatibilidad
    public void updateAppointments(List<AppointmentCard> newAppointments) {
        this.citas.clear();
        this.citas.addAll(newAppointments);
        notifyDataSetChanged();
    }

    public void addAppointment(AppointmentCard appointment) {
        this.citas.add(appointment);
        notifyItemInserted(citas.size() - 1);
    }

    public void removeAppointment(int position) {
        if (position >= 0 && position < citas.size()) {
            this.citas.remove(position);
            notifyItemRemoved(position);
        }
    }
}