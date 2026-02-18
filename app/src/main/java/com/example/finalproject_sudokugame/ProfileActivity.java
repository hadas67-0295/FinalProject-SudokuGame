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

    private TextView profile_tvGuestMode;
    private Button profile_btnEasy, profile_btnMedium, profile_btnHard;
    private TextView profile_tvUserName, profile_tvGamePlayed, profile_tvGameWins,
            profile_tvGameLosses, profile_tvGameBestTime,
            profile_tvGameBestWinsStreak, profile_tvGamePerfectWins;
    private Toolbar profile_toolbar;

    private SharedPreferences statsPrefs;
    private SharedPreferences userPrefs;
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
        profile_tvGuestMode = findViewById(R.id.profile_tvGuestMode);
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

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        statsPrefs = getSharedPreferences("sudoku_stats", Context.MODE_PRIVATE);
        userPrefs = getSharedPreferences("sudoku_user", Context.MODE_PRIVATE);


        profile_btnEasy.setOnClickListener(v -> showStatsForLevel("easy", profile_btnEasy));
        profile_btnMedium.setOnClickListener(v -> showStatsForLevel("medium", profile_btnMedium));
        profile_btnHard.setOnClickListener(v -> showStatsForLevel("hard", profile_btnHard));

        updateUI();
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

        String username = userPrefs.getString("username", "");
        String prefix = username.isEmpty() ? "guest_" : username + "_";

        int played = statsPrefs.getInt(prefix + level + "_played", 0);
        int wins = statsPrefs.getInt(prefix + level + "_wins", 0);
        int losses = statsPrefs.getInt(prefix + level + "_losses", 0);
        String bestTime = statsPrefs.getString(prefix + level + "_bestTime", "--:--");
        int bestStreak = statsPrefs.getInt(prefix + level + "_bestStreak", 0);
        int perfectWins = statsPrefs.getInt(prefix + level + "_perfectWins", 0);

        profile_tvGamePlayed.setText(getString(R.string.stats_played, played));
        profile_tvGameWins.setText(getString(R.string.stats_wins, wins));
        profile_tvGameLosses.setText(getString(R.string.stats_losses, losses));
        profile_tvGameBestTime.setText(getString(R.string.stats_best_time, bestTime));
        profile_tvGameBestWinsStreak.setText(getString(R.string.stats_streak, bestStreak));
        profile_tvGamePerfectWins.setText(getString(R.string.stats_perfect, perfectWins));
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

    private void updateUI() {
        boolean isLoggedIn = userPrefs.getBoolean("is_logged_in", false);
        String username = userPrefs.getString("username", "Guest");

        if (!isLoggedIn) {
            profile_tvUserName.setText(getString(R.string.hello_guest));
            profile_tvGuestMode.setText(getString(R.string.login_to_see_stats));

            profile_tvGamePlayed.setText("");
            profile_tvGameWins.setText("");
            profile_tvGameLosses.setText("");
            profile_tvGameBestTime.setText("");
            profile_tvGameBestWinsStreak.setText("");
            profile_tvGamePerfectWins.setText("");

            profile_btnEasy.setEnabled(false);
            profile_btnMedium.setEnabled(false);
            profile_btnHard.setEnabled(false);
        } else {
            profile_tvUserName.setText(getString(R.string.hello_user, username));
            profile_tvGuestMode.setText("");

            profile_btnEasy.setEnabled(true);
            profile_btnMedium.setEnabled(true);
            profile_btnHard.setEnabled(true);

            showStatsForLevel("easy", profile_btnEasy);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }
}
