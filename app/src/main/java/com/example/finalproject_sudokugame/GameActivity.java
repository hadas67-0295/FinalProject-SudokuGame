package com.example.finalproject_sudokugame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
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

                cell.setTextSize(24);
                cell.setGravity(Gravity.CENTER);

                boolean isSelected =
                        row == board.getSelectedRow() &&
                                col == board.getSelectedCol();

                Drawable background = createCellBackground(row, col, isSelected);
                cell.setBackground(background);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(0, 0, 0, 0);
                cell.setLayoutParams(params);

                int value = board.getCell(row, col);
                if (value != 0) {
                    cell.setText(String.valueOf(value));
                    cell.setEnabled(false);
                    cell.setTextColor(Color.BLACK);
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

    private Drawable createCellBackground(int row, int col, boolean isSelected) {
        float density = getResources().getDisplayMetrics().density;

        int thinBorder = (int) (1 * density);  // 1dp - קו דק
        int thickBorder = (int) (3 * density); // 3dp - קו עבה

        int leftWidth   = (col == 8) ? thickBorder : thinBorder;
        int topWidth    = (row == 0) ? thickBorder : thinBorder;

        int rightWidth  = (col % 3 == 0) ? thickBorder : thinBorder;
        int bottomWidth = ((row+1) % 3 == 0) ? thickBorder : thinBorder;


        GradientDrawable leftBorder = new GradientDrawable();
        leftBorder.setColor(Color.BLACK);

        GradientDrawable topBorder = new GradientDrawable();
        topBorder.setColor(Color.BLACK);

        GradientDrawable rightBorder = new GradientDrawable();
        rightBorder.setColor(Color.BLACK);

        GradientDrawable bottomBorder = new GradientDrawable();
        bottomBorder.setColor(Color.BLACK);

        GradientDrawable center = new GradientDrawable();
        center.setColor(isSelected
                ? Color.parseColor("#90CAF9")
                : Color.WHITE);

        Drawable[] layers = {leftBorder, topBorder, rightBorder, bottomBorder, center};
        LayerDrawable layerDrawable = new LayerDrawable(layers);

        layerDrawable.setLayerInset(0, 0, 0, 0, 0);
        layerDrawable.setLayerInset(1, 0, 0, 0, 0);
        layerDrawable.setLayerInset(2, 0, 0, 0, 0);
        layerDrawable.setLayerInset(3, 0, 0, 0, 0);
        layerDrawable.setLayerInset(4, leftWidth, topWidth, rightWidth, bottomWidth);

        return layerDrawable;
    }
}