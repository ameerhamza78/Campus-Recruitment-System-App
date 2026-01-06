package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.crs2025.R;
import com.example.crs2025.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etEnrollment, etBranch, etInstitute, etPassword, etConfirmPassword, etSkills, etCgpa;
    private RadioGroup roleSelectorGroup;
    private Button btnRegister;
    private LinearLayout studentFieldsLayout, addressLayout;
    private String selectedRole = "Student";

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_blue));
        setContentView(R.layout.activity_register);

        initializeViews();

        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        roleSelectorGroup.setOnCheckedChangeListener((group, checkedId) -> {
            selectedRole = (checkedId == R.id.role_student) ? "Student" : "Company";
            toggleFieldsVisibility();
        });

        btnRegister.setOnClickListener(v -> registerUser());
        toggleFieldsVisibility();
    }

    private void initializeViews() {
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etEnrollment = findViewById(R.id.et_enrollment);
        etBranch = findViewById(R.id.et_branch);
        etInstitute = findViewById(R.id.et_institute);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        etSkills = findViewById(R.id.et_skills);
        etCgpa = findViewById(R.id.et_cgpa);
        btnRegister = findViewById(R.id.btn_register);
        roleSelectorGroup = findViewById(R.id.role_selector_group);
        studentFieldsLayout = findViewById(R.id.student_fields_layout);
        addressLayout = findViewById(R.id.address_layout);
    }

    private void toggleFieldsVisibility() {
        studentFieldsLayout.setVisibility("Student".equals(selectedRole) ? View.VISIBLE : View.GONE);
        addressLayout.setVisibility("Company".equals(selectedRole) ? View.VISIBLE : View.GONE);
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || !password.equals(confirmPassword)) {
            Toast.makeText(this, "Please check all fields and ensure passwords match.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder().setDisplayName(name).build();
                        firebaseUser.updateProfile(profileUpdates).addOnCompleteListener(profileTask -> {
                            if (!profileTask.isSuccessful()) {
                                Log.w("RegisterActivity", "User profile update failed.", profileTask.getException());
                            }
                            saveUserToDatabase(firebaseUser, name, email);
                        });
                    }
                } else {
                    Log.e("RegisterActivity", "Registration failed", task.getException());
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                }
            });
    }

    private void saveUserToDatabase(FirebaseUser firebaseUser, String name, String email) {
        String userId = firebaseUser.getUid();

        if ("Student".equals(selectedRole)) {
            String enrollment = etEnrollment.getText().toString().trim();
            String branch = etBranch.getText().toString().trim();
            String institute = etInstitute.getText().toString().trim();
            String skills = etSkills.getText().toString().trim();
            String cgpa = etCgpa.getText().toString().trim();

            if (TextUtils.isEmpty(enrollment) || TextUtils.isEmpty(branch) || TextUtils.isEmpty(institute) || TextUtils.isEmpty(skills) || TextUtils.isEmpty(cgpa)) {
                Toast.makeText(this, "Please fill all student fields, including Skills and CGPA", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User(userId, name, email, "", "Student", enrollment, branch, institute, skills, cgpa);
            databaseReference.child(userId).setValue(user).addOnCompleteListener(dbTask -> handleDbWrite(dbTask.isSuccessful(), dbTask.getException()));
        } else { // Company
            String address = etAddress.getText().toString().trim();
            if (TextUtils.isEmpty(address)) {
                Toast.makeText(this, "Please fill company address", Toast.LENGTH_SHORT).show();
                return;
            }
            User user = new User(userId, name, email, address, "Company");
            databaseReference.child(userId).setValue(user).addOnCompleteListener(dbTask -> handleDbWrite(dbTask.isSuccessful(), dbTask.getException()));
        }
    }

    private void handleDbWrite(boolean isSuccess, Exception e) {
        if (isSuccess) {
            registrationSuccess();
        } else {
            Toast.makeText(RegisterActivity.this, "Database error: " + (e != null ? e.getMessage() : "Unknown error"), Toast.LENGTH_LONG).show();
        }
    }

    private void registrationSuccess() {
        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
        mAuth.signOut();
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}