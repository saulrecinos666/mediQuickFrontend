package com.example.mediquick.ui.activities;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;

import java.util.ArrayList;
import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.ViewHolder> {

    private static final String TAG = "APPOINTMENT_ADAPTER";

    public interface OnItemClickListener {
        void onItemClick(Appointment appointment);
    }

    private List<Appointment> list;
    private Context context;
    private OnItemClickListener listener;

    public AppointmentAdapter(List<Appointment> list, Context context, OnItemClickListener listener) {
        // CRÍTICO: Crear una nueva lista si la recibida es null
        this.list = list != null ? list : new ArrayList<>();
        this.context = context;
        this.listener = listener;

        Log.d(TAG, "Adapter creado con " + this.list.size() + " items iniciales");
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_appointment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (list == null || position >= list.size()) {
            Log.e(TAG, "❌ Error: position " + position + " out of bounds or list is null");
            return;
        }

        Appointment item = list.get(position);
        if (item == null) {
            Log.e(TAG, "❌ Error: appointment at position " + position + " is null");
            return;
        }

        Log.d(TAG, "Binding item " + position + ": " + item.getId());

        // Bind data with null checks
        holder.tvId.setText("ID: " + (item.getId() != null ? item.getId() : "N/A"));
        holder.tvPaciente.setText("Paciente: " + (item.getPaciente() != null ? item.getPaciente() : "No especificado"));
        holder.tvSucursal.setText("Sucursal: " + (item.getSucursal() != null ? item.getSucursal() : "No especificada"));
        holder.tvFecha.setText("Fecha: " + (item.getFecha() != null ? item.getFecha() : "Sin fecha"));

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(item);
            }
        });
    }

    @Override
    public int getItemCount() {
        int count = list != null ? list.size() : 0;
        Log.d(TAG, "getItemCount() devuelve: " + count);
        return count;
    }

    /**
     * MÉTODO CORREGIDO - Este era el problema principal
     */
    public void updateAppointments(List<Appointment> newAppointments) {
        Log.d(TAG, "=== ACTUALIZANDO LISTA EN ADAPTER ===");
        Log.d(TAG, "Lista actual: " + (this.list != null ? this.list.size() : "null") + " items");
        Log.d(TAG, "Nueva lista: " + (newAppointments != null ? newAppointments.size() : "null") + " items");

        if (this.list == null) {
            Log.e(TAG, "❌ Lista interna es null, creando nueva");
            this.list = new ArrayList<>();
        }

        // Limpiar lista actual
        this.list.clear();
        Log.d(TAG, "Lista limpiada, tamaño: " + this.list.size());

        // Agregar nuevos appointments si existen
        if (newAppointments != null && !newAppointments.isEmpty()) {
            Log.d(TAG, "Agregando " + newAppointments.size() + " nuevos appointments...");

            for (int i = 0; i < newAppointments.size(); i++) {
                Appointment appointment = newAppointments.get(i);
                if (appointment != null) {
                    this.list.add(appointment);
                    Log.d(TAG, "✅ Appointment " + (i + 1) + " agregado: " + appointment.getId());
                } else {
                    Log.w(TAG, "⚠️ Appointment " + (i + 1) + " es null, omitiendo");
                }
            }

            Log.d(TAG, "✅ Total agregados: " + this.list.size());
        } else {
            Log.w(TAG, "⚠️ Nueva lista está vacía o es null");
        }

        Log.d(TAG, "Lista final tiene: " + this.list.size() + " items");
        Log.d(TAG, "Llamando notifyDataSetChanged()...");

        // Notificar cambios
        notifyDataSetChanged();

        Log.d(TAG, "✅ notifyDataSetChanged() completado");
        Log.d(TAG, "getItemCount() después de actualizar: " + getItemCount());
    }

    /**
     * Método para agregar una cita individual
     */
    public void addAppointment(Appointment appointment) {
        Log.d(TAG, "Agregando appointment individual: " + (appointment != null ? appointment.getId() : "null"));

        if (appointment != null) {
            if (list == null) {
                list = new ArrayList<>();
            }
            list.add(appointment);
            notifyItemInserted(list.size() - 1);
            Log.d(TAG, "✅ Appointment agregado, total: " + list.size());
        } else {
            Log.w(TAG, "⚠️ Appointment es null, no se puede agregar");
        }
    }

    /**
     * Método para remover una cita por posición
     */
    public void removeAppointment(int position) {
        if (list != null && position >= 0 && position < list.size()) {
            Appointment removed = list.remove(position);
            notifyItemRemoved(position);
            Log.d(TAG, "✅ Appointment removido: " + (removed != null ? removed.getId() : "null"));
        } else {
            Log.w(TAG, "⚠️ No se puede remover appointment en posición " + position);
        }
    }

    /**
     * Método para obtener una cita por posición
     */
    public Appointment getAppointment(int position) {
        if (list != null && position >= 0 && position < list.size()) {
            return list.get(position);
        }
        return null;
    }

    /**
     * Método para limpiar la lista
     */
    public void clearAppointments() {
        Log.d(TAG, "Limpiando todos los appointments");
        if (list != null) {
            int oldSize = list.size();
            list.clear();
            notifyDataSetChanged();
            Log.d(TAG, "✅ Lista limpiada, antes: " + oldSize + ", después: " + list.size());
        }
    }

    /**
     * Método para debug - obtener el estado actual
     */
    public void logCurrentState() {
        Log.d(TAG, "=== ESTADO ACTUAL DEL ADAPTER ===");
        Log.d(TAG, "Lista: " + (list != null ? "existe" : "null"));
        Log.d(TAG, "Tamaño: " + (list != null ? list.size() : "N/A"));
        Log.d(TAG, "getItemCount(): " + getItemCount());

        if (list != null && !list.isEmpty()) {
            Log.d(TAG, "Primeros elementos:");
            for (int i = 0; i < Math.min(3, list.size()); i++) {
                Appointment app = list.get(i);
                Log.d(TAG, "  [" + i + "] " + (app != null ? app.getId() : "null"));
            }
        }
        Log.d(TAG, "================================");
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

// Adapter corregido definitivamente by Assistant - Basado en código de Moris Navas