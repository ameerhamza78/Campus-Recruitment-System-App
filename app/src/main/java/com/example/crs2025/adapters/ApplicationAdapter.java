package com.example.crs2025.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;

import java.util.ArrayList;
import java.util.List;

public class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ApplicationViewHolder> {

    private final Context context;
    private final List<Application> applicationList;
    private final OnApplicationInteractionListener interactionListener;
    private final SparseBooleanArray selectedItems;

    public interface OnApplicationInteractionListener {
        void onApplicationClick(int position);
        void onApplicationLongClick(int position);
    }

    public ApplicationAdapter(Context context, List<Application> applicationList, OnApplicationInteractionListener listener) {
        this.context = context;
        this.applicationList = applicationList;
        this.interactionListener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ApplicationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application, parent, false);
        return new ApplicationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ApplicationViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.tvJobTitle.setText(application.getJobTitle());
        holder.tvCompanyName.setText(application.getCompanyName());
        holder.tvStudentName.setText("Applied by: " + application.getFullName());
        holder.tvStatus.setText(application.getStatus());

        // Set the status background color based on the status
        GradientDrawable statusBackground = (GradientDrawable) holder.tvStatus.getBackground().mutate();
        switch (application.getStatus()) {
            case "Accepted":
                statusBackground.setColor(ContextCompat.getColor(context, R.color.status_accepted));
                break;
            case "Rejected":
                statusBackground.setColor(ContextCompat.getColor(context, R.color.status_rejected));
                break;
            default: // Pending
                statusBackground.setColor(ContextCompat.getColor(context, R.color.status_pending));
                break;
        }
        
        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onApplicationClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onApplicationLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
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

    public static class ApplicationViewHolder extends RecyclerView.ViewHolder {
        final TextView tvJobTitle;
        final TextView tvCompanyName;
        final TextView tvStudentName;
        final TextView tvStatus;

        public ApplicationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStatus = itemView.findViewById(R.id.tv_application_status);
        }
    }
}