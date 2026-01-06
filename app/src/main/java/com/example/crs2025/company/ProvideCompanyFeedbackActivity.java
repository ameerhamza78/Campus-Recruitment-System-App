package com.example.crs2025.company;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Feedback;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ProvideCompanyFeedbackActivity extends AppCompatActivity {

    private EditText etFeedback;
    private Button btnSubmitFeedback;
    private DatabaseReference feedbackRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // ** THE FIX **: Use the new, single, unified layout
        setContentView(R.layout.activity_provide_feedback);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        etFeedback = findViewById(R.id.et_feedback_text);
        btnSubmitFeedback = findViewById(R.id.btn_submit_feedback);
        mAuth = FirebaseAuth.getInstance();
        feedbackRef = FirebaseDatabase.getInstance().getReference("companyFeedbacks");

        btnSubmitFeedback.setOnClickListener(v -> submitFeedback());
    }

    private void submitFeedback() {
        String feedbackText = etFeedback.getText().toString().trim();
        if (TextUtils.isEmpty(feedbackText)) {
            Toast.makeText(this, "Please enter your feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(this, "You must be logged in to submit feedback", Toast.LENGTH_SHORT).show();
            return;
        }

        String feedbackId = feedbackRef.push().getKey();
        String authorId = currentUser.getUid();
        String authorName = currentUser.getDisplayName() != null ? currentUser.getDisplayName() : "Anonymous Company";
        long timestamp = System.currentTimeMillis();

        Feedback feedback = new Feedback(feedbackId, authorId, authorName, "company", feedbackText, timestamp);

        if (feedbackId != null) {
            feedbackRef.child(feedbackId).setValue(feedback)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(ProvideCompanyFeedbackActivity.this, "Feedback submitted successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ProvideCompanyFeedbackActivity.this, "Failed to submit feedback", Toast.LENGTH_SHORT).show());
        }
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