package com.example.finalproject_sudokugame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SettingsActivity extends AppCompatActivity {

    private Toolbar settings_toolbar;
    private TextView settings_tvAccount, settings_tvMusic, settings_tvNotification, settings_tvVibration;
    private Button settings_btnAccount, settings_btnMusic;
    private Switch settings_switchNotifications, settings_switchVibration;

    private SharedPreferences prefs;

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

        prefs = getSharedPreferences("sudoku_settings", Context.MODE_PRIVATE);

        settings_switchNotifications.setChecked(prefs.getBoolean("notifications_enabled", true));
        settings_switchVibration.setChecked(prefs.getBoolean("vibration_enabled", true));

        settings_btnAccount.setOnClickListener(v -> showAccountDialog());

        settings_btnMusic.setOnClickListener(v -> showMusicDialog());

        settings_switchNotifications.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("notifications_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "התראות הופעלו" : "התראות כובו", Toast.LENGTH_SHORT).show();
        });

        settings_switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> {
            prefs.edit().putBoolean("vibration_enabled", isChecked).apply();
            Toast.makeText(this, isChecked ? "רטט הופעל" : "רטט כובה", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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

    private void showAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הגדרות חשבון");
        builder.setItems(new CharSequence[]{"התנתק"}, (dialog, which) -> {
            SharedPreferences.Editor editor = prefs.edit();
            switch (which) {
                case 0:
                    editor.remove("username");
                    editor.remove("password");
                    editor.apply();
                    Toast.makeText(this, "התנתקת מהחשבון", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
        builder.setNegativeButton("סגור", null);
        builder.show();
    }

    private void showMusicDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("הגדרות מוזיקה");
        builder.setMultiChoiceItems(new CharSequence[]{"הפעל מוזיקה", "כבה מוזיקה"}, null, (dialog, which, isChecked) -> {
            SharedPreferences.Editor editor = prefs.edit();
            if (which == 0 && isChecked) {
                editor.putBoolean("music_enabled", true);
                Toast.makeText(this, "המוזיקה הופעלה", Toast.LENGTH_SHORT).show();
            } else if (which == 1 && isChecked) {
                editor.putBoolean("music_enabled", false);
                Toast.makeText(this, "המוזיקה כובתה", Toast.LENGTH_SHORT).show();
            }
            editor.apply();
        });
        builder.setPositiveButton("סגור", null);
        builder.show();
    }
}
