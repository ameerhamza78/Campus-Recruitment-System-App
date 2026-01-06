package com.example.crs2025.company;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.crs2025.R;
import com.example.crs2025.models.Job;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.UUID;

public class PostJobActivity extends AppCompatActivity {

    private EditText etJobTitle, etSkills, etCgpa, etJobType, etLocation;
    private CheckBox cbInternship;
    private Button btnPostJob;
    private DatabaseReference jobsRef, globalJobsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_job);

        etJobTitle = findViewById(R.id.et_job_title);
        etSkills = findViewById(R.id.et_skills); 
        etCgpa = findViewById(R.id.et_cgpa);
        etJobType = findViewById(R.id.et_job_type);
        etLocation = findViewById(R.id.et_location);
        cbInternship = findViewById(R.id.cb_internship);
        btnPostJob = findViewById(R.id.btn_post_job);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "Authentication error", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        String companyId = currentUser.getUid();
        jobsRef = FirebaseDatabase.getInstance().getReference("jobs").child(companyId);
        globalJobsRef = FirebaseDatabase.getInstance().getReference("globalJobs");

        btnPostJob.setOnClickListener(v -> postJob());
    }

    private void postJob() {
        String jobTitle = etJobTitle.getText().toString().trim();
        String skills = etSkills.getText().toString().trim();
        String cgpa = etCgpa.getText().toString().trim();
        String jobType = etJobType.getText().toString().trim();
        String location = etLocation.getText().toString().trim();
        boolean isInternship = cbInternship.isChecked();

        if (TextUtils.isEmpty(jobTitle) || TextUtils.isEmpty(skills) || TextUtils.isEmpty(cgpa) || TextUtils.isEmpty(jobType) || TextUtils.isEmpty(location)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        String companyId = currentUser.getUid();
        String companyName = currentUser.getDisplayName();
        String jobId = UUID.randomUUID().toString();

        Job job = new Job(jobId, companyId, companyName, jobTitle, skills, cgpa, jobType, isInternship, location);

        jobsRef.child(jobId).setValue(job);
        globalJobsRef.child(jobId).setValue(job)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(PostJobActivity.this, "Job posted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(PostJobActivity.this, "Failed to post job", Toast.LENGTH_SHORT).show());
    }
}