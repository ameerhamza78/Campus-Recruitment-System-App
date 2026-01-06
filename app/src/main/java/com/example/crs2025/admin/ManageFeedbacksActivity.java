package com.example.crs2025.admin;

import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ActionMode;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.adapters.FeedbackAdapter;
import com.example.crs2025.models.Feedback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ManageFeedbacksActivity extends AppCompatActivity implements FeedbackAdapter.OnFeedbackInteractionListener {

    private RecyclerView recyclerViewFeedbacks;
    private ProgressBar progressBar;
    private TextView tvNoFeedbacks;
    private FeedbackAdapter adapter;
    private List<Feedback> feedbackList;
    private ActionMode actionMode;
    private ValueEventListener studentListener, companyListener;
    private DatabaseReference studentFeedbacksRef, companyFeedbacksRef;
    private final AtomicInteger fetchCounter = new AtomicInteger(0);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_feedbacks);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        recyclerViewFeedbacks = findViewById(R.id.recycler_feedbacks);
        progressBar = findViewById(R.id.progress_bar);
        tvNoFeedbacks = findViewById(R.id.tv_no_feedbacks);
        recyclerViewFeedbacks.setLayoutManager(new LinearLayoutManager(this));

        feedbackList = new ArrayList<>();
        adapter = new FeedbackAdapter(this, feedbackList, this);
        recyclerViewFeedbacks.setAdapter(adapter);

        studentFeedbacksRef = FirebaseDatabase.getInstance().getReference("studentFeedbacks");
        companyFeedbacksRef = FirebaseDatabase.getInstance().getReference("companyFeedbacks");

        setupSwipeToDelete();
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchFeedbacks();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (studentListener != null) studentFeedbacksRef.removeEventListener(studentListener);
        if (companyListener != null) companyFeedbacksRef.removeEventListener(companyListener);
    }

    private void fetchFeedbacks() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoFeedbacks.setVisibility(View.GONE);
        feedbackList.clear();
        adapter.notifyDataSetChanged();
        fetchCounter.set(2); // Reset for two listeners

        studentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.removeIf(f -> "student".equalsIgnoreCase(f.getAuthorRole()));
                for (DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    Feedback feedback = feedbackSnap.getValue(Feedback.class);
                    if (feedback != null) feedbackList.add(feedback);
                }
                checkFetchCompletion();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { checkFetchCompletion(); }
        };

        companyListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                feedbackList.removeIf(f -> "company".equalsIgnoreCase(f.getAuthorRole()));
                for (DataSnapshot feedbackSnap : snapshot.getChildren()) {
                    Feedback feedback = feedbackSnap.getValue(Feedback.class);
                    if (feedback != null) feedbackList.add(feedback);
                }
                checkFetchCompletion();
            }
            @Override public void onCancelled(@NonNull DatabaseError e) { checkFetchCompletion(); }
        };

        studentFeedbacksRef.addValueEventListener(studentListener);
        companyFeedbacksRef.addValueEventListener(companyListener);
    }

    private void checkFetchCompletion() {
        if (fetchCounter.decrementAndGet() == 0) {
            updateUi();
        }
    }

    private void updateUi() {
        progressBar.setVisibility(View.GONE);
        if (feedbackList.isEmpty()) {
            tvNoFeedbacks.setVisibility(View.VISIBLE);
            recyclerViewFeedbacks.setVisibility(View.GONE);
        } else {
            Collections.sort(feedbackList, (f1, f2) -> Long.compare(f2.getTimestamp(), f1.getTimestamp()));
            tvNoFeedbacks.setVisibility(View.GONE);
            recyclerViewFeedbacks.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onFeedbackClick(int position) {
        if (actionMode != null) toggleSelection(position);
    }

    @Override
    public void onFeedbackLongClick(int position) {
        if (actionMode == null) actionMode = startSupportActionMode(actionModeCallback);
        toggleSelection(position);
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            if (actionMode != null) actionMode.finish();
        } else {
            if (actionMode != null) {
                actionMode.setTitle(String.valueOf(count));
                actionMode.invalidate();
            }
        }
    }

    private final ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.contextual_action_menu, menu);
            return true;
        }
        @Override public boolean onPrepareActionMode(ActionMode m, Menu me) { return false; }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                deleteSelectedFeedbacks();
                mode.finish();
                return true;
            }
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSelections();
        }
    };

    private void deleteSelectedFeedbacks() {
        List<Integer> selected = adapter.getSelectedItems();
        Collections.sort(selected, Collections.reverseOrder());
        new AlertDialog.Builder(this)
                .setTitle("Delete Feedbacks")
                .setMessage("Delete " + selected.size() + " feedbacks?")
                .setPositiveButton("Delete", (dialog, i) -> {
                    for (int position : selected) {
                        if(position < feedbackList.size()) deleteFeedback(feedbackList.get(position));
                    }
                    Toast.makeText(this, selected.size() + " feedbacks deleted", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteFeedback(Feedback feedback) {
        String path = "student".equalsIgnoreCase(feedback.getAuthorRole()) ? "studentFeedbacks" : "companyFeedbacks";
        FirebaseDatabase.getInstance().getReference(path).child(feedback.getFeedbackId()).removeValue();
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override public boolean onMove(@NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, @NonNull RecyclerView.ViewHolder t) { return false; }
            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                new AlertDialog.Builder(ManageFeedbacksActivity.this)
                        .setTitle("Delete Feedback")
                        .setMessage("Are you sure you want to delete this feedback?")
                        .setPositiveButton("Delete", (dialog, i) -> {
                            deleteFeedback(feedbackList.get(position));
                            Toast.makeText(ManageFeedbacksActivity.this, "Feedback deleted", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, (d, i) -> adapter.notifyItemChanged(position))
                        .setCancelable(false).show();
            }
            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView r, @NonNull RecyclerView.ViewHolder v, float dX, float dY, int a, boolean i) {
                View itemView = v.itemView;
                Drawable icon = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete);
                ColorDrawable background = new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.red));
                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin, iconBottom = iconTop + icon.getIntrinsicHeight();
                if (dX < 0) { // Swiping left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth(), iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);
                super.onChildDraw(c, r, v, dX, dY, a, i);
            }
        }).attachToRecyclerView(recyclerViewFeedbacks);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}