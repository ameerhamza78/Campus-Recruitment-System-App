package com.example.crs2025.student;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;
import com.example.crs2025.models.Job;
import com.example.crs2025.models.User;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.UUID;

public class ApplyJobActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvCompanyName, tvSkills, tvCgpa;
    private EditText etReasonToApply, etResumeLink;
    private Button btnSubmitApplication;

    private Job selectedJob;
    private User student;

    private DatabaseReference applicationsRef;
    private DatabaseReference studentApplicationsRef;
    private DatabaseReference userRef;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_apply_job);

        // --- Toolbar Setup ---
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        // --- Initialize Views ---
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvSkills = findViewById(R.id.tv_skills);
        tvCgpa = findViewById(R.id.tv_cgpa);
        etReasonToApply = findViewById(R.id.et_reason_to_apply);
        etResumeLink = findViewById(R.id.et_resume_link);
        btnSubmitApplication = findViewById(R.id.btn_submit_application);

        // --- Get Data from Intent ---
        selectedJob = (Job) getIntent().getSerializableExtra("JOB_DATA");
        if (selectedJob == null) {
            Toast.makeText(this, "Error: Job data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // --- Firebase Setup ---
        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You need to be logged in to apply.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        String studentId = currentUser.getUid();
        userRef = FirebaseDatabase.getInstance().getReference("users").child(studentId);
        applicationsRef = FirebaseDatabase.getInstance().getReference("applications");
        studentApplicationsRef = FirebaseDatabase.getInstance().getReference("studentApplications");

        // --- Populate UI and Fetch Student Data ---
        populateJobDetails();
        fetchStudentData();

        // --- Set Click Listener ---
        btnSubmitApplication.setOnClickListener(v -> submitApplication());
    }

    private void populateJobDetails() {
        tvJobTitle.setText(selectedJob.getJobTitle());
        tvCompanyName.setText(selectedJob.getCompanyName());
        tvSkills.setText("Skills Required: " + selectedJob.getSkills());
        tvCgpa.setText("Minimum CGPA: " + selectedJob.getCgpa());
    }

    private void fetchStudentData() {
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                student = snapshot.getValue(User.class);
                if (student == null) {
                    Toast.makeText(ApplyJobActivity.this, "Could not fetch student profile.", Toast.LENGTH_SHORT).show();
                    btnSubmitApplication.setEnabled(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(ApplyJobActivity.this, "Database error while fetching profile.", Toast.LENGTH_SHORT).show();
                btnSubmitApplication.setEnabled(false);
            }
        });
    }

    private void submitApplication() {
        String reason = etReasonToApply.getText().toString().trim();
        String resumeLink = etResumeLink.getText().toString().trim();

        if (TextUtils.isEmpty(reason) || TextUtils.isEmpty(resumeLink)) {
            Toast.makeText(this, "Please fill all application fields.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (student == null) {
            Toast.makeText(this, "Student profile not loaded yet. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            double studentCgpa = Double.parseDouble(student.getCgpa());
            double requiredCgpa = Double.parseDouble(selectedJob.getCgpa());
            if (studentCgpa < requiredCgpa) {
                new AlertDialog.Builder(this)
                    .setTitle("CGPA Requirement Not Met")
                    .setMessage("Your CGPA (" + studentCgpa + ") is below the minimum required for this job (" + requiredCgpa + ").")
                    .setPositiveButton("OK", null)
                    .show();
                return;
            }
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Your profile CGPA is invalid. Please update it.", Toast.LENGTH_LONG).show();
            return;
        }

        String applicationId = UUID.randomUUID().toString();
        String studentId = student.getUserId();

        Application application = new Application(
                applicationId,
                studentId,
                selectedJob.getJobId(),
                selectedJob.getCompanyId(),
                selectedJob.getJobTitle(),
                selectedJob.getCompanyName(),
                student.getSkills(),
                student.getName(),
                student.getEmail(),
                student.getAddress(),
                student.getBranch(),
                student.getCgpa(),
                reason,
                resumeLink,
                "Pending"
        );

        // Save to company-specific list
        applicationsRef.child(selectedJob.getCompanyId()).child(applicationId).setValue(application);

        // Save to student-specific list
        studentApplicationsRef.child(studentId).child(applicationId).setValue(application)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ApplyJobActivity.this, "Application Submitted Successfully!", Toast.LENGTH_LONG).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ApplyJobActivity.this, "Failed to submit application: " + e.getMessage(), Toast.LENGTH_LONG).show());
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