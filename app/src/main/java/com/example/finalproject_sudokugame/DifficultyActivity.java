package com.example.finalproject_sudokugame;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DifficultyActivity extends AppCompatActivity {
    Button difficulty_btnEasy;
    Button difficulty_btnMedium;
    Button difficulty_btnHard;
    Button difficulty_btnReturnHome;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_difficulty);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        difficulty_btnEasy = findViewById(R.id.profile_btnEasy);
        difficulty_btnMedium = findViewById(R.id.profile_btnMedium);
        difficulty_btnHard = findViewById(R.id.profile_btnHard);
        difficulty_btnReturnHome = findViewById(R.id.difficulty_btnReturnHome);

        difficulty_btnEasy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithDifficulty("easy");
            }
        });

        difficulty_btnMedium.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithDifficulty("medium");
            }
        });

        difficulty_btnHard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGameWithDifficulty("hard");
            }
        });

        difficulty_btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DifficultyActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void startGameWithDifficulty(String difficulty) {
        Intent intent = new Intent(DifficultyActivity.this, GameActivity.class);
        intent.putExtra("difficulty_level", difficulty);
        startActivity(intent);
        finish();
    }
}