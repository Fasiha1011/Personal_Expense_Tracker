
package com.example.personalexpensetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends Activity {

    private TextView balanceText;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        balanceText = findViewById(R.id.balanceText);
        databaseHelper = new DatabaseHelper(this);

        LinearLayout addExpenseCard =
                findViewById(R.id.addExpenseCard);

        LinearLayout transactionsCard =
                findViewById(R.id.transactionsCard);

        LinearLayout categoriesCard =
                findViewById(R.id.categoriesCard);

        LinearLayout profileCard =
                findViewById(R.id.profileCard);

        Button setBalanceButton =
                findViewById(R.id.setBalanceButton);

        Button logoutButton =
                findViewById(R.id.logoutButton);

        // Open Add Expense screen
        addExpenseCard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    AddExpenseActivity.class
            );
            startActivity(intent);
        });

        // Open Transactions screen
        transactionsCard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    TransactionsActivity.class
            );
            startActivity(intent);
        });

        // Open Categories screen
        categoriesCard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    CategoriesActivity.class
            );
            startActivity(intent);
        });

        // Open Profile screen
        profileCard.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    ProfileActivity.class
            );
            startActivity(intent);
        });

        // Set starting balance
        setBalanceButton.setOnClickListener(v -> showBalanceDialog());

        // Logout
        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LoginActivity.class
            );

            intent.setFlags(
                    Intent.FLAG_ACTIVITY_NEW_TASK
                            | Intent.FLAG_ACTIVITY_CLEAR_TASK
            );

            startActivity(intent);
            finish();
        });
    }

    // Display a box to enter the starting balance
    private void showBalanceDialog() {

        EditText amountInput = new EditText(this);
        amountInput.setHint("e.g. 40000");
        amountInput.setInputType(
                InputType.TYPE_CLASS_NUMBER
                        | InputType.TYPE_NUMBER_FLAG_DECIMAL
        );

        double currentBalance = databaseHelper.getStartingBalance();

        if (currentBalance > 0) {
            amountInput.setText(
                    String.valueOf(currentBalance)
            );
            amountInput.setSelection(amountInput.length());
        }

        int padding = (int) (20 * getResources()
                .getDisplayMetrics().density);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(padding, padding / 2, padding, 0);
        layout.addView(amountInput);

        new AlertDialog.Builder(this)
                .setTitle("Set Starting Balance")
                .setMessage("Enter how much money you currently have (Rs.).")
                .setView(layout)
                .setPositiveButton("Save", (dialog, which) -> {

                    String input = amountInput.getText()
                            .toString().trim();

                    if (input.isEmpty()) {
                        Toast.makeText(
                                MainActivity.this,
                                "Please enter an amount.",
                                Toast.LENGTH_SHORT
                        ).show();
                        return;
                    }

                    try {
                        double amount = Double.parseDouble(input);

                        if (!Double.isFinite(amount) || amount < 0) {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Enter a valid non-negative amount.",
                                    Toast.LENGTH_SHORT
                            ).show();
                            return;
                        }

                        boolean saved =
                                databaseHelper.saveStartingBalance(amount);

                        if (saved) {
                            updateBalance();

                            Toast.makeText(
                                    MainActivity.this,
                                    "Starting balance saved!",
                                    Toast.LENGTH_SHORT
                            ).show();
                        } else {
                            Toast.makeText(
                                    MainActivity.this,
                                    "Could not save the balance.",
                                    Toast.LENGTH_SHORT
                            ).show();
                        }

                    } catch (NumberFormatException e) {
                        Toast.makeText(
                                MainActivity.this,
                                "Please enter a valid number.",
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBalance();
    }

    // Remaining balance = starting balance - expenses
    private void updateBalance() {

        double startingBalance =
                databaseHelper.getStartingBalance();

        double totalExpenses =
                databaseHelper.getTotalExpenses();

        double remainingBalance =
                startingBalance - totalExpenses;

        balanceText.setText(String.format(
                Locale.US,
                "Rs. %.2f",
                remainingBalance
        ));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
