package com.example.crs2025.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crs2025.R;
import com.example.crs2025.models.Interview;
import java.util.List;

public class InterviewStatusAdapter extends RecyclerView.Adapter<InterviewStatusAdapter.ViewHolder> {

    private final Context context;
    private final List<Interview> interviewList;

    public InterviewStatusAdapter(Context context, List<Interview> interviewList) {
        this.context = context;
        this.interviewList = interviewList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interview_student_view, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Interview interview = interviewList.get(position);

        holder.tvJobTitle.setText(interview.getJobTitle());
        holder.tvCompanyName.setText(interview.getCompanyName());
        // ** THE FIX **: Use the correct method names from the Interview model
        holder.tvInterviewDate.setText("Date: " + interview.getInterviewDate());
        holder.tvInterviewTime.setText("Time: " + interview.getInterviewTime());
        holder.tvInterviewLocation.setText("Location: " + interview.getLocation());
    }

    @Override
    public int getItemCount() {
        return interviewList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvJobTitle, tvCompanyName, tvInterviewDate, tvInterviewTime, tvInterviewLocation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvCompanyName = itemView.findViewById(R.id.tv_company_name);
            tvInterviewDate = itemView.findViewById(R.id.tv_interview_date);
            tvInterviewTime = itemView.findViewById(R.id.tv_interview_time);
            tvInterviewLocation = itemView.findViewById(R.id.tv_interview_location);
        }
    }
}