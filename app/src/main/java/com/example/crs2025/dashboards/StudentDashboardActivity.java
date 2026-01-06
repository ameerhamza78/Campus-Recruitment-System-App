package com.example.crs2025.dashboards;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import com.example.crs2025.R;
import com.example.crs2025.activities.LoginActivity;
import com.example.crs2025.student.ProvideStudentFeedbackActivity;
import com.example.crs2025.student.SearchJobActivity;
import com.example.crs2025.student.TrackApplicationsActivity;
import com.example.crs2025.student.TrackInterviewActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StudentDashboardActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dashboard);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Welcome Message
        TextView tvWelcome = findViewById(R.id.tv_welcome_student);
        if (currentUser != null && currentUser.getDisplayName() != null && !currentUser.getDisplayName().isEmpty()) {
            tvWelcome.setText("Welcome, " + currentUser.getDisplayName());
        } else {
            tvWelcome.setText("Student Dashboard");
        }

        // Find the new cards and the logout button
        CardView cardSearchJobs = findViewById(R.id.card_search_jobs);
        CardView cardTrackApplications = findViewById(R.id.card_track_applications);
        CardView cardTrackInterviews = findViewById(R.id.card_track_interviews);
        CardView cardProvideFeedback = findViewById(R.id.card_provide_feedback);
        Button btnLogout = findViewById(R.id.btn_logout);

        // Set click listeners for the cards
        cardSearchJobs.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, SearchJobActivity.class)));

        cardTrackApplications.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, TrackApplicationsActivity.class)));

        cardTrackInterviews.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, TrackInterviewActivity.class)));

        cardProvideFeedback.setOnClickListener(v ->
                startActivity(new Intent(StudentDashboardActivity.this, ProvideStudentFeedbackActivity.class)));

        // Set click listener for the logout button
        btnLogout.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Logout")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton(android.R.string.yes, (dialog, which) -> {
                        mAuth.signOut();
                        Toast.makeText(StudentDashboardActivity.this, "Logged out successfully", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(StudentDashboardActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton(android.R.string.no, null)
                    .setIcon(R.drawable.ic_logout)
                    .show();
        });
    }
}