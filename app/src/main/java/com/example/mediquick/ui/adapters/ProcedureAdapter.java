package com.example.mediquick.ui.adapters;

import android.animation.ObjectAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.MedicalProcedure;
import com.google.android.material.chip.Chip;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProcedureAdapter extends RecyclerView.Adapter<ProcedureAdapter.ViewHolder> {

    public interface OnProcedureClick {
        void onSelect(MedicalProcedure procedure);
    }

    private final List<MedicalProcedure> procedures;
    private final OnProcedureClick listener;

    public ProcedureAdapter(List<MedicalProcedure> procedures, OnProcedureClick listener) {
        this.procedures = procedures;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_procedure, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < procedures.size()) {
            MedicalProcedure procedure = procedures.get(position);
            holder.bind(procedure);

            // Animación de entrada
            animateItemEntry(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return procedures.size();
    }

    private void animateItemEntry(View view, int position) {
        view.setAlpha(0f);
        view.setTranslationY(50f);

        ObjectAnimator fadeIn = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
        ObjectAnimator slideUp = ObjectAnimator.ofFloat(view, "translationY", 50f, 0f);

        fadeIn.setDuration(250);
        slideUp.setDuration(250);

        fadeIn.setStartDelay(position * 30L);
        slideUp.setStartDelay(position * 30L);

        fadeIn.setInterpolator(new AccelerateDecelerateInterpolator());
        slideUp.setInterpolator(new AccelerateDecelerateInterpolator());

        fadeIn.start();
        slideUp.start();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final ImageView ivProcedureIcon;
        private final TextView txtNombre;
        private final TextView txtEspecialidad;
        private final TextView txtDuracion;
        private final TextView txtCosto;
        private final Chip chipOnlineStatus;
        private final Chip chipAvailableSlots;
        private final Chip chipRequiresConfirmation;
        private final View badgesLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivProcedureIcon = itemView.findViewById(R.id.ivProcedureIcon);
            txtNombre = itemView.findViewById(R.id.txtNombreProcedimiento);
            txtEspecialidad = itemView.findViewById(R.id.txtEspecialidadProcedimiento);
            txtDuracion = itemView.findViewById(R.id.txtDuracionProcedimiento);
            txtCosto = itemView.findViewById(R.id.txtCostoProcedimiento);
            chipOnlineStatus = itemView.findViewById(R.id.chipOnlineStatus);
            chipAvailableSlots = itemView.findViewById(R.id.chipAvailableSlots);
            chipRequiresConfirmation = itemView.findViewById(R.id.chipRequiresConfirmation);
            badgesLayout = itemView.findViewById(R.id.badgesLayout);
        }

        public void bind(MedicalProcedure procedure) {
            if (procedure == null) return;

            // Configurar textos básicos
            if (txtNombre != null) {
                txtNombre.setText(procedure.getProcedureName() != null ?
                        procedure.getProcedureName() : "Procedimiento sin nombre");
            }

            if (txtEspecialidad != null) {
                String especialidad = procedure.getSpecialty();
                if (especialidad != null && !especialidad.trim().isEmpty()) {
                    txtEspecialidad.setText(especialidad);
                    txtEspecialidad.setVisibility(View.VISIBLE);
                } else {
                    txtEspecialidad.setText("General");
                    txtEspecialidad.setVisibility(View.VISIBLE);
                }
            }

            if (txtDuracion != null) {
                String duracion = procedure.getProcedureDuration();
                if (duracion != null && !duracion.trim().isEmpty()) {
                    txtDuracion.setText(duracion);
                } else {
                    txtDuracion.setText("Duración no especificada");
                }
            }

            // Configurar precio con formato
            if (txtCosto != null) {
                setupPriceDisplay(procedure.getProcedureCost());
            }

            // Configurar icono según la especialidad
            setupProcedureIcon(procedure.getSpecialty());

            // Configurar estado online
            setupOnlineStatus(procedure.isAvailableOnline());

            // Configurar badges adicionales
            setupAdditionalBadges(procedure);

            // Click listener con animación
            itemView.setOnClickListener(v -> {
                animateClick(v);
                if (listener != null) {
                    listener.onSelect(procedure);
                }
            });
        }

        private void setupPriceDisplay(double cost) {
            try {
                NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("es", "MX"));
                String formattedPrice = formatter.format(cost);
                txtCosto.setText(formattedPrice);
            } catch (Exception e) {
                txtCosto.setText("$" + String.format("%.2f", cost));
            }
        }

        private void setupProcedureIcon(String specialty) {
            if (ivProcedureIcon == null || specialty == null) return;

            String spec = specialty.toLowerCase();

            if (spec.contains("cardiolog")) {
                ivProcedureIcon.setImageResource(R.drawable.ic_cardiology);
            } else if (spec.contains("radiolog") || spec.contains("imagen")) {
                ivProcedureIcon.setImageResource(R.drawable.ic_radiology);
            } else if (spec.contains("odontolog") || spec.contains("dental")) {
                ivProcedureIcon.setImageResource(R.drawable.ic_dental);
            } else if (spec.contains("oftalmolog") || spec.contains("vista")) {
                ivProcedureIcon.setImageResource(R.drawable.ic_ophthalmology);
            } else if (spec.contains("laboratorio") || spec.contains("análisis")) {
                ivProcedureIcon.setImageResource(R.drawable.ic_laboratory);
            } else {
                ivProcedureIcon.setImageResource(R.drawable.ic_medical_procedure); // Default
            }
        }

        private void setupOnlineStatus(boolean isOnline) {
            if (chipOnlineStatus != null) {
                if (isOnline) {
                    chipOnlineStatus.setVisibility(View.VISIBLE);
                    chipOnlineStatus.setText("Online");
                    chipOnlineStatus.setChipBackgroundColorResource(R.color.online_background);
                } else {
                    chipOnlineStatus.setVisibility(View.GONE);
                }
            }
        }

        private void setupAdditionalBadges(MedicalProcedure procedure) {
            // Por ahora ocultar badges adicionales
            // En el futuro puedes agregar slots disponibles, confirmación requerida, etc.
            if (chipAvailableSlots != null) {
                chipAvailableSlots.setVisibility(View.GONE);
            }
            if (chipRequiresConfirmation != null) {
                chipRequiresConfirmation.setVisibility(View.GONE);
            }
            if (badgesLayout != null) {
                badgesLayout.setVisibility(View.GONE);
            }
        }

        private void animateClick(View view) {
            view.animate()
                    .scaleX(0.95f)
                    .scaleY(0.95f)
                    .setDuration(100)
                    .withEndAction(() ->
                            view.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(100)
                                    .start())
                    .start();
        }
    }
}