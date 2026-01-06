package com.example.crs2025.company;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Interview;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class InterviewDetailsActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvStudentName, tvDate, tvTime, tvVenue, tvInterviewType;
    private Button btnGoBack;
    private DatabaseReference interviewRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_interview_details);

        tvJobTitle = findViewById(R.id.tv_job_title);
        tvStudentName = findViewById(R.id.tv_student_name);
        tvDate = findViewById(R.id.tv_date);
        tvTime = findViewById(R.id.tv_time);
        tvVenue = findViewById(R.id.tv_venue);
        tvInterviewType = findViewById(R.id.tv_interview_type);
        btnGoBack = findViewById(R.id.btn_go_back);

        String interviewId = getIntent().getStringExtra("interviewId");
        if (interviewId == null) {
            Toast.makeText(this, "Interview ID is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        interviewRef = FirebaseDatabase.getInstance().getReference("interviews").child(interviewId);

        loadInterviewDetails();

        btnGoBack.setOnClickListener(v -> finish());
    }

    private void loadInterviewDetails() {
        interviewRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Interview interview = snapshot.getValue(Interview.class);
                    if (interview != null) {
                        tvJobTitle.setText(interview.getJobTitle());
                        tvStudentName.setText(interview.getStudentName());
                        tvDate.setText(interview.getInterviewDate());
                        tvTime.setText(interview.getInterviewTime());
                        tvVenue.setText(interview.getLocation());
                        tvInterviewType.setText(interview.getInterviewType());
                    }
                } else {
                    Toast.makeText(InterviewDetailsActivity.this, "Interview details not found.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(InterviewDetailsActivity.this, "Failed to load interview details.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
