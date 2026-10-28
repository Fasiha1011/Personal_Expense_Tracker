
package com.example.personalexpensetracker;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.DatePicker;
import android.view.ViewGroup;
import android.widget.LinearLayout.LayoutParams;

import java.util.Calendar;
import java.util.Locale;

public class AddExpenseActivity extends Activity {

    private EditText titleInput, amountInput;
    private Spinner categorySpinner;
    private DatePicker datePicker;
    private DatabaseHelper databaseHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        layout.setGravity(Gravity.CENTER_VERTICAL);

        TextView heading = new TextView(this);
        heading.setText("Add Expense");
        heading.setTextSize(28);
        heading.setPadding(0, 0, 0, 24);
        layout.addView(heading);

        titleInput = new EditText(this);
        titleInput.setHint("Expense title (e.g. Lunch)");
        layout.addView(titleInput);

        amountInput = new EditText(this);
        amountInput.setHint("Amount in Rs.");
        amountInput.setInputType(
                android.text.InputType.TYPE_CLASS_NUMBER
                        | android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL);
        layout.addView(amountInput);

        TextView categoryLabel = new TextView(this);
        categoryLabel.setText("Select category");
        categoryLabel.setTextSize(16);
        layout.addView(categoryLabel);

        categorySpinner = new Spinner(this);
        String[] categories = {
                "Food", "Transport", "Shopping",
                "Bills", "Education", "Health", "Other"
        };

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        adapter.setDropDownViewResource(
                android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(adapter);
        layout.addView(categorySpinner);

        TextView dateLabel = new TextView(this);
        dateLabel.setText("Select date");
        dateLabel.setTextSize(16);
        layout.addView(dateLabel);

        datePicker = new DatePicker(this);
        datePicker.setCalendarViewShown(false);
        layout.addView(datePicker, new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));

        Button saveButton = new Button(this);
        saveButton.setText("Save Expense");
        layout.addView(saveButton);

        Button backButton = new Button(this);
        backButton.setText("Back to Dashboard");
        layout.addView(backButton);

        saveButton.setOnClickListener(v -> saveExpense());
        backButton.setOnClickListener(v -> finish());

        setContentView(layout);
    }

    private void saveExpense() {
        String title = titleInput.getText().toString().trim();
        String amountText = amountInput.getText().toString().trim();

        if (title.isEmpty() || amountText.isEmpty()) {
            Toast.makeText(this,
                    "Please enter title and amount",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;

        try {
            amount = Double.parseDouble(amountText);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Enter a valid amount",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Double.isFinite(amount) || amount <= 0) {
            Toast.makeText(this, "Amount must be greater than zero",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        int year = datePicker.getYear();
        int month = datePicker.getMonth() + 1;
        int day = datePicker.getDayOfMonth();

        String date = String.format(
                Locale.US, "%04d-%02d-%02d", year, month, day);

        String category = categorySpinner.getSelectedItem().toString();

        boolean saved = databaseHelper.addExpense(
                title, amount, category, date);

        if (saved) {
            Toast.makeText(this, "Expense saved successfully!",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Could not save expense",
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            databaseHelper.close();
        }
    }
}
