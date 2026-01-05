package com.example.crs2025.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ReviewApplicationsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView tvNoApplications;
    private ApplicationAdapter adapter;
    private List<Application> applicationList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review_applications);

        // Set up the toolbar
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // Initialize views
        recyclerView = findViewById(R.id.recycler_applications);
        progressBar = findViewById(R.id.progress_bar);
        tvNoApplications = findViewById(R.id.tv_no_applications);
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        applicationList = new ArrayList<>();
        adapter = new ApplicationAdapter(applicationList);
        recyclerView.setAdapter(adapter);

        fetchApplications();
    }

    private void fetchApplications() {
        progressBar.setVisibility(View.VISIBLE);
        String companyId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference applicationsRef = FirebaseDatabase.getInstance().getReference("applications").child(companyId);

        applicationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                applicationList.clear();
                for (DataSnapshot appSnap : snapshot.getChildren()) {
                    Application application = appSnap.getValue(Application.class);
                    if (application != null) {
                        applicationList.add(application);
                    }
                }

                if (applicationList.isEmpty()) {
                    tvNoApplications.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tvNoApplications.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReviewApplicationsActivity.this, "Failed to load applications.", Toast.LENGTH_SHORT).show();
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

    // --- Inner Adapter Class ---
    private class ApplicationAdapter extends RecyclerView.Adapter<ApplicationAdapter.ViewHolder> {

        private final List<Application> localDataSet;

        public ApplicationAdapter(List<Application> dataSet) {
            localDataSet = dataSet;
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_application_company_view, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Application application = localDataSet.get(position);
            holder.tvJobTitle.setText(application.getJobTitle());
            holder.tvStudentName.setText("Applicant: " + application.getFullName());
            holder.tvStatus.setText(application.getStatus());

            // Set status color
            switch (application.getStatus().toLowerCase()) {
                case "accepted":
                    holder.tvStatus.getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_accepted));
                    break;
                case "rejected":
                    holder.tvStatus.getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_rejected));
                    break;
                default: // Pending
                    holder.tvStatus.getBackground().setTint(ContextCompat.getColor(holder.itemView.getContext(), R.color.status_pending));
                    break;
            }

            holder.itemView.setOnClickListener(v -> {
                Intent intent = new Intent(ReviewApplicationsActivity.this, ApplicationDetailsForCompanyActivity.class);
                // **THE FIX**: Pass the entire object, not just the ID
                intent.putExtra("APPLICATION_DATA", (Serializable) application);
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return localDataSet.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            final TextView tvJobTitle;
            final TextView tvStudentName;
            final TextView tvStatus;

            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                tvJobTitle = itemView.findViewById(R.id.tv_job_title);
                tvStudentName = itemView.findViewById(R.id.tv_student_name);
                tvStatus = itemView.findViewById(R.id.tv_application_status);
            }
        }
    }
}