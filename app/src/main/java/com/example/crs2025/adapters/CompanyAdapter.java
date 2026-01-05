package com.example.crs2025.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.User;

import java.util.ArrayList;
import java.util.List;

public class CompanyAdapter extends RecyclerView.Adapter<CompanyAdapter.CompanyViewHolder> {

    private final Context context;
    private final List<User> companyList;
    private final OnCompanyInteractionListener interactionListener;
    private final SparseBooleanArray selectedItems;

    public interface OnCompanyInteractionListener {
        void onCompanyClick(int position);
        void onCompanyLongClick(int position);
    }

    public CompanyAdapter(Context context, List<User> companyList, OnCompanyInteractionListener listener) {
        this.context = context;
        this.companyList = companyList;
        this.interactionListener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public CompanyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_company, parent, false);
        return new CompanyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CompanyViewHolder holder, int position) {
        User company = companyList.get(position);
        holder.tvCompanyName.setText(company.getName());
        holder.tvCompanyEmail.setText(company.getEmail());

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onCompanyClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onCompanyLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return companyList.size();
    }

    public void toggleSelection(int pos) {
        if (selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelections() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public static class CompanyViewHolder extends RecyclerView.ViewHolder {
        final TextView tvCompanyName;
        final TextView tvCompanyEmail;

        public CompanyViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvCompanyEmail = itemView.findViewById(R.id.tv_company_email);
        }
    }
}