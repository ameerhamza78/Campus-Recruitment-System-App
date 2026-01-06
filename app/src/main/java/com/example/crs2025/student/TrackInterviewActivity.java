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
import com.example.crs2025.adapters.InterviewStatusAdapter;
import com.example.crs2025.models.Interview;
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

public class TrackInterviewActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private InterviewStatusAdapter adapter;
    private List<Interview> interviewList;
    private ProgressBar progressBar;
    private TextView tvNoInterviews;
    private DatabaseReference studentInterviewsRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track_interview);

        // --- Toolbar Setup ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // --- Initialize Views ---
        recyclerView = findViewById(R.id.recycler_interviews);
        progressBar = findViewById(R.id.progress_bar);
        tvNoInterviews = findViewById(R.id.tv_no_interviews);

        // --- RecyclerView Setup ---
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        interviewList = new ArrayList<>();
        adapter = new InterviewStatusAdapter(this, interviewList);
        recyclerView.setAdapter(adapter);

        // --- Firebase Setup ---
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        studentInterviewsRef = FirebaseDatabase.getInstance().getReference("studentInterviews").child(currentUser.getUid());
    }

    @Override
    protected void onStart() {
        super.onStart();
        fetchInterviews();
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Detach the listener to prevent memory leaks when the activity is not visible
        if (valueEventListener != null) {
            studentInterviewsRef.removeEventListener(valueEventListener);
        }
    }

    private void fetchInterviews() {
        progressBar.setVisibility(View.VISIBLE);
        tvNoInterviews.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interviewList.clear();
                for (DataSnapshot interviewSnap : snapshot.getChildren()) {
                    Interview interview = interviewSnap.getValue(Interview.class);
                    if (interview != null) {
                        interviewList.add(interview);
                    }
                }
                
                // Show most recent interviews first
                Collections.reverse(interviewList);

                progressBar.setVisibility(View.GONE);
                if (interviewList.isEmpty()) {
                    tvNoInterviews.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoInterviews.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(TrackInterviewActivity.this, "Failed to load interviews.", Toast.LENGTH_SHORT).show();
            }
        };
        studentInterviewsRef.addValueEventListener(valueEventListener);
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