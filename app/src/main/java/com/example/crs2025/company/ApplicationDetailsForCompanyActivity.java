package com.example.crs2025.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;
import com.example.crs2025.models.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ApplicationDetailsForCompanyActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvFullName, tvEmail, tvAddress, tvBranch, tvCgpa, tvReason, tvResume, tvStatus;
    private Button btnApprove, btnReject, btnScheduleInterview, btnGoBack;

    private Application currentApplication;
    private DatabaseReference applicationRef;
    private DatabaseReference globalApplicationRef;
    private DatabaseReference studentRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details_for_company);

        initializeViews();

        currentApplication = (Application) getIntent().getSerializableExtra("APPLICATION_DATA");

        if (currentApplication == null) {
            Toast.makeText(this, "Error: Application data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up Firebase references
        applicationRef = FirebaseDatabase.getInstance().getReference("applications")
                .child(currentApplication.getCompanyId())
                .child(currentApplication.getApplicationId());
        globalApplicationRef = FirebaseDatabase.getInstance().getReference("globalApplications")
                .child(currentApplication.getApplicationId());
        // ** THE FIX **: Reference the specific student's user profile
        studentRef = FirebaseDatabase.getInstance().getReference("users").child(currentApplication.getStudentId());

        // Populate UI
        populateApplicationDetails();
        fetchAndPopulateStudentDetails();

        setupClickListeners();
    }

    private void initializeViews() {
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvFullName = findViewById(R.id.tv_full_name);
        tvEmail = findViewById(R.id.tv_email);
        tvAddress = findViewById(R.id.tv_address);
        tvBranch = findViewById(R.id.tv_branch);
        tvCgpa = findViewById(R.id.tv_cgpa);
        tvReason = findViewById(R.id.tv_reason);
        tvResume = findViewById(R.id.tv_resume);
        tvStatus = findViewById(R.id.tv_status);

        btnApprove = findViewById(R.id.btn_approve);
        btnReject = findViewById(R.id.btn_reject);
        btnScheduleInterview = findViewById(R.id.btn_schedule_interview);
        btnGoBack = findViewById(R.id.btn_go_back);
    }

    private void populateApplicationDetails() {
        tvJobTitle.setText(currentApplication.getJobTitle());
        tvFullName.setText(currentApplication.getFullName());
        tvEmail.setText(currentApplication.getEmail());
        tvReason.setText(currentApplication.getReasonToApply());
        tvResume.setText(currentApplication.getResumeLink());
        tvStatus.setText(currentApplication.getStatus());
    }

    // ** THE FIX **: New method to fetch details from the User model
    private void fetchAndPopulateStudentDetails() {
        studentRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    User student = snapshot.getValue(User.class);
                    if (student != null) {
                        tvAddress.setText(student.getAddress());
                        tvBranch.setText(student.getBranch());
                        tvCgpa.setText(student.getCgpa());
                    }
                } else {
                    Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Could not find student profile.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Failed to load student details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupClickListeners() {
        btnApprove.setOnClickListener(v -> updateApplicationStatus("Accepted"));
        btnReject.setOnClickListener(v -> updateApplicationStatus("Rejected"));
        btnGoBack.setOnClickListener(v -> finish());

        btnScheduleInterview.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicationDetailsForCompanyActivity.this, ScheduleInterviewActivity.class);
            intent.putExtra("APPLICATION_DATA", currentApplication);
            startActivity(intent);
        });
    }

    private void updateApplicationStatus(String status) {
        applicationRef.child("status").setValue(status);
        globalApplicationRef.child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Application status updated to " + status, Toast.LENGTH_SHORT).show();
                    tvStatus.setText(status);
                })
                .addOnFailureListener(e -> Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Failed to update status.", Toast.LENGTH_SHORT).show());
    }
}