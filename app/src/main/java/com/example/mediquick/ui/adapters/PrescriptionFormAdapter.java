package com.example.mediquick.ui.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.databinding.ItemMedicamentoBinding;
import com.example.mediquick.data.model.PrescriptionForm;

import java.util.List;

public class PrescriptionFormAdapter extends RecyclerView.Adapter<PrescriptionFormAdapter.ViewHolder> {

    private final List<PrescriptionForm> medicamentos;
    private OnMedicamentoActionListener listener;

    // Datos predefinidos para autocompletado
    private final String[] unidades = {
            "mg", "g", "ml", "cc", "gotas", "comprimidos", "cápsulas",
            "tabletas", "sobres", "ampollas", "cucharadas", "UI", "mcg"
    };

    private final String[] frecuenciasComunes = {
            "Cada 8 horas", "Cada 12 horas", "Cada 24 horas",
            "1 vez al día", "2 veces al día", "3 veces al día", "4 veces al día",
            "Cada 6 horas", "Cada 4 horas", "En ayunas", "Después de comidas",
            "Antes de dormir", "Según necesidad"
    };

    private final String[] duracionesComunes = {
            "3 días", "5 días", "7 días", "10 días", "14 días",
            "1 semana", "2 semanas", "3 semanas", "1 mes",
            "Hasta agotar", "Por tiempo indefinido", "Según evolución"
    };

    public interface OnMedicamentoActionListener {
        void onEliminarMedicamento(int position);
        void onMedicamentoValidado(int position, boolean esValido);
    }

    public PrescriptionFormAdapter(List<PrescriptionForm> medicamentos) {
        this.medicamentos = medicamentos;
    }

    public void setOnMedicamentoActionListener(OnMedicamentoActionListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemMedicamentoBinding binding = ItemMedicamentoBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding, parent.getContext());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        PrescriptionForm item = medicamentos.get(position);
        holder.bind(item, position);
    }

    @Override
    public int getItemCount() {
        return medicamentos.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        private final ItemMedicamentoBinding binding;
        private final Context context;
        private PrescriptionForm currentItem;
        private int currentPosition;

        ViewHolder(ItemMedicamentoBinding binding, Context context) {
            super(binding.getRoot());
            this.binding = binding;
            this.context = context;
            setupAutoComplete();
            setupClickListeners();
        }

        void bind(PrescriptionForm item, int position) {
            this.currentItem = item;
            this.currentPosition = position;

            clearTextWatchers();

            binding.txtMedicamentoNumero.setText("Medicamento #" + (position + 1));
            binding.edtNombre.setText(item.getNombre());
            binding.edtDosis.setText(item.getDosis());
            binding.edtUnidad.setText(item.getUnidad());

            binding.edtFrecuencia.setText(item.getFrecuencia());
            binding.edtDuracion.setText(item.getDuracion());

            binding.edtNotas.setText(item.getNotas());

            binding.btnEliminar.setVisibility(medicamentos.size() > 1 ? View.VISIBLE : View.GONE);

            // Configurar listeners de texto
            setupTextWatchers();

            // Validar estado inicial
            validateMedicamento();
        }

        private void setupAutoComplete() {
            // Configurar autocompletado para unidades (ya funciona porque es AutoCompleteTextView)
            ArrayAdapter<String> unidadesAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, unidades);
            binding.edtUnidad.setAdapter(unidadesAdapter);

            // Configurar autocompletado para frecuencias (ahora es AutoCompleteTextView)
            ArrayAdapter<String> frecuenciasAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, frecuenciasComunes);

            // Verificar que el campo existe y es del tipo correcto
            if (binding.edtFrecuencia instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) binding.edtFrecuencia).setAdapter(frecuenciasAdapter);
            }

            // Configurar autocompletado para duraciones (ahora es AutoCompleteTextView)
            ArrayAdapter<String> duracionesAdapter = new ArrayAdapter<>(context,
                    android.R.layout.simple_dropdown_item_1line, duracionesComunes);

            // Verificar que el campo existe y es del tipo correcto
            if (binding.edtDuracion instanceof AutoCompleteTextView) {
                ((AutoCompleteTextView) binding.edtDuracion).setAdapter(duracionesAdapter);
            }
        }

        private void setupClickListeners() {
            binding.btnEliminar.setOnClickListener(v -> {
                if (listener != null && medicamentos.size() > 1) {
                    listener.onEliminarMedicamento(currentPosition);
                }
            });
        }

        private void clearTextWatchers() {
            // Aquí podrías limpiar watchers existentes si los guardas en variables
        }

        private void setupTextWatchers() {
            binding.edtNombre.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setNombre(s);
                validateMedicamento();
            }));

            binding.edtDosis.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setDosis(s);
                validateMedicamento();
            }));

            binding.edtUnidad.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setUnidad(s);
                validateMedicamento();
            }));

            // Para frecuencia (AutoCompleteTextView)
            binding.edtFrecuencia.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setFrecuencia(s);
                validateMedicamento();
            }));

            // Para duración (AutoCompleteTextView)
            binding.edtDuracion.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setDuracion(s);
                validateMedicamento();
            }));

            binding.edtNotas.addTextChangedListener(new SimpleWatcher(s -> {
                currentItem.setNotas(s);
            }));
        }

        private void validateMedicamento() {
            boolean esValido = !currentItem.getNombre().trim().isEmpty() &&
                    !currentItem.getDosis().trim().isEmpty() &&
                    !currentItem.getUnidad().trim().isEmpty() &&
                    !currentItem.getFrecuencia().trim().isEmpty() &&
                    !currentItem.getDuracion().trim().isEmpty();

            // Cambiar el color del borde de la card según validación
            if (esValido) {
                binding.getRoot().setStrokeColor(context.getColor(R.color.status_confirmed));
                binding.getRoot().setStrokeWidth(2);
            } else {
                binding.getRoot().setStrokeColor(context.getColor(R.color.divider_color));
                binding.getRoot().setStrokeWidth(1);
            }

            if (listener != null) {
                listener.onMedicamentoValidado(currentPosition, esValido);
            }
        }
    }

    // Métodos utilitarios para el adapter
    public void eliminarMedicamento(int position) {
        if (position >= 0 && position < medicamentos.size() && medicamentos.size() > 1) {
            medicamentos.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, medicamentos.size());
        }
    }

    public void agregarMedicamento() {
        medicamentos.add(new PrescriptionForm());
        notifyItemInserted(medicamentos.size() - 1);
    }

    public boolean todosMedicamentosValidos() {
        for (PrescriptionForm medicamento : medicamentos) {
            if (medicamento.getNombre().trim().isEmpty() ||
                    medicamento.getDosis().trim().isEmpty() ||
                    medicamento.getUnidad().trim().isEmpty() ||
                    medicamento.getFrecuencia().trim().isEmpty() ||
                    medicamento.getDuracion().trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public int getMedicamentosValidos() {
        int count = 0;
        for (PrescriptionForm medicamento : medicamentos) {
            if (!medicamento.getNombre().trim().isEmpty() &&
                    !medicamento.getDosis().trim().isEmpty() &&
                    !medicamento.getUnidad().trim().isEmpty() &&
                    !medicamento.getFrecuencia().trim().isEmpty() &&
                    !medicamento.getDuracion().trim().isEmpty()) {
                count++;
            }
        }
        return count;
    }

    // Clase helper para TextWatcher
    interface TextCallback {
        void onTextChanged(String s);
    }

    static class SimpleWatcher implements TextWatcher {
        private final TextCallback callback;

        public SimpleWatcher(TextCallback callback) {
            this.callback = callback;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            callback.onTextChanged(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) {}
    }
}