package com.example.finalproject_sudokugame;

import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GameActivity extends AppCompatActivity {

    private GameManager board;
    private GridLayout gridLayout;
    private TextView timerTextView, game_tvResponse;
    Button game_btnReturnHome;
    private static final int MAX_STRIKES = 3;
    private int strikes = 0;
    private boolean isGameOver = false;
    private TextView game_tvStrikes;

    Handler handler = new Handler();
    long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game_btnReturnHome = findViewById(R.id.game_btnReturnHome);
        gridLayout = findViewById(R.id.game_gridLayout);
        timerTextView = findViewById(R.id.game_tvTimer);
        game_tvResponse = findViewById(R.id.game_tvResponse);
        game_tvStrikes = findViewById(R.id.game_tvStrikes);
        updateStrikesUI();

        String difficulty = getIntent().getStringExtra("difficulty_level");
        board = new GameManager(difficulty);

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
            btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    placeNumber(finalNum);
                }
            });
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
        if (isGameOver) return;
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
                cell.setText(value == 0 ? "" : String.valueOf(value));

                if (value != 0) {
                    cell.setEnabled(false);

                    if (board.isOriginalCell(row, col)) {
                        cell.setTextColor(Color.BLACK);
                    } else {
                        cell.setTextColor(Color.BLUE);
                    }
                } else {
                    cell.setText("");
                    int finalI = row;
                    int finalJ = col;
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            board.selectCell(finalI, finalJ);
                            GameActivity.this.drawBoard();
                        }
                    });
                }
                gridLayout.addView(cell);
            }
        }
    }

    private void placeNumber(int number) {

        if (isGameOver) return;

        if (board.tryPlaceNumber(number)) {
            drawBoard();

            if (board.isComplete()) {
                isGameOver = true;
                handler.removeCallbacks(updateTimerRunnable);
                showMessage("כל הכבוד! פתרת את הלוח!");
            }

        } else {
            addStrike();
            showMessage("מספר לא חוקי או לא נבחר תא!");
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }

    private Drawable createCellBackground(int row, int col, boolean isSelected) {
        float density = getResources().getDisplayMetrics().density;

        int thinBorder = (int) (1 * density);
        int thickBorder = (int) (3 * density);

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

    private void showMessage(String message) {
        game_tvResponse.setText(message);
        game_tvResponse.setVisibility(View.VISIBLE);

        game_tvResponse.postDelayed(new Runnable() {
            @Override
            public void run() {
                game_tvResponse.setVisibility(View.GONE);
            }
        }, 3000);
    }
    private void updateStrikesUI() {
        game_tvStrikes.setText(strikes + "/" + MAX_STRIKES);
    }
    private void addStrike() {
        strikes++;
        updateStrikesUI();

        if (strikes >= MAX_STRIKES) {
            endGame();
        }
    }

    private void endGame() {
        isGameOver = true;
        handler.removeCallbacks(updateTimerRunnable);
        showEndGameDialog(false);
    }


    private void showEndGameDialog(boolean isWin) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        if (isWin) {
            builder.setTitle("כל הכבוד! 🎉");
            builder.setMessage("פתרת את הסודוקו בהצלחה!\nתחזור למסך הבית.");
        } else {
            builder.setTitle("המשחק הסתיים");
            builder.setMessage("הגעת ל־3 פסילות.\nהמשחק יסתיים ותחזור למסך הבית.");
        }

        builder.setCancelable(false);

        builder.setPositiveButton("אישור", (dialog, which) -> {
            dialog.dismiss();
            finish();
        });
        builder.show();
    }


}