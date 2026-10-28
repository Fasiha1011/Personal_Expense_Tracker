package com.example.personalexpensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends Activity {

    EditText nameInput, emailInput;
    EditText passwordInput, confirmPasswordInput;
    Button registerButton, backToLoginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        nameInput = findViewById(R.id.nameInput);
        emailInput = findViewById(R.id.emailInput);
        passwordInput = findViewById(R.id.passwordInput);
        confirmPasswordInput = findViewById(R.id.confirmPasswordInput);
        registerButton = findViewById(R.id.registerButton);
        backToLoginButton = findViewById(R.id.backToLoginButton);

        registerButton.setOnClickListener(v -> {
            String name = nameInput.getText().toString().trim();
            String email = emailInput.getText().toString().trim();
            String password = passwordInput.getText().toString();
            String confirm = confirmPasswordInput.getText().toString();

            if (name.isEmpty() || email.isEmpty()
                    || password.isEmpty() || confirm.isEmpty()) {
                showMessage("Please fill in all fields");
            } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                showMessage("Enter a valid email");
            } else if (password.length() < 6) {
                showMessage("Password must be at least 6 characters");
            } else if (!password.equals(confirm)) {
                showMessage("Passwords do not match");
            } else {
                SharedPreferences prefs = getSharedPreferences(
                        "ExpenseMatePrefs", MODE_PRIVATE);

                prefs.edit()
                        .putString("name", name)
                        .putString("email", email)
                        .putString("password", password)
                        .apply();

                showMessage("Registration successful!");
                startActivity(new Intent(
                        RegisterActivity.this, LoginActivity.class));
                finish();
            }
        });

        backToLoginButton.setOnClickListener(v -> {
            finish();
        });
    }

    private void showMessage(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
