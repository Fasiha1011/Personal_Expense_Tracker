
package com.example.personalexpensetracker;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

public class TransactionsActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout listLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);

        TextView heading = new TextView(this);
        heading.setText("Transaction History");
        heading.setTextSize(26);
        root.addView(heading);

        ScrollView scrollView = new ScrollView(this);
        listLayout = new LinearLayout(this);
        listLayout.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(listLayout);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                -1, 0, 1));

        Button backButton = new Button(this);
        backButton.setText("Back to Dashboard");
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(root);
        loadExpenses();
    }

    private void loadExpenses() {
        listLayout.removeAllViews();

        Cursor cursor = databaseHelper.getAllExpenses();
        double total = 0;

        try {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(1);
                    double amount = cursor.getDouble(2);
                    String category = cursor.getString(3);
                    String date = cursor.getString(4);

                    total += amount;

                    TextView item = new TextView(this);
                    item.setText(
                            title + "\nRs. " + amount
                                    + " | " + category + "\n" + date);
                    item.setTextSize(18);
                    item.setPadding(8, 20, 8, 20);
                    listLayout.addView(item);
                } while (cursor.moveToNext());

                TextView totalText = new TextView(this);
                totalText.setText(
                        "Total Expenses: Rs. " + total);
                totalText.setTextSize(20);
                totalText.setPadding(8, 20, 8, 20);
                listLayout.addView(totalText);

            } else {
                TextView empty = new TextView(this);
                empty.setText("No expenses saved yet.");
                empty.setTextSize(18);
                listLayout.addView(empty);
            }
        } finally {
            cursor.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
