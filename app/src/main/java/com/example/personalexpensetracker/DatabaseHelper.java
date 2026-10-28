
package com.example.personalexpensetracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ExpenseMate.db";
    private static final int DATABASE_VERSION = 2;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Table for storing expenses
        db.execSQL(
                "CREATE TABLE expenses (" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "title TEXT NOT NULL, " +
                        "amount REAL NOT NULL, " +
                        "category TEXT NOT NULL, " +
                        "date TEXT NOT NULL)"
        );

        // Table for storing the starting balance
        db.execSQL(
                "CREATE TABLE settings (" +
                        "id INTEGER PRIMARY KEY, " +
                        "starting_balance REAL NOT NULL)"
        );
    }

    @Override
    public void onUpgrade(
            SQLiteDatabase db, int oldVersion, int newVersion) {

        // Add the settings table to an existing database
        if (oldVersion < 2) {
            db.execSQL(
                    "CREATE TABLE IF NOT EXISTS settings (" +
                            "id INTEGER PRIMARY KEY, " +
                            "starting_balance REAL NOT NULL)"
            );
        }
    }

    // Add a new expense
    public boolean addExpense(
            String title, double amount,
            String category, String date) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("title", title);
        values.put("amount", amount);
        values.put("category", category);
        values.put("date", date);

        long result = db.insert("expenses", null, values);

        db.close();

        return result != -1;
    }

    // Get all expenses
    public Cursor getAllExpenses() {

        SQLiteDatabase db = getReadableDatabase();

        return db.rawQuery(
                "SELECT id, title, amount, category, date " +
                        "FROM expenses ORDER BY id DESC",
                null
        );
    }

    // Calculate total expenses
    public double getTotalExpenses() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT SUM(amount) FROM expenses",
                null
        );

        double total = 0;

        if (cursor.moveToFirst() && !cursor.isNull(0)) {
            total = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return total;
    }

    // Save or update the starting balance
    public boolean saveStartingBalance(double amount) {

        SQLiteDatabase db = getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("id", 1);
        values.put("starting_balance", amount);

        long result = db.insertWithOnConflict(
                "settings",
                null,
                values,
                SQLiteDatabase.CONFLICT_REPLACE
        );

        db.close();

        return result != -1;
    }

    // Get the saved starting balance
    public double getStartingBalance() {

        SQLiteDatabase db = getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT starting_balance " +
                        "FROM settings WHERE id = 1",
                null
        );

        double balance = 0;

        if (cursor.moveToFirst()) {
            balance = cursor.getDouble(0);
        }

        cursor.close();
        db.close();

        return balance;
    }
}
