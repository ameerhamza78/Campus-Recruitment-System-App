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

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private final Context context;
    private final List<User> studentList;
    private final OnStudentInteractionListener interactionListener;
    private final SparseBooleanArray selectedItems;

    public interface OnStudentInteractionListener {
        void onStudentClick(int position);
        void onStudentLongClick(int position);
    }

    public StudentAdapter(Context context, List<User> studentList, OnStudentInteractionListener listener) {
        this.context = context;
        this.studentList = studentList;
        this.interactionListener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        User student = studentList.get(position);
        holder.tvStudentName.setText(student.getName());
        holder.tvStudentEmail.setText(student.getEmail());
        holder.tvStudentEnrollment.setText("Enrollment: " + student.getEnrollmentNo());

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onStudentClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onStudentLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
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

    public static class StudentViewHolder extends RecyclerView.ViewHolder {
        final TextView tvStudentName;
        final TextView tvStudentEmail;
        final TextView tvStudentEnrollment;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvStudentName = itemView.findViewById(R.id.tv_student_name);
            tvStudentEmail = itemView.findViewById(R.id.tv_student_email);
            tvStudentEnrollment = itemView.findViewById(R.id.tv_student_enrollment);
        }
    }
}
