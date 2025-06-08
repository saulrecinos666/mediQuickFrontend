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
import com.example.mediquick.data.model.Branch;
import com.google.android.material.card.MaterialCardView;

import java.util.List;

public class BranchAdapter extends RecyclerView.Adapter<BranchAdapter.ViewHolder> {

    public interface OnBranchClick {
        void onSelect(Branch branch);
    }

    private final List<Branch> branches;
    private final OnBranchClick listener;

    public BranchAdapter(List<Branch> branches, OnBranchClick listener) {
        this.branches = branches;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_branch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < branches.size()) {
            Branch branch = branches.get(position);
            holder.bind(branch);

            // Animación de entrada
            animateItemEntry(holder.itemView, position);
        }
    }

    @Override
    public int getItemCount() {
        return branches.size();
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

        private final ImageView ivBranchIcon;
        private final TextView txtNombre;
        private final TextView txtDireccion;
        private final TextView txtDescripcion;
        private final View availabilityIndicator;
        private final TextView tvOpeningHours;
        private final TextView tvDistance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            ivBranchIcon = itemView.findViewById(R.id.ivBranchIcon);
            txtNombre = itemView.findViewById(R.id.txtNombreSucursal);
            txtDireccion = itemView.findViewById(R.id.txtDireccionSucursal);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionSucursal);
            availabilityIndicator = itemView.findViewById(R.id.availabilityIndicator);
            tvOpeningHours = itemView.findViewById(R.id.tvOpeningHours);
            tvDistance = itemView.findViewById(R.id.tvDistance);
        }

        public void bind(Branch branch) {
            if (branch == null) return;

            // Configurar textos básicos
            if (txtNombre != null) {
                txtNombre.setText(branch.getBranchName() != null ?
                        branch.getBranchName() : "Sucursal sin nombre");
            }

            if (txtDireccion != null) {
                String direccion = branch.getBranchFullAddress();
                if (direccion != null && !direccion.trim().isEmpty()) {
                    txtDireccion.setText(direccion);
                    txtDireccion.setVisibility(View.VISIBLE);
                } else {
                    txtDireccion.setText("Dirección no disponible");
                    txtDireccion.setVisibility(View.VISIBLE);
                }
            }

            if (txtDescripcion != null) {
                String descripcion = branch.getBranchDescription();
                if (descripcion != null && !descripcion.trim().isEmpty()) {
                    txtDescripcion.setText(descripcion);
                    txtDescripcion.setVisibility(View.VISIBLE);
                } else {
                    txtDescripcion.setVisibility(View.GONE);
                }
            }

            // Configurar icono según el tipo de sucursal
            setupBranchIcon(branch.getBranchName());

            // Configurar indicador de disponibilidad
            setupAvailabilityIndicator(true); // Por ahora todas activas

            // Información adicional (opcional)
            setupAdditionalInfo(branch);

            // Click listener con animación
            itemView.setOnClickListener(v -> {
                animateClick(v);
                if (listener != null) {
                    listener.onSelect(branch);
                }
            });
        }

        private void setupBranchIcon(String branchName) {
            if (ivBranchIcon == null || branchName == null) return;

            String name = branchName.toLowerCase();

            if (name.contains("centro") || name.contains("principal")) {
                ivBranchIcon.setImageResource(R.drawable.ic_hospital);
            } else if (name.contains("norte") || name.contains("sur") ||
                    name.contains("este") || name.contains("oeste")) {
                ivBranchIcon.setImageResource(R.drawable.ic_location);
            } else if (name.contains("urgencias") || name.contains("emergencias")) {
                ivBranchIcon.setImageResource(R.drawable.ic_emergency);
            } else {
                ivBranchIcon.setImageResource(R.drawable.ic_clinic); // Default
            }
        }

        private void setupAvailabilityIndicator(boolean isAvailable) {
            if (availabilityIndicator != null) {
                if (isAvailable) {
                    availabilityIndicator.setBackgroundTintList(
                            ContextCompat.getColorStateList(itemView.getContext(), R.color.status_active));
                } else {
                    availabilityIndicator.setBackgroundTintList(
                            ContextCompat.getColorStateList(itemView.getContext(), R.color.status_inactive));
                }
            }
        }

        private void setupAdditionalInfo(Branch branch) {
            // Por ahora ocultar información adicional
            // En el futuro puedes agregar horarios, distancia, etc.
            if (tvOpeningHours != null) {
                tvOpeningHours.setVisibility(View.GONE);
            }
            if (tvDistance != null) {
                tvDistance.setVisibility(View.GONE);
            }

            // Ocultar el layout completo si no hay información adicional
            View additionalInfoLayout = itemView.findViewById(R.id.additionalInfoLayout);
            if (additionalInfoLayout != null) {
                additionalInfoLayout.setVisibility(View.GONE);
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