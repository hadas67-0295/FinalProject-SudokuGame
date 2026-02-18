package com.example.finalproject_sudokugame;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class HomeActivity extends AppCompatActivity {

    Button home_btnInstructions;
    Button home_btnNewGame;
    Toolbar home_toolbar;
    Button home_btnContinueGame;
    private boolean isStartingActivity = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        home_btnInstructions = findViewById(R.id.home_btnInstructions);
        home_btnNewGame = findViewById(R.id.home_btnNewGame);
        home_btnContinueGame = findViewById(R.id.home_btnContinueGame);
        home_toolbar = findViewById(R.id.home_toolbar);

        setSupportActionBar(home_toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
        SharedPreferences userPrefs = getSharedPreferences("sudoku_user", MODE_PRIVATE);
        boolean isLoggedIn = userPrefs.getBoolean("is_logged_in", false);
        String username = userPrefs.getString("username", "");
        SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", Context.MODE_PRIVATE);
        boolean hasSavedGame = gamePrefs.getBoolean("hasSavedGame_" + username, false);
        home_btnContinueGame.setEnabled(isLoggedIn&&hasSavedGame);

        home_btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionsDialog();
            }
        });

        home_btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartingActivity) return;
                isStartingActivity = true;
                Intent intent = new Intent(HomeActivity.this, DifficultyActivity.class);
                startActivity(intent);
            }
        });

        home_btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStartingActivity) return;
                if (isLoggedIn&&hasSavedGame) {
                    isStartingActivity = true;
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    intent.putExtra("resume_game", true);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, getString(R.string.no_saved_game), Toast.LENGTH_SHORT).show();
                }
            }
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

        if (id == R.id.menu_profile) {
            startActivity(new Intent(HomeActivity.this, ProfileActivity.class));
            return true;
        }
        if (id == R.id.menu_login) {
            startActivity(new Intent(HomeActivity.this, LoginActivity.class));
            return true;
        }
        if (id == R.id.menu_settings) {
            startActivity(new Intent(HomeActivity.this, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void showInstructionsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle(getString(R.string.instructions_title));
        builder.setMessage(getString(R.string.instructions_text));
        builder.setPositiveButton(getString(R.string.close), null);
        builder.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
        isStartingActivity = false;
        SharedPreferences userPrefs = getSharedPreferences("sudoku_user", MODE_PRIVATE);
        boolean isLoggedIn = userPrefs.getBoolean("is_logged_in", false);
        String username = userPrefs.getString("username", "");
        SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", Context.MODE_PRIVATE);
        boolean hasSavedGame = gamePrefs.getBoolean("hasSavedGame_" + username, false);
        home_btnContinueGame.setEnabled(isLoggedIn && hasSavedGame);
    }
}