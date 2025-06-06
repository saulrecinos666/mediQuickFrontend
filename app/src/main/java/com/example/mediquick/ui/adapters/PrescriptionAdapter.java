package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.AppointmentCard;

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
        holder.txtPaciente.setText("Paciente: " + item.getPaciente());
        holder.txtFecha.setText("Fecha: " + item.getFecha());
        holder.txtSucursal.setText("Sucursal: " + item.getSucursal());
        holder.txtEstado.setText("Estado: " + item.getEstado());
        holder.itemView.setOnClickListener(v -> listener.select(item));
    }

    @Override
    public int getItemCount() {
        return citas.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtPaciente, txtFecha, txtSucursal, txtEstado;
        ViewHolder(View itemView) {
            super(itemView);
            txtPaciente = itemView.findViewById(R.id.txtPaciente);
            txtFecha = itemView.findViewById(R.id.txtFecha);
            txtSucursal = itemView.findViewById(R.id.txtSucursal);
            txtEstado = itemView.findViewById(R.id.txtEstado);
        }
    }
}
