package com.example.crs2025.student;

import android.os.Bundle;
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

public class ApplicationDetailsActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvCompanyName, tvSkills, tvFullName, tvEmail, tvAddress, tvBranch, tvCgpa, tvReason, tvResume, tvStatus;
    private Button btnGoBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_application_details);

        initializeViews();

        String applicationId = getIntent().getStringExtra("applicationId");
        if (applicationId == null) {
            Toast.makeText(this, "Application ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadApplicationDetails(applicationId);

        btnGoBack.setOnClickListener(v -> finish());
    }

    private void initializeViews() {
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvCompanyName = findViewById(R.id.tv_company_name);
        tvSkills = findViewById(R.id.tv_skills);
        tvFullName = findViewById(R.id.tv_full_name);
        tvEmail = findViewById(R.id.tv_email);
        tvAddress = findViewById(R.id.tv_address);
        tvBranch = findViewById(R.id.tv_branch);
        tvCgpa = findViewById(R.id.tv_cgpa);
        tvReason = findViewById(R.id.tv_reason);
        tvResume = findViewById(R.id.tv_resume);
        tvStatus = findViewById(R.id.tv_status);
        btnGoBack = findViewById(R.id.btn_go_back);
    }

    private void loadApplicationDetails(String applicationId) {
        DatabaseReference applicationRef = FirebaseDatabase.getInstance().getReference("globalApplications").child(applicationId);
        applicationRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot applicationSnapshot) {
                Application application = applicationSnapshot.getValue(Application.class);
                if (application != null) {
                    // Populate views with data from the Application object
                    tvJobTitle.setText(application.getJobTitle());
                    tvCompanyName.setText(application.getCompanyName());
                    tvFullName.setText(application.getFullName());
                    tvEmail.setText(application.getEmail());
                    tvReason.setText(application.getReasonToApply());
                    tvResume.setText(application.getResumeLink());
                    tvStatus.setText(application.getStatus());

                    // Fetch and populate student-specific details from the User object
                    fetchAndPopulateStudentDetails(application.getStudentId());
                } else {
                    Toast.makeText(ApplicationDetailsActivity.this, "Application details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ApplicationDetailsActivity.this, "Failed to load application details.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchAndPopulateStudentDetails(String studentId) {
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(studentId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot userSnapshot) {
                User student = userSnapshot.getValue(User.class);
                if (student != null) {
                    // ** THE FIX **: Get these details from the User object, not the Application object
                    tvSkills.setText(student.getSkills());
                    tvAddress.setText(student.getAddress());
                    tvBranch.setText(student.getBranch());
                    tvCgpa.setText(student.getCgpa());
                } else {
                    Toast.makeText(ApplicationDetailsActivity.this, "Could not load student profile details.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(ApplicationDetailsActivity.this, "Failed to load student profile details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}