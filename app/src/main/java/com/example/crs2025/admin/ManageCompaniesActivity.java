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
import com.example.crs2025.adapters.CompanyAdapter;
import com.example.crs2025.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ManageCompaniesActivity extends AppCompatActivity implements CompanyAdapter.OnCompanyInteractionListener {

    private RecyclerView recyclerViewCompanies;
    private ProgressBar progressBar;
    private CompanyAdapter adapter;
    private List<User> companyList;
    private DatabaseReference usersRef;
    private ActionMode actionMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_companies);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        recyclerViewCompanies = findViewById(R.id.recycler_companies);
        progressBar = findViewById(R.id.progress_bar);
        recyclerViewCompanies.setLayoutManager(new LinearLayoutManager(this));

        companyList = new ArrayList<>();
        adapter = new CompanyAdapter(this, companyList, this);
        recyclerViewCompanies.setAdapter(adapter);

        usersRef = FirebaseDatabase.getInstance().getReference("users");

        fetchCompanies();
        setupSwipeToDelete();
    }

    @Override
    public void onCompanyClick(int position) {
        if (actionMode != null) {
            toggleSelection(position);
        }
    }

    @Override
    public void onCompanyLongClick(int position) {
        if (actionMode == null) {
            actionMode = startSupportActionMode(actionModeCallback);
        }
        toggleSelection(position);
    }

    private void fetchCompanies() {
        progressBar.setVisibility(View.VISIBLE);
        usersRef.orderByChild("role").equalTo("Company").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                companyList.clear();
                for (DataSnapshot companySnap : snapshot.getChildren()) {
                    User company = companySnap.getValue(User.class);
                    if (company != null) {
                        companyList.add(company);
                    }
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ManageCompaniesActivity.this, "Failed to load companies. " + error.getMessage(), Toast.LENGTH_LONG).show();
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
                deleteSelectedCompanies();
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

    private void deleteSelectedCompanies() {
        List<Integer> selectedItemPositions = adapter.getSelectedItems();
        Collections.sort(selectedItemPositions, Collections.reverseOrder());

        new AlertDialog.Builder(this)
                .setTitle("Delete Companies")
                .setMessage("Are you sure you want to delete " + selectedItemPositions.size() + " companies?")
                .setPositiveButton("Delete", (dialog, which) -> {
                    for (int position : selectedItemPositions) {
                        User companyToDelete = companyList.get(position);
                        // ** THE FIX **: Use the correct getUserId() method
                        usersRef.child(companyToDelete.getUserId()).removeValue();
                    }
                    Toast.makeText(this, selectedItemPositions.size() + " companies deleted.", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton(android.R.string.no, null).show();
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
                User companyToDelete = companyList.get(position);

                new AlertDialog.Builder(ManageCompaniesActivity.this)
                        .setTitle("Delete Company")
                        .setMessage("Are you sure you want to delete " + companyToDelete.getName() + "?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            // ** THE FIX **: Use the correct getUserId() method
                            usersRef.child(companyToDelete.getUserId()).removeValue()
                                    .addOnSuccessListener(aVoid -> Toast.makeText(ManageCompaniesActivity.this, "Company deleted.", Toast.LENGTH_SHORT).show())
                                    .addOnFailureListener(e -> Toast.makeText(ManageCompaniesActivity.this, "Failed to delete company.", Toast.LENGTH_SHORT).show());
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
        }).attachToRecyclerView(recyclerViewCompanies);
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