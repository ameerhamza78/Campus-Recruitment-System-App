package com.example.crs2025.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.crs2025.R;
import com.example.crs2025.adapters.JobSearchAdapter;
import com.example.crs2025.models.Job;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SearchJobActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private JobSearchAdapter adapter;
    private List<Job> jobList;
    private EditText etSearch;
    private ProgressBar progressBar;
    private TextView tvNoJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_job);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        recyclerView = findViewById(R.id.recycler_jobs);
        etSearch = findViewById(R.id.et_search_jobs);
        progressBar = findViewById(R.id.progress_bar);
        tvNoJobs = findViewById(R.id.tv_no_jobs);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        jobList = new ArrayList<>();
        adapter = new JobSearchAdapter(this, jobList);
        recyclerView.setAdapter(adapter);

        fetchJobs();

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) { adapter.getFilter().filter(s); }
            @Override public void afterTextChanged(Editable s) {}
        });
    }

    private void fetchJobs() {
        progressBar.setVisibility(View.VISIBLE);
        DatabaseReference jobsRef = FirebaseDatabase.getInstance().getReference("globalJobs");
        jobsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Job> fullList = new ArrayList<>();
                for (DataSnapshot jobSnap : snapshot.getChildren()) {
                    Job job = jobSnap.getValue(Job.class);
                    if (job != null) {
                        fullList.add(job);
                    }
                }
                adapter.updateFullList(fullList);
                progressBar.setVisibility(View.GONE);

                if (fullList.isEmpty()) {
                    tvNoJobs.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoJobs.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(SearchJobActivity.this, "Failed to load jobs.", Toast.LENGTH_SHORT).show();
            }
        });
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