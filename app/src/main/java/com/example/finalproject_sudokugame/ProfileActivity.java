package com.example.finalproject_sudokugame;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ProfileActivity extends AppCompatActivity {

    private ImageView profile_imageProfile;
    private Button profile_btnEasy, profile_btnMedium, profile_btnHard;
    private TextView profile_tvUserName, profile_tvGamePlayed, profile_tvGameWins,
            profile_tvGameLosses, profile_tvGameBestTime,
            profile_tvGameBestWinsStreak, profile_tvGamePerfectWins;
    private Toolbar profile_toolbar;

    private SharedPreferences prefs;
    private int easyOriginalColor, mediumOriginalColor, hardOriginalColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        profile_imageProfile = findViewById(R.id.profile_imageProfile);
        profile_btnEasy = findViewById(R.id.profile_btnEasy);
        profile_btnMedium = findViewById(R.id.profile_btnMedium);
        profile_btnHard = findViewById(R.id.profile_btnHard);
        profile_tvUserName = findViewById(R.id.profile_tvUserName);
        profile_tvGamePlayed = findViewById(R.id.profile_tvGamePlayed);
        profile_tvGameWins = findViewById(R.id.profile_tvGameWins);
        profile_tvGameLosses = findViewById(R.id.profile_tvGameLosses);
        profile_tvGameBestTime = findViewById(R.id.profile_tvGameBestTime);
        profile_tvGameBestWinsStreak = findViewById(R.id.profile_tvGameBestWinsStreak);
        profile_tvGamePerfectWins = findViewById(R.id.profile_tvGamePerfectWins);
        profile_toolbar = findViewById(R.id.profile_toolbar);

        easyOriginalColor = ViewCompat.getBackgroundTintList(profile_btnEasy) != null ?
                ViewCompat.getBackgroundTintList(profile_btnEasy).getDefaultColor() :
                ContextCompat.getColor(this, R.color.profile_button_default);

        mediumOriginalColor = ViewCompat.getBackgroundTintList(profile_btnMedium) != null ?
                ViewCompat.getBackgroundTintList(profile_btnMedium).getDefaultColor() :
                ContextCompat.getColor(this, R.color.profile_button_default);

        hardOriginalColor = ViewCompat.getBackgroundTintList(profile_btnHard) != null ?
                ViewCompat.getBackgroundTintList(profile_btnHard).getDefaultColor() :
                ContextCompat.getColor(this,R.color.profile_button_default);

        setSupportActionBar(profile_toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        prefs = getSharedPreferences("sudoku_stats", Context.MODE_PRIVATE);
        String username = prefs.getString("username", "אורח");
        profile_tvUserName.setText("שלום, " + username);

        profile_btnEasy.setOnClickListener(v -> showStatsForLevel("easy", profile_btnEasy));
        profile_btnMedium.setOnClickListener(v -> showStatsForLevel("medium", profile_btnMedium));
        profile_btnHard.setOnClickListener(v -> showStatsForLevel("hard", profile_btnHard));
    }

    private void showStatsForLevel(String level, Button selectedButton) {
        int defaultColor = ContextCompat.getColor(this, R.color.profile_button_default);

        Button[] buttons = { profile_btnEasy, profile_btnMedium, profile_btnHard };
        for (Button btn : buttons) {
            if (btn == selectedButton) {
                if (btn == profile_btnEasy) ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(easyOriginalColor));
                if (btn == profile_btnMedium) ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(mediumOriginalColor));
                if (btn == profile_btnHard) ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(hardOriginalColor));
            } else {
                ViewCompat.setBackgroundTintList(btn, ColorStateList.valueOf(defaultColor));
            }
        }

        int played = prefs.getInt(level + "_played", 0);
        int wins = prefs.getInt(level + "_wins", 0);
        int losses = prefs.getInt(level + "_losses", 0);
        String bestTime = prefs.getString(level + "_bestTime", "--:--");
        int bestStreak = prefs.getInt(level + "_bestStreak", 0);
        int perfectWins = prefs.getInt(level + "_perfectWins", 0);

        profile_tvGamePlayed.setText("משחקים: " + played);
        profile_tvGameWins.setText("ניצחונות: " + wins);
        profile_tvGameLosses.setText("הפסדים: " + losses);
        profile_tvGameBestTime.setText("הזמן הטוב ביותר: " + bestTime);
        profile_tvGameBestWinsStreak.setText("רצף ניצחונות: " + bestStreak);
        profile_tvGamePerfectWins.setText("ניצחונות מושלמים: " + perfectWins);
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
            Intent intent = new Intent(ProfileActivity.this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            return true;
        }
        if (id == R.id.menu_login) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(ProfileActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
