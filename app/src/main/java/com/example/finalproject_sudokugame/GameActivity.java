package com.example.finalproject_sudokugame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.Gravity;
import androidx.appcompat.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

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
    private String username;
    private String currentDifficulty;

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

        SharedPreferences userPrefs = getSharedPreferences("sudoku_user", MODE_PRIVATE);
        username = userPrefs.getString("username", "");

        boolean resumeGame = getIntent().getBooleanExtra("resume_game", false);

        if (resumeGame) {
            loadSavedGame();
        } else {
            currentDifficulty = getIntent().getStringExtra("difficulty_level");
            board = new GameManager(currentDifficulty,true);
            drawBoard();
        }


        game_btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerRunnable);
                finish();
            }
        });

        if (!resumeGame) {
            startTime = System.currentTimeMillis();
            handler.postDelayed(updateTimerRunnable, 1000);
        }

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
    private void endGame(boolean isWin, long elapsedMillis, boolean perfect) {
        isGameOver = true;
        handler.removeCallbacks(updateTimerRunnable);
        saveStats(isWin, elapsedMillis, perfect);
        saveCurrentGame(false);

        SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
        gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWin ? "כל הכבוד! 🎉" : "המשחק הסתיים");
        builder.setMessage(isWin ? "פתרת את הסודוקו בהצלחה!\nתחזור למסך הבית."
                : "הגעת ל־3 פסילות.\nהמשחק יסתיים ותחזור למסך הבית.");
        builder.setCancelable(false);
        builder.setPositiveButton("אישור", (dialog, which) -> finish());
        builder.show();
    }

    private void placeNumber(int number) {
        if (isGameOver) return;

        if (board.getSelectedRow() == -1 || board.getSelectedCol() == -1) {
            showMessage("בחרי תא לפני הכנסת מספר!");
            return;
        }

        if (board.tryPlaceNumber(number)) {
            drawBoard();

            if (board.isComplete()) {
                long elapsedMillis = System.currentTimeMillis() - startTime;
                boolean perfect = strikes == 0;
                endGame(true, elapsedMillis, perfect);
            }
        } else {
            addStrike();
            showMessage("מספר לא חוקי!");
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

        int leftWidth = (col == 8) ? thickBorder : thinBorder;
        int topWidth = (row == 0) ? thickBorder : thinBorder;

        int rightWidth = (col % 3 == 0) ? thickBorder : thinBorder;
        int bottomWidth = ((row + 1) % 3 == 0) ? thickBorder : thinBorder;

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
        vibrateIfEnabled();

        if (strikes >= MAX_STRIKES) {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            endGame(false, elapsedMillis, false);
        }
    }

    private String getUsernamePrefix() {
        return username.isEmpty() ? "guest_" : username + "_";
    }

    private void vibrateIfEnabled() {
        SharedPreferences prefs = getSharedPreferences("sudoku_settings", MODE_PRIVATE);
        boolean vibrationEnabled = prefs.getBoolean("vibration_enabled", true);

        if (!vibrationEnabled) return;

        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(
                        VibrationEffect.createOneShot(
                                200,
                                VibrationEffect.DEFAULT_AMPLITUDE
                        )
                );
            } else {
                vibrator.vibrate(200);
            }
        }
    }

    private void saveStats(boolean isWin, long elapsedMillis, boolean perfect) {
        String level = getIntent().getStringExtra("difficulty_level"); // easy/medium/hard
        SharedPreferences statsPrefs = getSharedPreferences("sudoku_stats", MODE_PRIVATE);
        SharedPreferences.Editor editor = statsPrefs.edit();

        String prefix = getUsernamePrefix();

        int played = statsPrefs.getInt(prefix + level + "_played", 0) + 1;
        editor.putInt(prefix + level + "_played", played);

        if (isWin) {
            int wins = statsPrefs.getInt(prefix + level + "_wins", 0) + 1;
            editor.putInt(prefix + level + "_wins", wins);

            int streak = statsPrefs.getInt(prefix + level + "_currentStreak", 0) + 1;
            editor.putInt(prefix + level + "_currentStreak", streak);

            int bestStreak = statsPrefs.getInt(prefix + level + "_bestStreak", 0);
            if (streak > bestStreak) editor.putInt(prefix + level + "_bestStreak", streak);

            if (perfect) {
                int perfectWins = statsPrefs.getInt(prefix + level + "_perfectWins", 0) + 1;
                editor.putInt(prefix + level + "_perfectWins", perfectWins);
            }

            String previousBest = statsPrefs.getString(prefix + level + "_bestTime", "--:--");
            if (previousBest.equals("--:--") || elapsedMillis < parseTime(previousBest)) {
                editor.putString(prefix + level + "_bestTime", formatTime(elapsedMillis));
            }

        } else {

            int losses = statsPrefs.getInt(prefix + level + "_losses", 0) + 1;
            editor.putInt(prefix + level + "_losses", losses);
            editor.putInt(prefix + level + "_currentStreak", 0);
        }

        editor.apply();
    }

    private long parseTime(String time) {
        // time = "mm:ss"
        String[] parts = time.split(":");
        int minutes = Integer.parseInt(parts[0]);
        int seconds = Integer.parseInt(parts[1]);
        return (minutes * 60 + seconds) * 1000L;
    }

    private String formatTime(long elapsedMillis) {
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }

    private String getBoardStateAsString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                int value = board.getCell(row, col);
                if (value < 0 || value > 9) value = 0;
                sb.append(value);
            }
        }
        String result = sb.toString();
        if (result.length() != 81) {
            throw new IllegalStateException("Board state לא חוקי!");
        }
        return result;
    }
    private void saveCurrentGame(boolean save) {
        if (username.isEmpty()) return;

        final String difficultyToSave = currentDifficulty;
        final long timerToSave = System.currentTimeMillis() - startTime;
        final int strikesToSave = strikes;
        final String boardStateToSave;
        final String initialBoardStateToSave;

        if (save) {
            if (board == null) return;
            try {
                boardStateToSave = getBoardStateAsString();
                initialBoardStateToSave = board.getOriginalBoardString();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
             boardStateToSave = "";
             initialBoardStateToSave = "";
        }

        new Thread(() -> {
            try {
                SavedGameDao dao = DataBase.getInstance(this).savedGameDao();
                if (!save) {
                    dao.deleteSavedGameForUser(username);
                } else {
                    dao.saveGame(new SavedGameEntity(
                            difficultyToSave,
                            timerToSave,
                            strikesToSave,
                            boardStateToSave,
                            initialBoardStateToSave,
                            username
                    ));
                    SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
                    gamePrefs.edit().putBoolean("hasSavedGame_" + username, true).apply();
                }
            } catch (Exception e) {
                e.printStackTrace();
                if (save) {
                     SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
                     gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();
                }
            }
        }).start();
    }


    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimerRunnable);

        if (!isGameOver) {
            saveCurrentGame(true);
        }
    }

    private void loadSavedGame() {
        gridLayout.setEnabled(false);
        new Thread(() -> {
            SavedGameEntity savedGame = DataBase.getInstance(this)
                    .savedGameDao()
                    .getSavedGameForUser(username);

            if (savedGame != null) {
                runOnUiThread(() -> {
                    try {
                        currentDifficulty = savedGame.getDifficultyLevel();
                        
                        String savedBoard = savedGame.getBoardState();
                        String savedInitial = savedGame.getInitialBoardState();
                        
                        if (savedInitial != null && !savedInitial.isEmpty() && savedInitial.length() == 81 && savedBoard.length() == 81) {
                            board = new GameManager(savedBoard, savedInitial);
                        } else if (savedBoard.length() == 81) {
                            board = new GameManager(savedBoard, false); 
                        } else {
                             throw new IllegalStateException("Invalid board length");
                        }

                        strikes = savedGame.getMistakes();
                        updateStrikesUI();
                        startTime = System.currentTimeMillis() - savedGame.getTimer();
                        drawBoard();
                        gridLayout.setEnabled(true);
                        handler.postDelayed(updateTimerRunnable, 1000);
                    } catch (Exception e) {
                         e.printStackTrace();
                         Toast.makeText(GameActivity.this, "שגיאה בטעינת נתונים: " + e.getMessage(), Toast.LENGTH_LONG).show();
                         SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
                         gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();

                         new Thread(() -> {
                             DataBase.getInstance(this).savedGameDao().deleteSavedGameForUser(username);
                         }).start();

                         handler.postDelayed(() -> finish(), 3500);
                    }
                });
            } else {
                runOnUiThread(() -> {
                    android.widget.Toast.makeText(GameActivity.this, "שגיאה בטעינת המשחק", android.widget.Toast.LENGTH_SHORT).show();
                    SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
                    gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();
                    finish();
                });
            }
        }).start();
    }
}