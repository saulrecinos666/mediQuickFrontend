package com.example.mediquick.ui.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mediquick.R;
import com.example.mediquick.data.model.Branch;

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
    public BranchAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_branch, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BranchAdapter.ViewHolder holder, int position) {
        Branch b = branches.get(position);
        holder.txtNombre.setText("🏥 " + b.getBranchName());
        holder.txtDireccion.setText("📍 " + b.getBranchFullAddress());
        holder.txtDescripcion.setText("📝 " + b.getBranchDescription());

        holder.itemView.setOnClickListener(v -> listener.onSelect(b));
    }

    @Override
    public int getItemCount() {
        return branches.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre, txtDireccion, txtDescripcion;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtNombre = itemView.findViewById(R.id.txtNombreSucursal);
            txtDireccion = itemView.findViewById(R.id.txtDireccionSucursal);
            txtDescripcion = itemView.findViewById(R.id.txtDescripcionSucursal);
        }
    }
}
