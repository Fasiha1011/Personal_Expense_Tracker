package com.example.personalexpensetracker;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {

    private EditText emailInput;
    private EditText passwordInput;
    private Button loginButton;
    private Button registerButton;
    private TextView forgotButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        forgotButton = findViewById(R.id.forgotButton);

        loginButton.setOnClickListener(v -> {
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();

            if (email.isEmpty() || password.isEmpty()) {
                showMessage("Enter email and password");
                return;
            }

            if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage("Enter a valid email address");
                return;
            }

            SharedPreferences prefs = getSharedPreferences(
                    "ExpenseMatePrefs", MODE_PRIVATE);

            String savedEmail = prefs.getString("email", "");
            String savedPassword = prefs.getString("password", "");

            if (!savedEmail.isEmpty()
                    && email.equalsIgnoreCase(savedEmail)
                    && password.equals(savedPassword)) {

                showMessage("Login successful!");

                Intent intent = new Intent(
                        LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();

            } else {
                showMessage("Incorrect email or password");
            }
        });

        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });

        forgotButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
