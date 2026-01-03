package com.example.crs2025.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.example.crs2025.R;

public class SplashActivity extends AppCompatActivity {

    private static final int SPLASH_DELAY = 2500; // 2.5 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Handler to start the MainActivity after a delay
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            // Create an Intent to start MainActivity
            Intent intent = new Intent(SplashActivity.this, MainActivity.class);
            startActivity(intent);

            // Close this activity
            finish();
        }, SPLASH_DELAY);
    }
}