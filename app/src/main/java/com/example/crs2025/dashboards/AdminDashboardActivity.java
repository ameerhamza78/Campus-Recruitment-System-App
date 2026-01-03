package com.example.crs2025.dashboards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.crs2025.R;
import com.example.crs2025.activities.LoginActivity;
import com.example.crs2025.admin.ManageApplicationsActivity;
import com.example.crs2025.admin.ManageCompaniesActivity;
import com.example.crs2025.admin.ManageFeedbacksActivity;
import com.example.crs2025.admin.ManageInterviewsActivity;
import com.example.crs2025.admin.ManageJobsActivity;
import com.example.crs2025.admin.ManageStudentsActivity;

public class AdminDashboardActivity extends AppCompatActivity {

    private CardView cardManageStudents, cardManageCompanies, cardManageApplications, cardManageJobs, cardManageInterviews, cardManageFeedbacks;
    private Button btnLogout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dashboard);

        // Initialize CardViews
        cardManageStudents = findViewById(R.id.card_manage_students);
        cardManageCompanies = findViewById(R.id.card_manage_companies);
        cardManageApplications = findViewById(R.id.card_manage_applications);
        cardManageJobs = findViewById(R.id.card_manage_jobs);
        cardManageInterviews = findViewById(R.id.card_manage_interviews);
        cardManageFeedbacks = findViewById(R.id.card_manage_feedbacks);
        btnLogout = findViewById(R.id.btn_logout);

        // Set click listeners for all cards
        cardManageStudents.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageStudentsActivity.class)));
        cardManageCompanies.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageCompaniesActivity.class)));
        cardManageApplications.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageApplicationsActivity.class)));
        cardManageJobs.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageJobsActivity.class)));
        cardManageInterviews.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageInterviewsActivity.class)));
        cardManageFeedbacks.setOnClickListener(v -> startActivity(new Intent(AdminDashboardActivity.this, ManageFeedbacksActivity.class)));

        btnLogout.setOnClickListener(v -> {
            // Logout logic
            Intent intent = new Intent(AdminDashboardActivity.this, LoginActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        });
    }
}