package com.example.crs2025.adapters;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.Feedback;

import java.util.ArrayList;
import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {

    private final Context context;
    private final List<Feedback> feedbackList;
    private final OnFeedbackInteractionListener interactionListener;
    private final SparseBooleanArray selectedItems;

    public interface OnFeedbackInteractionListener {
        void onFeedbackClick(int position);
        void onFeedbackLongClick(int position);
    }

    public FeedbackAdapter(Context context, List<Feedback> feedbackList, OnFeedbackInteractionListener listener) {
        this.context = context;
        this.feedbackList = feedbackList;
        this.interactionListener = listener;
        this.selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Feedback feedback = feedbackList.get(position);

        holder.tvFeedbackText.setText(feedback.getFeedbackText());
        holder.tvFeedbackAuthor.setText("From: " + feedback.getAuthorName());
        holder.tvFeedbackRole.setText(feedback.getAuthorRole().toUpperCase());

        // Set the color of the role tag
        if ("student".equalsIgnoreCase(feedback.getAuthorRole())) {
            holder.tvFeedbackRole.getBackground().setTint(ContextCompat.getColor(context, R.color.brand_blue));
        } else {
            holder.tvFeedbackRole.getBackground().setTint(ContextCompat.getColor(context, R.color.status_accepted)); // Using green for companies
        }

        holder.itemView.setActivated(selectedItems.get(position, false));

        holder.itemView.setOnClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onFeedbackClick(holder.getAdapterPosition());
            }
        });

        holder.itemView.setOnLongClickListener(v -> {
            if (interactionListener != null) {
                interactionListener.onFeedbackLongClick(holder.getAdapterPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return feedbackList.size();
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

    public static class FeedbackViewHolder extends RecyclerView.ViewHolder {
        final TextView tvFeedbackText;
        final TextView tvFeedbackAuthor;
        final TextView tvFeedbackRole;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvFeedbackText = itemView.findViewById(R.id.tv_feedback_text);
            tvFeedbackAuthor = itemView.findViewById(R.id.tv_feedback_author);
            tvFeedbackRole = itemView.findViewById(R.id.tv_feedback_role);
        }
    }
}