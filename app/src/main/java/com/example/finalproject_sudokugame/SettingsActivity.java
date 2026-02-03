package com.example.finalproject_sudokugame;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.audiofx.Equalizer;
import android.os.Build;
import android.os.Bundle;
import android.Manifest;
import android.os.VibrationEffect;
import android.os.Vibrator;

import android.view.MenuItem;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settings_toolbar;
    private TextView settings_tvAccount, settings_tvMusic, settings_tvNotification, settings_tvVibration;
    private Button settings_btnAccount, settings_btnMusic;
    private Switch settings_switchNotifications, settings_switchVibration;

    private static final String PREFS_NAME = "sudoku_settings";

    private static final String KEY_MUSIC = "music_enabled";
    private static final String KEY_VIBRATION = "vibration_enabled";
    private static final String KEY_NOTIFICATIONS = "notifications_enabled";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";


    private SharedPreferences settingsPrefs;
    private SharedPreferences userPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_settings);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        settings_toolbar = findViewById(R.id.settings_toolbar);
        settings_tvAccount = findViewById(R.id.settings_tvAccount);
        settings_btnAccount = findViewById(R.id.settings_btnAccount);
        settings_tvMusic = findViewById(R.id.settings_tvMusic);
        settings_btnMusic = findViewById(R.id.settings_btnMusic);
        settings_tvNotification = findViewById(R.id.settings_tvNotification);
        settings_switchNotifications = findViewById(R.id.settings_switchNotifications);
        settings_tvVibration = findViewById(R.id.settings_tvVibration);
        settings_switchVibration = findViewById(R.id.settings_switchVibration);

        setSupportActionBar(settings_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        settingsPrefs = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userPrefs = getSharedPreferences("sudoku_user", Context.MODE_PRIVATE);

        settings_switchNotifications.setChecked(settingsPrefs.getBoolean(KEY_NOTIFICATIONS, true));
        settings_switchVibration.setChecked(settingsPrefs.getBoolean(KEY_VIBRATION, true));

        settings_btnAccount.setOnClickListener(v -> showAccountDialog());

        settings_btnMusic.setOnClickListener(v -> showMusicDialog());

        settings_switchNotifications.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, isChecked).apply();

                if (isChecked) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS)
                                != PackageManager.PERMISSION_GRANTED) {

                            requestPermissions(
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    100
                            );

                            settings_switchNotifications.setChecked(false);
                            return;
                        }
                    }

                    Toast.makeText(SettingsActivity.this, "התראות הופעלו", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(SettingsActivity.this, "התראות כובו", Toast.LENGTH_SHORT).show();
                }
            }
        });

        settings_switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {

            settingsPrefs.edit().putBoolean(KEY_VIBRATION, isChecked).apply();

            if (isChecked) {
                Vibrator vibrator =
                        (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (vibrator != null && vibrator.hasVibrator()) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        vibrator.vibrate(
                                VibrationEffect.createOneShot(
                                        300,
                                        VibrationEffect.DEFAULT_AMPLITUDE
                                )
                        );
                    } else {
                        vibrator.vibrate(300);
                    }
                }

                Toast.makeText(SettingsActivity.this,
                        "רטט הופעל",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(SettingsActivity.this,
                        "רטט כובה",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
        @Override
        public boolean onOptionsItemSelected (MenuItem item){
            int id = item.getItemId();

            if (id == R.id.menu_home) {
                Intent intent = new Intent(SettingsActivity.this, HomeActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                return true;
            }
            if (id == R.id.menu_profile) {
                startActivity(new Intent(SettingsActivity.this, ProfileActivity.class));
                return true;
            }
            if (id == R.id.menu_login) {
                startActivity(new Intent(SettingsActivity.this, LoginActivity.class));
                return true;
            }
            if (id == R.id.menu_settings) {
                Toast.makeText(this, "אתה כבר במסך ההגדרות", Toast.LENGTH_SHORT).show();
                return true;
            }

            return super.onOptionsItemSelected(item);
        }

        private void showAccountDialog () {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("הגדרות חשבון");
            SharedPreferences userPrefs = getSharedPreferences("sudoku_user", MODE_PRIVATE);
            String username = userPrefs.getString("username", "");
            builder.setItems(new CharSequence[]{"התנתק"}, (dialog, which) -> {
                switch (which) {
                    case 0:
                        userPrefs.edit().putBoolean("is_logged_in", false).apply();



                        Toast.makeText(SettingsActivity.this,
                                "התנתקת מהחשבון",
                                Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        break;
                }
            });
            builder.setNegativeButton("סגור", null);
            builder.show();
        }
    private void showMusicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הגדרות מוזיקה");

        boolean musicEnabled = settingsPrefs.getBoolean(KEY_MUSIC, true);
        int checkedItem = musicEnabled ? 0 : 1;

        builder.setSingleChoiceItems(
                new CharSequence[]{"הפעל מוזיקה", "כבה מוזיקה"},
                checkedItem,
                (dialog, which) -> {

                    if (which == 0) {
                        settingsPrefs.edit().putBoolean(KEY_MUSIC, true).apply();

                        Intent musicIntent = new Intent(SettingsActivity.this, MusicService.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                            startForegroundService(musicIntent);
                        } else {
                            startService(musicIntent);
                        }

                        Toast.makeText(SettingsActivity.this,
                                "המוזיקה הופעלה",
                                Toast.LENGTH_SHORT).show();

                    } else {
                        settingsPrefs.edit().putBoolean(KEY_MUSIC, false).apply();

                        stopService(new Intent(SettingsActivity.this, MusicService.class));

                        Toast.makeText(SettingsActivity.this,
                                "המוזיקה כובתה",
                                Toast.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                });

        builder.setNegativeButton("סגור", (dialog, which) -> dialog.dismiss());

        builder.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 100) {

            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                settingsPrefs.edit().putBoolean(KEY_NOTIFICATIONS, true).apply();
                settings_switchNotifications.setChecked(true);
                Toast.makeText(this, "התראות הופעלו", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(this, "לא ניתן לשלוח התראות ללא הרשאה", Toast.LENGTH_LONG).show();
            }
        }
    }
}
