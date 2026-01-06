package com.example.crs2025.company;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.models.Application;
import com.example.crs2025.models.Interview;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.UUID;

public class ScheduleInterviewActivity extends AppCompatActivity {

    private TextView tvJobTitle, tvStudentName, tvSelectedDate, tvSelectedTime;
    private Button btnSelectDate, btnSelectTime, btnSchedule, btnGoBack;
    private Spinner spInterviewType;
    private TextInputEditText etVenue;

    private Application currentApplication;
    private DatabaseReference interviewsRef, studentInterviewsRef, globalAppsRef, companyAppsRef;

    private int mYear, mMonth, mDay, mHour, mMinute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schedule_interview);

        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        }

        initializeViews();

        currentApplication = (Application) getIntent().getSerializableExtra("APPLICATION_DATA");
        if (currentApplication == null) {
            Toast.makeText(this, "Error: Application data not found.", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        // Database References
        interviewsRef = FirebaseDatabase.getInstance().getReference("interviews");
        studentInterviewsRef = FirebaseDatabase.getInstance().getReference("studentInterviews");
        globalAppsRef = FirebaseDatabase.getInstance().getReference("globalApplications");
        companyAppsRef = FirebaseDatabase.getInstance().getReference("applications");


        populateDetails();
        setupListeners();
    }

    private void initializeViews() {
        tvJobTitle = findViewById(R.id.tv_job_title);
        tvStudentName = findViewById(R.id.tv_student_name);
        tvSelectedDate = findViewById(R.id.tv_selected_date);
        tvSelectedTime = findViewById(R.id.tv_selected_time);
        btnSelectDate = findViewById(R.id.btn_select_date);
        btnSelectTime = findViewById(R.id.btn_select_time);
        btnSchedule = findViewById(R.id.btn_schedule);
        btnGoBack = findViewById(R.id.btn_go_back);
        spInterviewType = findViewById(R.id.sp_interview_type);
        etVenue = findViewById(R.id.et_venue);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.interview_types, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spInterviewType.setAdapter(adapter);
    }

    private void populateDetails() {
        tvJobTitle.setText(currentApplication.getJobTitle());
        tvStudentName.setText("For: " + currentApplication.getFullName());
    }

    private void setupListeners() {
        btnSelectDate.setOnClickListener(v -> openDatePicker());
        btnSelectTime.setOnClickListener(v -> openTimePicker());
        btnSchedule.setOnClickListener(v -> scheduleInterview());
        btnGoBack.setOnClickListener(v -> finish());
    }

    private void openDatePicker() {
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, monthOfYear, dayOfMonth) -> {
            String selectedDate = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
            tvSelectedDate.setText(selectedDate);
        }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void openTimePicker() {
        final Calendar c = Calendar.getInstance();
        mHour = c.get(Calendar.HOUR_OF_DAY);
        mMinute = c.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            String selectedTime = String.format("%02d:%02d", hourOfDay, minute);
            tvSelectedTime.setText(selectedTime);
        }, mHour, mMinute, false);
        timePickerDialog.show();
    }

    private void scheduleInterview() {
        String date = tvSelectedDate.getText().toString();
        String time = tvSelectedTime.getText().toString();
        String venue = etVenue.getText().toString().trim();
        String interviewType = spInterviewType.getSelectedItem().toString();

        if (TextUtils.isEmpty(date) || date.equals("YYYY-MM-DD") || TextUtils.isEmpty(time) || time.equals("HH:MM") || TextUtils.isEmpty(venue)) {
            Toast.makeText(this, "Please select date, time, and specify a venue.", Toast.LENGTH_SHORT).show();
            return;
        }

        String interviewId = UUID.randomUUID().toString();

        Interview interview = new Interview(
                interviewId, currentApplication.getApplicationId(), currentApplication.getStudentId(),
                currentApplication.getCompanyId(), currentApplication.getJobTitle(),
                currentApplication.getFullName(), currentApplication.getCompanyName(),
                date, time, venue, interviewType, "Scheduled"
        );

        interviewsRef.child(interviewId).setValue(interview);
        studentInterviewsRef.child(currentApplication.getStudentId()).child(interviewId).setValue(interview)
                .addOnSuccessListener(aVoid -> {
                    updateApplicationStatus();
                    Toast.makeText(ScheduleInterviewActivity.this, "Interview Scheduled Successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> Toast.makeText(ScheduleInterviewActivity.this, "Failed to schedule interview.", Toast.LENGTH_SHORT).show());
    }

    private void updateApplicationStatus() {
        String companyId = currentApplication.getCompanyId();
        String studentId = currentApplication.getStudentId();
        String applicationId = currentApplication.getApplicationId();
        String status = "Interview Scheduled";

        if (companyId != null) companyAppsRef.child(companyId).child(applicationId).child("status").setValue(status);
        if (applicationId != null) globalAppsRef.child(applicationId).child("status").setValue(status);
        if (studentId != null) FirebaseDatabase.getInstance().getReference("studentApplications").child(studentId).child(applicationId).child("status").setValue(status);
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