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
import com.example.crs2025.adapters.JobAdapter;
import com.example.crs2025.models.Job;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageJobsActivity extends AppCompatActivity implements JobAdapter.OnJobInteractionListener {

    private RecyclerView recyclerViewJobs;
    private ProgressBar progressBar;
    private JobAdapter adapter;
    private List<Job> jobList;
    private DatabaseReference globalJobsRef;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_jobs);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        recyclerViewJobs = findViewById(R.id.recycler_jobs);
        progressBar = findViewById(R.id.progress_bar);
        recyclerViewJobs.setLayoutManager(new LinearLayoutManager(this));

        jobList = new ArrayList<>();
        adapter = new JobAdapter(this, jobList, this);
        recyclerViewJobs.setAdapter(adapter);

        globalJobsRef = FirebaseDatabase.getInstance().getReference("globalJobs");

        fetchJobs();
        setupSwipeToDelete();
    }

    @Override
    public void onJobClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public void onJobLongClick(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void fetchJobs() {
        progressBar.setVisibility(View.VISIBLE);
        globalJobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                jobList.clear();
                for (DataSnapshot jobSnap : snapshot.getChildren()) {
                    Job job = jobSnap.getValue(Job.class);
                    if (job != null) {
                        jobList.add(job);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageJobsActivity.this, "Failed to load jobs. " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                deleteSelectedJobs();
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

    private void deleteSelectedJobs() {
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        Collections.sort(selectedItemPositions, Collections.reverseOrder());

        new AlertDialog.Builder(this)
                .setTitle("Delete Jobs")
                .setMessage("Are you sure you want to delete " + selectedItemPositions.size() + " jobs?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (int position : selectedItemPositions) {
                        Job jobToDelete = jobList.get(position);
                        deleteJob(jobToDelete);
                    }
                    Toast.makeText(this, selectedItemPositions.size() + " jobs deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void deleteJob(Job job) {
        DatabaseReference companyJobRef = FirebaseDatabase.getInstance().getReference("jobs")
                .child(job.getCompanyId()).child(job.getJobId());

        globalJobsRef.child(job.getJobId()).removeValue();
        companyJobRef.removeValue();
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
                Job jobToDelete = jobList.get(position);

                new AlertDialog.Builder(ManageJobsActivity.this)
                        .setTitle("Delete Job")
                        .setMessage("Are you sure you want to delete the job '" + jobToDelete.getJobTitle() + "'?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            deleteJob(jobToDelete);
                            Toast.makeText(ManageJobsActivity.this, "Job deleted.", Toast.LENGTH_SHORT).show();
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
        }).attachToRecyclerView(recyclerViewJobs);
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