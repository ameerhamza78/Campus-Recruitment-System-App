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

import com.example.crs2025.R;
import com.example.crs2025.models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText etName, etEmail, etAddress, etEnrollment, etBranch, etInstitute, etPassword, etConfirmPassword;
    private RadioGroup roleSelectorGroup;
    private Button btnRegister;
    private LinearLayout studentFieldsLayout, addressLayout;
    private String selectedRole = "Student"; // Default role

    private DatabaseReference databaseReference;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Views
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etAddress = findViewById(R.id.et_address);
        etEnrollment = findViewById(R.id.et_enrollment);
        etBranch = findViewById(R.id.et_branch);
        etInstitute = findViewById(R.id.et_institute);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);
        btnRegister = findViewById(R.id.btn_register);

        roleSelectorGroup = findViewById(R.id.role_selector_group);
        studentFieldsLayout = findViewById(R.id.student_fields_layout);
        addressLayout = findViewById(R.id.address_layout);


        // Firebase Instances
        databaseReference = FirebaseDatabase.getInstance().getReference("users");
        mAuth = FirebaseAuth.getInstance();

        // Set up listener for the new visual role selector
        roleSelectorGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.role_student) {
                selectedRole = "Student";
            } else if (checkedId == R.id.role_company) {
                selectedRole = "Company";
            }
            toggleFieldsVisibility();
        });


        // Register button action
        btnRegister.setOnClickListener(v -> registerUser());

        // Set initial visibility
        toggleFieldsVisibility();
    }

    private void toggleFieldsVisibility() {
        if (selectedRole.equals("Student")) {
            studentFieldsLayout.setVisibility(View.VISIBLE);
            addressLayout.setVisibility(View.GONE);
        } else { // Company
            studentFieldsLayout.setVisibility(View.GONE);
            addressLayout.setVisibility(View.VISIBLE);
        }
    }

    private void registerUser() {
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // Basic validation for common fields
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Please fill all required fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String userId = firebaseUser.getUid();
                            User user;

                            if (selectedRole.equals("Student")) {
                                String enrollment = etEnrollment.getText().toString().trim();
                                String branch = etBranch.getText().toString().trim();
                                String institute = etInstitute.getText().toString().trim();

                                if (TextUtils.isEmpty(enrollment) || TextUtils.isEmpty(branch) || TextUtils.isEmpty(institute)) {
                                    Toast.makeText(this, "Please fill all student fields", Toast.LENGTH_SHORT).show();
                                    return;
                                }

                                // For student, we pass a null or empty address.
                                user = new User(userId, name, email, "", "Student", enrollment, branch, institute, password);
                                databaseReference.child(userId).setValue(user);

                            } else { // Company
                                String address = etAddress.getText().toString().trim();
                                if (TextUtils.isEmpty(address)) {
                                    Toast.makeText(this, "Please fill company address", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                // For company, student-specific fields are null.
                                user = new User(userId, name, email, address, "Company", null, null, null, password);
                                databaseReference.child(userId).setValue(user);
                            }

                            Toast.makeText(this, "Registration Successful", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
                            finish();
                        }
                    } else {
                        Log.e("RegisterActivity", "Registration failed", task.getException());
                        Toast.makeText(this, "Registration failed. " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
