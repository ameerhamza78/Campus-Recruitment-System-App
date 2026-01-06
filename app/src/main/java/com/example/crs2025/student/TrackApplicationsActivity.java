package com.example.crs2025.student;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.adapters.ApplicationStatusAdapter;
import com.example.crs2025.models.Application;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TrackApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ApplicationStatusAdapter adapter;
    private List<Application> applicationList;
    private ProgressBar progressBar;
    private TextView tvNoApplications;
    private DatabaseReference studentApplicationsRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_applications);

        // --- Toolbar Setup ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // --- Initialize Views ---
        recyclerView = findViewById(R.id.recycler_applications);
        progressBar = findViewById(R.id.progress_bar);
        tvNoApplications = findViewById(R.id.tv_no_applications);

        // --- RecyclerView Setup ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        adapter = new ApplicationStatusAdapter(this, applicationList);
        recyclerView.setAdapter(adapter);

        // --- Firebase Setup ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        studentApplicationsRef = FirebaseDatabase.getInstance().getReference("studentApplications").child(currentUser.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchApplications();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detach the listener to prevent memory leaks when the activity is not visible
        if (valueEventListener != null) {
            studentApplicationsRef.removeEventListener(valueEventListener);
        }
    }

    private void fetchApplications() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoApplications.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicationList.clear();
                for (DataSnapshot appSnap : snapshot.getChildren()) {
                    Application application = appSnap.getValue(Application.class);
                    if (application != null) {
                        applicationList.add(application);
                    }
                }
                
                // Show newest applications first
                Collections.reverse(applicationList);

                progressBar.setVisibility(View.GONE);
                if (applicationList.isEmpty()) {
                    tvNoApplications.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoApplications.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TrackApplicationsActivity.this, "Failed to load applications.", Toast.LENGTH_SHORT).show();
            }
        };
        studentApplicationsRef.addValueEventListener(valueEventListener);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}