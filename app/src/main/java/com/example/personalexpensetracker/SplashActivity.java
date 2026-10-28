package com.example.personalexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

public class SplashActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Button getStartedButton = findViewById(R.id.getStartedButton);

        getStartedButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    SplashActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
