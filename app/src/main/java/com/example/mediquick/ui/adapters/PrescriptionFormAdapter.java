package com.example.mediquick.ui.adapters;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.databinding.ItemMedicamentoBinding;
import com.example.mediquick.data.model.PrescriptionForm;

import java.util.List;

public class PrescriptionFormAdapter extends RecyclerView.Adapter<PrescriptionFormAdapter.ViewHolder> {

    private final List<PrescriptionForm> medicamentos;

    public PrescriptionFormAdapter(List<PrescriptionForm> medicamentos) {
        this.medicamentos = medicamentos;
    }

    @NonNull
    @Override
    public PrescriptionFormAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(ItemMedicamentoBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull PrescriptionFormAdapter.ViewHolder holder, int position) {
        PrescriptionForm item = medicamentos.get(position);

        holder.binding.edtNombre.setText(item.getNombre());
        holder.binding.edtDosis.setText(item.getDosis());
        holder.binding.edtFrecuencia.setText(item.getFrecuencia());
        holder.binding.edtDuracion.setText(item.getDuracion());
        holder.binding.edtUnidad.setText(item.getUnidad());
        holder.binding.edtNotas.setText(item.getNotas());

        holder.binding.edtNombre.addTextChangedListener(new SimpleWatcher(s -> item.setNombre(s)));
        holder.binding.edtDosis.addTextChangedListener(new SimpleWatcher(s -> item.setDosis(s)));
        holder.binding.edtFrecuencia.addTextChangedListener(new SimpleWatcher(s -> item.setFrecuencia(s)));
        holder.binding.edtDuracion.addTextChangedListener(new SimpleWatcher(s -> item.setDuracion(s)));
        holder.binding.edtUnidad.addTextChangedListener(new SimpleWatcher(s -> item.setUnidad(s)));
        holder.binding.edtNotas.addTextChangedListener(new SimpleWatcher(s -> item.setNotas(s)));
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ItemMedicamentoBinding binding;
        ViewHolder(ItemMedicamentoBinding b) {
            super(b.getRoot());
            this.binding = b;
        }
    }

    interface TextCallback {
        void onTextChanged(String s);
    }

    static class SimpleWatcher implements TextWatcher {
        private final TextCallback callback;
        public SimpleWatcher(TextCallback callback) { this.callback = callback; }
        @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
        @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.onTextChanged(s.toString());
        }
        @Override public void afterTextChanged(Editable s) {}
    }
}
