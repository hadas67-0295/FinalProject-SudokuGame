package com.example.finalproject_sudokugame;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Random;

public class SplashActivity extends AppCompatActivity {
    TextView splash_tvGameTitle;
    TextView splash_tvMotivationSentence;
    private static final int SPLASH_DURATION = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        splash_tvGameTitle = findViewById(R.id.splash_tvGameTitle);
        splash_tvMotivationSentence = findViewById(R.id.splash_tvMotivationSentence);

        int[] motivationResIds = {
                R.string.motivation_1,
                R.string.motivation_2,
                R.string.motivation_3,
                R.string.motivation_4
        };

        Random random = new Random();
        int index = random.nextInt(motivationResIds.length);
        splash_tvMotivationSentence.setText(getString(motivationResIds[index]));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, SPLASH_DURATION);
    }
}