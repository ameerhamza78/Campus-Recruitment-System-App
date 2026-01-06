package com.example.crs2025.company;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Interview;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PreviousInterviewsActivity extends AppCompatActivity {

    private ListView lvInterviews;
    private TextView tvNoInterviews;
    private Button btnGoBack;

    private List<Interview> interviewList = new ArrayList<>();
    private List<String> interviewDisplayList = new ArrayList<>();
    private ArrayAdapter<String> adapter;

    private DatabaseReference interviewsRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_previous_interviews);

        lvInterviews = findViewById(R.id.lv_interviews);
        tvNoInterviews = findViewById(R.id.tv_no_interviews);
        btnGoBack = findViewById(R.id.btn_go_back);

        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, interviewDisplayList);
        lvInterviews.setAdapter(adapter);

        mAuth = FirebaseAuth.getInstance();
        String companyId = mAuth.getCurrentUser().getUid();

        interviewsRef = FirebaseDatabase.getInstance().getReference("interviews");

        fetchPreviousInterviews(companyId);

        lvInterviews.setOnItemClickListener((parent, view, position, id) -> {
            Interview selectedInterview = interviewList.get(position);
            Intent intent = new Intent(PreviousInterviewsActivity.this, InterviewDetailsActivity.class);
            intent.putExtra("interviewId", selectedInterview.getInterviewId());
            startActivity(intent);
        });

        btnGoBack.setOnClickListener(v -> finish());
    }

    private void fetchPreviousInterviews(String companyId) {
        interviewsRef.orderByChild("companyId").equalTo(companyId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                interviewList.clear();
                interviewDisplayList.clear();

                if (snapshot.exists()) {
                    for (DataSnapshot interviewSnap : snapshot.getChildren()) {
                        Interview interview = interviewSnap.getValue(Interview.class);
                        if (interview != null) {
                            interviewList.add(interview);
                            interviewDisplayList.add(interview.getJobTitle() + " - " + interview.getStudentName() + " (" + interview.getInterviewDate() + ")");
                        }
                    }
                }

                if (interviewList.isEmpty()) {
                    tvNoInterviews.setVisibility(View.VISIBLE);
                } else {
                    tvNoInterviews.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PreviousInterviewsActivity.this, "Failed to load interviews.", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
