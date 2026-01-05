package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.crs2025.R;
import com.example.crs2025.dashboards.AdminDashboardActivity;
import com.example.crs2025.dashboards.CompanyDashboardActivity;
import com.example.crs2025.dashboards.StudentDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private RadioGroup roleSelectorGroup;
    private Button btnLogin;
    private FirebaseAuth mAuth;
    private String selectedRole = "Admin"; // Default role

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.brand_blue));

        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        roleSelectorGroup = findViewById(R.id.role_selector_group);
        btnLogin = findViewById(R.id.btn_login);

        roleSelectorGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.role_admin) {
                selectedRole = "Admin";
            } else if (checkedId == R.id.role_student) {
                selectedRole = "Student";
            } else if (checkedId == R.id.role_company) {
                selectedRole = "Company";
            }
        });

        btnLogin.setOnClickListener(v -> loginUser());
    }

    private void loginUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Please enter email and password", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        navigateToDashboard();
                    } else {
                        // ** THE FIX **: Get the specific error message from Firebase
                        String errorMessage = "Authentication failed.";
                        if (task.getException() != null) {
                            errorMessage += " " + task.getException().getMessage();
                            Log.e("LoginActivity", "Login Error", task.getException());
                        }
                        Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void navigateToDashboard() {
        Intent intent;
        switch (selectedRole) {
            case "Admin":
                intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
                break;
            case "Student":
                intent = new Intent(LoginActivity.this, StudentDashboardActivity.class);
                break;
            case "Company":
                intent = new Intent(LoginActivity.this, CompanyDashboardActivity.class);
                break;
            default:
                return; // Should not happen
        }
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}