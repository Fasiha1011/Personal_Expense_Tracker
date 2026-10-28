package com.example.personalexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ResetPasswordActivity extends Activity {

    EditText newPasswordInput, confirmNewPasswordInput;
    Button resetButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        newPasswordInput = findViewById(R.id.newPasswordInput);
        confirmNewPasswordInput = findViewById(R.id.confirmNewPasswordInput);
        resetButton = findViewById(R.id.resetButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        resetButton.setOnClickListener(v -> {
            String newPassword =
                    newPasswordInput.getText().toString().trim();
            String confirmPassword =
                    confirmNewPasswordInput.getText().toString().trim();

            if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "Fill in both password fields",
                        Toast.LENGTH_SHORT).show();
            } else if (newPassword.length() < 6) {
                Toast.makeText(this, "Password must be at least 6 characters",
                        Toast.LENGTH_SHORT).show();
            } else if (!newPassword.equals(confirmPassword)) {
                Toast.makeText(this, "Passwords do not match",
                        Toast.LENGTH_SHORT).show();
            } else {
                String email = getIntent().getStringExtra("email");

                SharedPreferences prefs =
                        getSharedPreferences("ExpenseMatePrefs", MODE_PRIVATE);

                String savedEmail = prefs.getString("email", "");

                if (email == null || !email.equals(savedEmail)
                        || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    Toast.makeText(this, "Please restart password recovery",
                            Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                prefs.edit()
                        .putString("password", newPassword)
                        .apply();

                Toast.makeText(this, "Password reset successfully!",
                        Toast.LENGTH_SHORT).show();

                Intent intent = new Intent(
                        ResetPasswordActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        backToLoginButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ResetPasswordActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}
