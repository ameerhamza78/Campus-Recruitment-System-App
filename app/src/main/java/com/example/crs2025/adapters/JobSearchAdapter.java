package com.example.crs2025.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.Job;
import com.example.crs2025.student.ApplyJobActivity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class JobSearchAdapter extends RecyclerView.Adapter<JobSearchAdapter.JobViewHolder> implements Filterable {

    private final Context context;
    private final List<Job> jobList;
    private final List<Job> jobListFull;

    public JobSearchAdapter(Context context, List<Job> jobList) {
        this.context = context;
        this.jobList = jobList;
        this.jobListFull = new ArrayList<>(jobList);
    }

    @NonNull
    @Override
    public JobViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_job_student_view, parent, false);
        return new JobViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull JobViewHolder holder, int position) {
        Job job = jobList.get(position);
        holder.tvJobTitle.setText(job.getJobTitle());
        holder.tvCompanyName.setText(job.getCompanyName());
        holder.tvJobLocation.setText("Location: " + job.getLocation());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ApplyJobActivity.class);
            intent.putExtra("JOB_DATA", (Serializable) job);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return jobList.size();
    }

    @Override
    public Filter getFilter() {
        return jobFilter;
    }

    private final Filter jobFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<Job> filteredList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filteredList.addAll(jobListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Job item : jobListFull) {
                    if (item.getJobTitle().toLowerCase().contains(filterPattern) || item.getCompanyName().toLowerCase().contains(filterPattern)) {
                        filteredList.add(item);
                    }
                }
            }
            FilterResults results = new FilterResults();
            results.values = filteredList;
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, @NonNull FilterResults results) {
            jobList.clear();
            jobList.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };

    public void updateFullList(List<Job> newFullList) {
        jobListFull.clear();
        jobListFull.addAll(newFullList);
        jobList.clear();
        jobList.addAll(newFullList);
        notifyDataSetChanged();
    }

    public static class JobViewHolder extends RecyclerView.ViewHolder {
        final TextView tvJobTitle;
        final TextView tvCompanyName;
        final TextView tvJobLocation;

        public JobViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvJobLocation = itemView.findViewById(R.id.tv_job_location);
        }
    }
}