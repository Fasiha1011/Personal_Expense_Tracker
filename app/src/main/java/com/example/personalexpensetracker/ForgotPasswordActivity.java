package com.example.personalexpensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class ForgotPasswordActivity extends Activity {

    EditText recoveryEmailInput;
    Button recoverButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        recoveryEmailInput = findViewById(R.id.recoveryEmailInput);
        recoverButton = findViewById(R.id.recoverButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        recoverButton.setOnClickListener(v -> {
            String email = recoveryEmailInput.getText()
                    .toString().trim();

            if (email.isEmpty()) {
                showMessage("Please enter your email");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage("Enter a valid email address");
            } else {
                SharedPreferences prefs = getSharedPreferences(
                        "ExpenseMatePrefs", MODE_PRIVATE);

                String savedEmail = prefs.getString("email", "");

                if (email.equalsIgnoreCase(savedEmail)) {
                    Intent intent = new Intent(
                            ForgotPasswordActivity.this,
                            ResetPasswordActivity.class);
                    intent.putExtra("email", email);
                    startActivity(intent);
                } else {
                    showMessage("No registered account found for this email");
                }
            }
        });

        backToLoginButton.setOnClickListener(v -> finish());
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
