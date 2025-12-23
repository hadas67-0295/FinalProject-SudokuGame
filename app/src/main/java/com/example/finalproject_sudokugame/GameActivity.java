package com.example.finalproject_sudokugame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameManager board;
    private GridLayout gridLayout;
    private TextView timerTextView;
    Button game_btnReturnHome;

    Handler handler = new Handler();
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game_btnReturnHome = findViewById(R.id.game_btnReturnHome);
        gridLayout = findViewById(R.id.game_gridLayout);
        timerTextView = findViewById(R.id.game_tvTimer);

        board = new GameManager();
        String difficulty = getIntent().getStringExtra("difficulty_level");

        game_btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerRunnable);
                finish();
            }
        });

        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimerRunnable, 1000);

        drawBoard();

        for (int num = 1; num <= 9; num++) {
            int buttonId = getResources().getIdentifier("game_btn" + num, "id", getPackageName());
            Button btn = findViewById(buttonId);
            int finalNum = num;
            btn.setOnClickListener(v -> placeNumber(finalNum));
        }
    }

    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            long seconds = (elapsedMillis / 1000) % 60;
            long minutes = (elapsedMillis / 1000) / 60;

            timerTextView.setText(String.format("זמן: %02d:%02d", minutes, seconds));

            handler.postDelayed(this, 1000);
        }
    };

    private void drawBoard() {
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(9);
        gridLayout.setRowCount(9);

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextView cell = new TextView(this);
                EditText cellBorder = new EditText(this);

                cell.setTextSize(24);
                cell.setGravity(Gravity.CENTER);
                int left,top,right,bottom;
                if(col%3==0){
                    left =3;
                }
                else{
                    left =1;
                }
                if(row%3==0){
                    top =3;
                }
                else{
                    top =1;
                }
                if(col==8||(col+1)%3==0){
                    right =3;
                }
                else{
                    right =1;
                }
                if(row==8||(row+1)%3==0){
                    bottom =3;
                }
                else{
                    bottom =1;
                }
                GradientDrawable border = new GradientDrawable();
                border.setColor(Color.WHITE);
                border.setStroke(1,Color.parseColor("#CCCCCC"));
                border = createCellBorder(row, col);

                if (row == board.getSelectedRow() && col == board.getSelectedCol()) {
                    border.setColor(getResources().getColor(android.R.color.holo_blue_light));
                } else {
                    border.setColor(Color.WHITE);
                }

                cell.setBackground(border);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(2, 2, 2, 2);
                cell.setLayoutParams(params);

                int value = board.getCell(row, col);
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setEnabled(false);
                } else {
                    cell.setText("");
                    int finalI = row;
                    int finalJ = col;
                    cell.setOnClickListener(v -> {
                        board.selectCell(finalI, finalJ);
                        drawBoard();
                    });
                }
                gridLayout.addView(cell);
            }
        }
    }

    private void placeNumber(int number) {
        if (board.tryPlaceNumber(number)) {
            drawBoard();
            if (board.isComplete()) {
                Toast.makeText(this, "כל הכבוד! פתרת את הלוח!", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "מספר לא חוקי או לא נבחר תא!", Toast.LENGTH_SHORT).show();
        }

        for (int num = 1; num <= 9; num++) {
            int buttonId = getResources().getIdentifier("game_btn" + num, "id", getPackageName());
            Button btn = findViewById(buttonId);
            btn.setBackgroundResource(android.R.drawable.btn_default);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }

    private GradientDrawable createCellBorder(int row, int col) {
        GradientDrawable border = new GradientDrawable();
        border.setColor(Color.WHITE);

        // עובי הגבול תלוי במיקום התא
        // גבול עבה (3-4dp) בקצוות בלוקים 3x3
        // גבול דק (1dp) בין תאים רגילים
        int strokeWidth;
        int color = Color.BLACK;

        // קביעת עובי הגבול
        if (row % 3 == 0 || col % 3 == 0 || row == 8 || col == 8) {
            strokeWidth = 3; // גבול עבה
        } else {
            strokeWidth = 1; // גבול דק
        }

        border.setStroke(strokeWidth, color);
        return border;
    }
}
