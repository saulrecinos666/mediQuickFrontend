package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.Institution;

import java.util.ArrayList;
import java.util.List;

public class InstitutionAdapter extends RecyclerView.Adapter<InstitutionAdapter.ViewHolder> {

    public interface OnInstitutionClick {
        void onSelect(Institution institution);
    }

    private List<Institution> institutions;
    private List<Institution> institutionsFiltered;
    private final OnInstitutionClick listener;

    public InstitutionAdapter(List<Institution> institutions, OnInstitutionClick listener) {
        this.institutions = institutions != null ? institutions : new ArrayList<>();
        this.institutionsFiltered = new ArrayList<>(this.institutions);
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_institution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (position < institutionsFiltered.size()) {
            Institution institution = institutionsFiltered.get(position);
            holder.bind(institution);
        }
    }

    @Override
    public int getItemCount() {
        return institutionsFiltered.size();
    }

    public void updateData(List<Institution> newInstitutions) {
        this.institutions = newInstitutions != null ? newInstitutions : new ArrayList<>();
        this.institutionsFiltered = new ArrayList<>(this.institutions);
        notifyDataSetChanged();
    }

    public void filter(String query) {
        institutionsFiltered.clear();

        if (query == null || query.trim().isEmpty()) {
            institutionsFiltered.addAll(institutions);
        } else {
            String lowerCaseQuery = query.toLowerCase().trim();
            for (Institution institution : institutions) {
                if (institution != null &&
                        (institution.getInstitutionName() != null &&
                                institution.getInstitutionName().toLowerCase().contains(lowerCaseQuery)) ||
                        (institution.getInstitutionDescription() != null &&
                                institution.getInstitutionDescription().toLowerCase().contains(lowerCaseQuery))) {
                    institutionsFiltered.add(institution);
                }
            }
        }
        notifyDataSetChanged();
    }

    public int getFilteredCount() {
        return institutionsFiltered.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtNombre;
        private final TextView txtDescripcion;
        //private final TextView txtEstado;
        private final ImageView ivIcon;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre = itemView.findViewById(R.id.txtNombreInstitucion);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionInstitucion);
            //txtEstado = itemView.findViewById(R.id.txtEstadoInstitucion);

            // Buscar icono si existe
            ivIcon = itemView.findViewById(R.id.ivInstitutionIcon);
        }

        public void bind(Institution institution) {
            if (institution == null) return;

            // Configurar textos básicos
            if (txtNombre != null) {
                txtNombre.setText(institution.getInstitutionName() != null ?
                        institution.getInstitutionName() : "Sin nombre");
            }

            if (txtDescripcion != null) {
                txtDescripcion.setText(institution.getInstitutionDescription() != null ?
                        institution.getInstitutionDescription() : "Sin descripción");
            }

//            if (txtEstado != null) {
//                txtEstado.setText(institution.isInstitutionStatus() ? "✅ Activo" : "❌ Inactivo");
//            }

            // Configurar icono si existe
            if (ivIcon != null) {
                setupInstitutionIcon(institution.getInstitutionName());
            }

            // Click listener
            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onSelect(institution);
                }
            });
        }

        private void setupInstitutionIcon(String institutionName) {
            if (ivIcon == null || institutionName == null) return;

            String name = institutionName.toLowerCase();

            if (name.contains("hospital")) {
                ivIcon.setImageResource(R.drawable.ic_hospital);
            } else if (name.contains("clínica") || name.contains("clinica")) {
                ivIcon.setImageResource(R.drawable.ic_clinic);
            } else if (name.contains("centro")) {
                ivIcon.setImageResource(R.drawable.ic_medical_center);
            } else {
                ivIcon.setImageResource(R.drawable.ic_hospital); // Default
            }
        }
    }
}