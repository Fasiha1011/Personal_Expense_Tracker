
package com.example.personalexpensetracker;

import android.app.Activity;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.Locale;

public class CategoriesActivity extends Activity {

    private DatabaseHelper databaseHelper;
    private LinearLayout categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        databaseHelper = new DatabaseHelper(this);

        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(24, 24, 24, 24);

        TextView heading = new TextView(this);
        heading.setText("Expense Categories");
        heading.setTextSize(26);
        root.addView(heading);

        ScrollView scrollView = new ScrollView(this);

        categoryList = new LinearLayout(this);
        categoryList.setOrientation(LinearLayout.VERTICAL);
        scrollView.addView(categoryList);

        root.addView(scrollView, new LinearLayout.LayoutParams(
                -1, 0, 1));

        Button backButton = new Button(this);
        backButton.setText("Back to Dashboard");
        backButton.setOnClickListener(v -> finish());
        root.addView(backButton);

        setContentView(root);
        loadCategories();
    }

    private void loadCategories() {
        categoryList.removeAllViews();

        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT category, SUM(amount), COUNT(*) " +
                        "FROM expenses GROUP BY category ORDER BY category",
                null
        );

        double grandTotal = 0;

        try {
            if (cursor.moveToFirst()) {
                do {
                    String category = cursor.getString(0);
                    double total = cursor.getDouble(1);
                    int count = cursor.getInt(2);

                    grandTotal += total;

                    TextView item = new TextView(this);
                    item.setText(String.format(
                            Locale.US,
                            "%s\nTotal: Rs. %.2f\nTransactions: %d",
                            category, total, count
                    ));
                    item.setTextSize(18);
                    item.setPadding(8, 20, 8, 20);

                    categoryList.addView(item);

                } while (cursor.moveToNext());

                TextView totalText = new TextView(this);
                totalText.setText(String.format(
                        Locale.US,
                        "All Categories Total: Rs. %.2f",
                        grandTotal
                ));
                totalText.setTextSize(20);
                totalText.setPadding(8, 24, 8, 24);
                categoryList.addView(totalText);

            } else {
                TextView empty = new TextView(this);
                empty.setText("No expenses saved yet.");
                empty.setTextSize(18);
                categoryList.addView(empty);
            }
        } finally {
            cursor.close();
            db.close();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        databaseHelper.close();
    }
}
