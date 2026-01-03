package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;
import com.example.crs2025.dashboards.AdminDashboardActivity;
import com.example.crs2025.dashboards.CompanyDashboardActivity;
import com.example.crs2025.dashboards.StudentDashboardActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private RadioGroup roleSelectorGroup;
    private Button btnLogin;
    private String selectedRole = "Admin"; // Default role

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        roleSelectorGroup = findViewById(R.id.role_selector_group);
        btnLogin = findViewById(R.id.btn_login);

        mAuth = FirebaseAuth.getInstance();

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

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedRole.equals("Admin") && email.equals("admin@admin.com") && password.equals("admin123")) {
            startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
            finish();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            switch (selectedRole) {
                                case "Student":
                                    startActivity(new Intent(LoginActivity.this, StudentDashboardActivity.class));
                                    break;
                                case "Company":
                                    startActivity(new Intent(LoginActivity.this, CompanyDashboardActivity.class));
                                    break;
                            }
                            finish();
                        }
                    } else {
                        Toast.makeText(this, "Login failed. Please try again.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
