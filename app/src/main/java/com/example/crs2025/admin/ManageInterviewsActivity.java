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
import com.example.crs2025.adapters.InterviewAdapter;
import com.example.crs2025.models.Interview;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageInterviewsActivity extends AppCompatActivity implements InterviewAdapter.OnInterviewInteractionListener {

    private RecyclerView recyclerViewInterviews;
    private ProgressBar progressBar;
    private InterviewAdapter adapter;
    private List<Interview> interviewList;
    private DatabaseReference globalInterviewsRef;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_interviews);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        recyclerViewInterviews = findViewById(R.id.recycler_interviews);
        progressBar = findViewById(R.id.progress_bar);
        recyclerViewInterviews.setLayoutManager(new LinearLayoutManager(this));

        interviewList = new ArrayList<>();
        adapter = new InterviewAdapter(this, interviewList, this);
        recyclerViewInterviews.setAdapter(adapter);

        globalInterviewsRef = FirebaseDatabase.getInstance().getReference("globalInterviews");

        fetchInterviews();
        setupSwipeToDelete();
    }

    @Override
    public void onInterviewClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public void onInterviewLongClick(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void fetchInterviews() {
        progressBar.setVisibility(View.VISIBLE);
        globalInterviewsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interviewList.clear();
                for (DataSnapshot interviewSnap : snapshot.getChildren()) {
                    Interview interview = interviewSnap.getValue(Interview.class);
                    if (interview != null) {
                        interviewList.add(interview);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageInterviewsActivity.this, "Failed to load interviews. " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void toggleSelection(int position) {
        adapter.toggleSelection(position);
        int count = adapter.getSelectedItemCount();
        if (count == 0) {
            if (actionMode != null) {
                actionMode.finish();
            }
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
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.contextual_action_menu, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            if (item.getItemId() == R.id.action_delete) {
                deleteSelectedInterviews();
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

    private void deleteSelectedInterviews() {
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        Collections.sort(selectedItemPositions, Collections.reverseOrder());

        new AlertDialog.Builder(this)
                .setTitle("Delete Interviews")
                .setMessage("Are you sure you want to delete " + selectedItemPositions.size() + " interviews?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (int position : selectedItemPositions) {
                        Interview interviewToDelete = interviewList.get(position);
                        deleteInterview(interviewToDelete);
                    }
                    Toast.makeText(this, selectedItemPositions.size() + " interviews deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteInterview(Interview interview) {
        DatabaseReference companyInterviewRef = FirebaseDatabase.getInstance().getReference("interviews")
                .child(interview.getCompanyId()).child(interview.getInterviewId());

        DatabaseReference studentInterviewRef = FirebaseDatabase.getInstance().getReference("studentInterviews")
                .child(interview.getStudentId()).child(interview.getInterviewId());

        globalInterviewsRef.child(interview.getInterviewId()).removeValue();
        companyInterviewRef.removeValue();
        studentInterviewRef.removeValue();
    }

    private void setupSwipeToDelete() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                Interview interviewToDelete = interviewList.get(position);

                new AlertDialog.Builder(ManageInterviewsActivity.this)
                        .setTitle("Delete Interview")
                        .setMessage("Are you sure you want to delete this interview schedule?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteInterview(interviewToDelete);
                            Toast.makeText(ManageInterviewsActivity.this, "Interview deleted.", Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton(android.R.string.no, (dialog, which) -> adapter.notifyItemChanged(position))
                        .setCancelable(false)
                        .show();
            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                View itemView = viewHolder.itemView;
                Drawable icon = ContextCompat.getDrawable(getBaseContext(), R.drawable.ic_delete);
                ColorDrawable background = new ColorDrawable(ContextCompat.getColor(getBaseContext(), R.color.red));

                int iconMargin = (itemView.getHeight() - icon.getIntrinsicHeight()) / 2;
                int iconTop = itemView.getTop() + iconMargin;
                int iconBottom = iconTop + icon.getIntrinsicHeight();

                if (dX < 0) { // Swiping left
                    int iconLeft = itemView.getRight() - iconMargin - icon.getIntrinsicWidth();
                    int iconRight = itemView.getRight() - iconMargin;
                    icon.setBounds(iconLeft, iconTop, iconRight, iconBottom);
                    background.setBounds(itemView.getRight() + (int) dX, itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else { // Not swiping
                    background.setBounds(0, 0, 0, 0);
                }
                background.draw(c);
                icon.draw(c);

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        }).attachToRecyclerView(recyclerViewInterviews);
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