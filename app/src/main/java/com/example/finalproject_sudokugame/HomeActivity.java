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
    Button home_btnContinueGame;
    Toolbar home_toolbar;

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
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        SharedPreferences prefs = getSharedPreferences("sudoku_game", Context.MODE_PRIVATE);
        boolean hasSavedGame = prefs.getBoolean("hasSavedGame", false);
        home_btnContinueGame.setEnabled(hasSavedGame);

        home_btnInstructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showInstructionsDialog();
            }
        });

        home_btnNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeActivity.this, DifficultyActivity.class);
                startActivity(intent);
            }
        });

        home_btnContinueGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasSavedGame) {
                    Intent intent = new Intent(HomeActivity.this, GameActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(HomeActivity.this, "אין משחק שמור להמשיך", Toast.LENGTH_SHORT).show();
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
        builder.setTitle("הוראות המשחק");
        builder.setMessage(
                "ברוכים הבאים למשחק הסודוקו!\n\n" +
                        "מטרת המשחק היא למלא את כל לוח הסודוקו במספרים מ-1 עד 9, כך שיתקיימו שלושת הכללים הבאים:\n\n" +
                        "1. כל שורה חייבת להכיל את המספרים 1–9 ללא כפילויות.\n" +
                        "2. כל עמודה חייבת להכיל את המספרים 1–9 ללא כפילויות.\n" +
                        "3. כל ריבוע 3×3 חייב להכיל את המספרים 1–9 ללא כפילויות.\n\n" +
                        "טיפים לפתרון:\n" +
                        "• התחילו מהתאים שמספר האפשרויות בהם קטן.\n" +
                        "• חפשו מספרים שחייבים להופיע בגלל חסימות של שורות/עמודות סמוכות.\n" +
                        "• אל תפחדו לנחש – אבל עשו זאת בחכמה.\n\n" +
                        "בהצלחה!");
        builder.setPositiveButton("סגור", null);
        builder.show();
    }
}