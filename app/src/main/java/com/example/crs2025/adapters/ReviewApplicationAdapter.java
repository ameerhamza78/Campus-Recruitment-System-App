package com.example.crs2025.adapters;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
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

public class ReviewApplicationAdapter extends RecyclerView.Adapter<ReviewApplicationAdapter.ViewHolder> {

    private final Context context;
    private final List<Application> applicationList;
    private final OnApplicationClickListener listener;

    public interface OnApplicationClickListener {
        void onApplicationClick(Application application);
    }

    public ReviewApplicationAdapter(Context context, List<Application> applicationList, OnApplicationClickListener listener) {
        this.context = context;
        this.applicationList = applicationList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_application_company_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Application application = applicationList.get(position);
        holder.bind(application, listener);

        holder.tvJobTitle.setText(application.getJobTitle());
        holder.tvStudentName.setText("Applicant: " + application.getFullName());
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
    }

    @Override
    public int getItemCount() {
        return applicationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvStudentName, tvStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStatus = itemView.findViewById(R.id.tv_application_status);
        }

        public void bind(final Application application, final OnApplicationClickListener listener) {
            itemView.setOnClickListener(v -> listener.onApplicationClick(application));
        }
    }
}