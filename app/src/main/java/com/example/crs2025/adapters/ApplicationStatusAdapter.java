package com.example.crs2025.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;

import java.util.List;

public class ApplicationStatusAdapter extends RecyclerView.Adapter<ApplicationStatusAdapter.ViewHolder> {

    private final Context context;
    private final List<Application> applicationList;

    public ApplicationStatusAdapter(Context context, List<Application> applicationList) {
        this.context = context;
        this.applicationList = applicationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_status, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);

        holder.tvJobTitle.setText(application.getJobTitle());
        holder.tvCompanyName.setText(application.getCompanyName());
        holder.tvStatus.setText(application.getStatus());

        // Set the status indicator color based on the status text
        switch (application.getStatus().toLowerCase()) {
            case "accepted":
                holder.tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.status_accepted));
                break;
            case "rejected":
                holder.tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.status_rejected));
                break;
            case "interview scheduled":
                holder.tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.brand_blue));
                break;
            default: // "Pending"
                holder.tvStatus.getBackground().setTint(ContextCompat.getColor(context, R.color.status_pending));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvStatus = itemView.findViewById(R.id.tv_application_status);
        }
    }
}