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
import com.example.crs2025.models.Interview;

import java.util.ArrayList;
import java.util.List;

public class InterviewAdapter extends RecyclerView.Adapter<InterviewAdapter.InterviewViewHolder> {

    private final Context context;
    private final List<Interview> interviewList;
    private final OnInterviewInteractionListener interactionListener;
    private final SparseBooleanArray selectedItems;

    public interface OnInterviewInteractionListener {
        void onInterviewClick(int position);
        void onInterviewLongClick(int position);
    }

    public InterviewAdapter(Context context, List<Interview> interviewList, OnInterviewInteractionListener listener) {
        this.context = context;
        this.interviewList = interviewList;
        this.interactionListener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public InterviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_interview, parent, false);
        return new InterviewViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InterviewViewHolder holder, int position) {
        Interview interview = interviewList.get(position);
        holder.tvJobTitle.setText(interview.getJobTitle());
        holder.tvStudentName.setText("Candidate: " + interview.getStudentName());

        // ** THE FIX **: Use the correct method names
        String interviewDetails = "Time: " + interview.getInterviewTime() + " on " + interview.getInterviewDate();
        holder.tvInterviewDetails.setText(interviewDetails);

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onInterviewClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onInterviewLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return interviewList.size();
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

    public static class InterviewViewHolder extends RecyclerView.ViewHolder {
        final TextView tvJobTitle;
        final TextView tvStudentName;
        final TextView tvInterviewDetails;

        public InterviewViewHolder(@NonNull View itemView) {
            super(itemView);
            tvJobTitle = itemView.findViewById(R.id.tv_job_title);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvInterviewDetails = itemView.findViewById(R.id.tv_interview_details);
        }
    }
}