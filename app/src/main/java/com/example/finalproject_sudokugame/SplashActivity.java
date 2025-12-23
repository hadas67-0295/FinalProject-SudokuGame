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
    String[] motivationSentences={
        "כל מספר במקום הנכון מקרב אותך לניצחון",
                "אל תוותר – פתרון הסודוקו מחכה לך",
                "ככל שתתרגל יותר, תהפוך למאסטר של סודוקו",
                "תשקיע מחשבה ותהנה מהאתגר"
    };
    final int Splash_DURATION = 2000;
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

        Random random = new Random();
        int index = random.nextInt(motivationSentences.length);
        splash_tvMotivationSentence.setText(motivationSentences[index]);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        }, Splash_DURATION);
    }
}