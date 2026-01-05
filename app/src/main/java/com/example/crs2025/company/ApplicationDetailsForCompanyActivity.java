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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ApplicationDetailsForCompanyActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvFullName, tvEmail, tvAddress, tvBranch, tvCgpa, tvReason, tvResume, tvStatus;
    private Button btnApprove, btnReject, btnScheduleInterview, btnGoBack;

    private Application currentApplication;
    private DatabaseReference applicationRef;
    private DatabaseReference globalApplicationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details_for_company);

        // Initialize Views
        initializeViews();

        // Get the entire Application object passed from the previous screen
        currentApplication = (Application) getIntent().getSerializableExtra("APPLICATION_DATA");

        if (currentApplication == null) {
            Toast.makeText(this, "Error: Application data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Set up the correct Firebase references
        applicationRef = FirebaseDatabase.getInstance().getReference("applications")
                .child(currentApplication.getCompanyId())
                .child(currentApplication.getApplicationId());
        globalApplicationRef = FirebaseDatabase.getInstance().getReference("globalApplications")
                .child(currentApplication.getApplicationId());

        // Populate the UI with the data we already have
        populateApplicationDetails();

        // Set up button listeners
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
        btnScheduleInterview = findViewById(R.id.btn_schedule_interview); // Find the new button
        btnGoBack = findViewById(R.id.btn_go_back);
    }

    private void populateApplicationDetails() {
        tvJobTitle.setText(currentApplication.getJobTitle());
        tvFullName.setText(currentApplication.getFullName());
        tvEmail.setText(currentApplication.getEmail());
        tvAddress.setText(currentApplication.getAddress());
        tvBranch.setText(currentApplication.getBranch());
        tvCgpa.setText(currentApplication.getCgpa());
        tvReason.setText(currentApplication.getReasonToApply());
        tvResume.setText(currentApplication.getResumeLink());
        tvStatus.setText(currentApplication.getStatus());
    }

    private void setupClickListeners() {
        btnApprove.setOnClickListener(v -> updateApplicationStatus("Accepted"));
        btnReject.setOnClickListener(v -> updateApplicationStatus("Rejected"));
        btnGoBack.setOnClickListener(v -> finish());

        // ** THE FIX **: Add the click listener for our new button
        btnScheduleInterview.setOnClickListener(v -> {
            Intent intent = new Intent(ApplicationDetailsForCompanyActivity.this, ScheduleInterviewActivity.class);
            // Pass the complete Application object to the next screen
            intent.putExtra("APPLICATION_DATA", currentApplication);
            startActivity(intent);
        });
    }

    private void updateApplicationStatus(String status) {
        // Update the status in both database locations
        applicationRef.child("status").setValue(status);
        globalApplicationRef.child("status").setValue(status)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Application status updated to " + status, Toast.LENGTH_SHORT).show();
                    tvStatus.setText(status); // Update the UI immediately
                })
                .addOnFailureListener(e -> Toast.makeText(ApplicationDetailsForCompanyActivity.this, "Failed to update status.", Toast.LENGTH_SHORT).show());
    }
}