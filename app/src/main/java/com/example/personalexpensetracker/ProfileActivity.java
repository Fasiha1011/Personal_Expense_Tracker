package com.example.personalexpensetracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ProfileActivity extends Activity {

    private static final int PICK_IMAGE_REQUEST = 1001;

    private ImageView profileImage;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        TextView profileName = findViewById(R.id.profileName);
        TextView profileEmail = findViewById(R.id.profileEmail);

        Button backButton = findViewById(R.id.backButton);
        Button logoutButton = findViewById(R.id.profileLogoutButton);
        Button changePhotoButton = findViewById(R.id.changePhotoButton);
        Button removePhotoButton = findViewById(R.id.removePhotoButton);

        profileImage = findViewById(R.id.profileImage);

        prefs = getSharedPreferences("ExpenseMatePrefs", MODE_PRIVATE);

        String name = prefs.getString("name", "ExpenseMate User");
        String email = prefs.getString("email", "No email saved");

        profileName.setText(name);
        profileEmail.setText(email);

        loadProfilePhoto();

        changePhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");

            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        removePhotoButton.setOnClickListener(v -> {
            new AlertDialog.Builder(ProfileActivity.this)
                    .setTitle("Remove Profile Photo")
                    .setMessage("Are you sure you want to remove your photo?")
                    .setNegativeButton("Cancel", (dialog, which) -> {
                        dialog.dismiss();
                    })
                    .setPositiveButton("Remove", (dialog, which) -> {
                        prefs.edit().remove("profilePhoto").apply();

                        profileImage.setImageResource(
                                android.R.drawable.ic_menu_myplaces
                        );

                        Toast.makeText(
                                ProfileActivity.this,
                                "Profile photo removed",
                                Toast.LENGTH_SHORT
                        ).show();
                    })
                    .show();
        });

        backButton.setOnClickListener(v -> finish());

        logoutButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    ProfileActivity.this,
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

    private void loadProfilePhoto() {
        String savedPhoto = prefs.getString("profilePhoto", null);

        if (savedPhoto != null) {
            try {
                profileImage.setImageURI(Uri.parse(savedPhoto));
            } catch (Exception e) {
                profileImage.setImageResource(
                        android.R.drawable.ic_menu_myplaces
                );
            }
        } else {
            profileImage.setImageResource(
                    android.R.drawable.ic_menu_myplaces
            );
        }
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data
    ) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST
                && resultCode == RESULT_OK
                && data != null
                && data.getData() != null) {

            Uri imageUri = data.getData();

            try {
                int takeFlags = data.getFlags()
                        & Intent.FLAG_GRANT_READ_URI_PERMISSION;

                getContentResolver().takePersistableUriPermission(
                        imageUri,
                        takeFlags
                );

                profileImage.setImageURI(imageUri);

                prefs.edit()
                        .putString("profilePhoto", imageUri.toString())
                        .apply();

                Toast.makeText(
                        this,
                        "Profile photo saved!",
                        Toast.LENGTH_SHORT
                ).show();

            } catch (Exception e) {
                Toast.makeText(
                        this,
                        "Unable to save photo. Please try another image.",
                        Toast.LENGTH_LONG
                ).show();
            }
        }
    }
}
