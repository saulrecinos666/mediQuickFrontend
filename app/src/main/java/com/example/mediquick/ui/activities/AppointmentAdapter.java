package com.example.mediquick.ui.activities;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    private List<Appointment> list;
    private Context context;
    private OnItemClickListener listener;

    public AppointmentAdapter(List<Appointment> list, Context context, OnItemClickListener listener) {
        this.list = list;
        this.context = context;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment item = list.get(position);
        holder.tvId.setText("ID: " + item.getId());
        holder.tvPaciente.setText("Paciente: " + item.getPaciente());
        holder.tvSucursal.setText("Sucursal: " + item.getSucursal());
        holder.tvFecha.setText("Fecha: " + item.getFecha());

        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvId, tvPaciente, tvSucursal, tvFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvId = itemView.findViewById(R.id.tvId);
            tvPaciente = itemView.findViewById(R.id.tvPaciente);
            tvSucursal = itemView.findViewById(R.id.tvSucursal);
            tvFecha = itemView.findViewById(R.id.tvFecha);
        }
    }
}
// <!--Moris Navas-->