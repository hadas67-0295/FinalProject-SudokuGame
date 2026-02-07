package com.example.finalproject_sudokugame;


import android.content.Context;
import android.content.DialogInterface;
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
import android.app.ProgressDialog;

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

    private enum HighlightType {
        SELECTED,
        SAME_NUMBER,
        RELATED,
        NONE
    }

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

        currentDifficulty = getIntent().getStringExtra("difficulty_level");
        boolean resumeGame = getIntent().getBooleanExtra("resume_game", false);

        if (resumeGame) {
            loadSavedGame();
        } else {
            initializeGameWithAi();
        }

        game_btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerRunnable);
                finish();
            }
        });


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

    private void initializeGameWithAi() {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("מייצר לוח עם AI...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        int emptyCells;
        if (currentDifficulty.equalsIgnoreCase("easy")) {
            emptyCells = 20;
        } else if (currentDifficulty.equalsIgnoreCase("medium")) {
            emptyCells = 40;
        } else {
            emptyCells = 55;
        }

        SudokuAiService aiService = new SudokuAiService(getString(R.string.google_api_key));
        aiService.generateSudoku(emptyCells, new SudokuAiService.SudokuCallback() {
            @Override
            public void onSuccess(int[][] puzzle, int[][] solution) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    board = new GameManager(puzzle, solution);
                    startTime = System.currentTimeMillis();
                    handler.postDelayed(updateTimerRunnable, 1000);
                    drawBoard();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    progressDialog.dismiss();
                    Toast.makeText(GameActivity.this, "AI Error: " + error + ". Falling back to local generation.", Toast.LENGTH_LONG).show();

                    board = new GameManager(currentDifficulty, true);
                    startTime = System.currentTimeMillis();
                    handler.postDelayed(updateTimerRunnable, 1000);
                    drawBoard();
                });
            }
        });
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
        if (isGameOver)
            return;
        gridLayout.removeAllViews();
        gridLayout.setColumnCount(9);
        gridLayout.setRowCount(9);

        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();
        int selectedValue = (selectedRow >= 0 && selectedCol >= 0)
                ? board.getCell(selectedRow, selectedCol)
                : 0;

        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                TextView cell = new TextView(this);
                cell.setTextSize(24);
                cell.setGravity(Gravity.CENTER);

                HighlightType highlightType = getHighlightType(row, col, selectedRow, selectedCol, selectedValue);

                Drawable background = createCellBackground(row, col, highlightType);
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
                    int finalRow = row;
                    int finalCol = col;
                    cell.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            board.selectCell(finalRow, finalCol);
                            GameActivity.this.drawBoard();
                        }
                    });
                }
                gridLayout.addView(cell);
            }
        }
    }

    private HighlightType getHighlightType(int row, int col, int selectedRow, int selectedCol, int selectedValue) {
        if (selectedRow < 0 || selectedCol < 0) {
            return HighlightType.NONE;
        }

        if (row == selectedRow && col == selectedCol) {
            return HighlightType.SELECTED;
        }

        int cellValue = board.getCell(row, col);
        if (cellValue != 0 && selectedValue != 0 && cellValue == selectedValue) {
            return HighlightType.SAME_NUMBER;
        }

        if (row == selectedRow || col == selectedCol) {
            return HighlightType.RELATED;
        }

        int boxRowStart = (selectedRow / 3) * 3;
        int boxColStart = (selectedCol / 3) * 3;
        if (row >= boxRowStart && row < boxRowStart + 3 &&
                col >= boxColStart && col < boxColStart + 3) {
            return HighlightType.RELATED;
        }

        return HighlightType.NONE;
    }

    private void endGame(boolean isWin, long elapsedMillis, boolean perfect) {
        isGameOver = true;
        handler.removeCallbacks(updateTimerRunnable);
        saveStats(isWin, elapsedMillis, perfect);
        saveCurrentGame(false);

        SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
        gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();

        if (isWin) {
            animateVictory();
            Handler endHandler = new Handler();
            endHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEndDialog(isWin);
                }
            }, 2500);
        } else {
            for (int i = 0; i < 25; i++) {
                int delay = i * 200;
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridLayout.setTranslationX(20);
                    }
                }, delay);

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        gridLayout.setTranslationX(-20);
                    }
                }, delay + 100);
            }
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    gridLayout.setTranslationX(0);
                }
            }, 2500);

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    showEndDialog(isWin);
                }
            }, 5000);
        }
    }

    private void showEndDialog(boolean isWin) {
        if (isFinishing() || isDestroyed()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWin ? "כל הכבוד! 🎉" : "המשחק הסתיים");
        builder.setMessage(isWin ? "פתרת את הסודוקו בהצלחה!\nתחזור למסך הבית."
                : "הגעת ל־3 פסילות.\nהמשחק יסתיים ותחזור למסך הבית.");
        builder.setCancelable(false);
        builder.setPositiveButton("אישור", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }

    private void animateVictory() {
        int rows = 9;
        int cols = 9;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View cell = gridLayout.getChildAt(i);
            int row = i / cols;
            int col = i % cols;
            long delay = (row + col) * 100L;

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    startManualJump(cell);
                }
            }, delay);
        }
    }

    private void startManualJump(View view) {
        final Handler jumpHandler = new Handler();
        final int jumpHeight = 30;
        final int steps = 10;
        final int delayPerStep = 10;
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            jumpHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float y = -(jumpHeight * ((float) step / steps));
                    view.setTranslationY(y);
                }
            }, i * delayPerStep);
        }
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            jumpHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    float y = -jumpHeight + (jumpHeight * ((float) step / steps));
                    view.setTranslationY(y);
                }
            }, (steps * delayPerStep) + (i * delayPerStep));
        }
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

    private Drawable createCellBackground(int row, int col, HighlightType highlightType) {
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

        int backgroundColor;
        switch (highlightType) {
            case SELECTED:
                backgroundColor = getResources().getColor(R.color.cell_selected, getTheme());
                break;
            case SAME_NUMBER:
                backgroundColor = getResources().getColor(R.color.cell_same_number, getTheme());
                break;
            case RELATED:
                backgroundColor = getResources().getColor(R.color.cell_related, getTheme());
                break;
            default:
                backgroundColor = getResources().getColor(R.color.cell_default, getTheme());
                break;
        }
        GradientDrawable center = new GradientDrawable();
        center.setColor(backgroundColor);

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
        String level = getIntent().getStringExtra("difficulty_level");
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
        final String solutionBoardToSave;

        if (save) {
            if (board == null) return;
            try {
                boardStateToSave = getBoardStateAsString();
                initialBoardStateToSave = board.getOriginalBoardString();
                solutionBoardToSave = board.getSolutionBoardString();
            } catch (Exception e) {
                e.printStackTrace();
                return;
            }
        } else {
            boardStateToSave = "";
            initialBoardStateToSave = "";
            solutionBoardToSave = "";
        }

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    SavedGameDao dao = DataBase.getInstance(GameActivity.this).savedGameDao();
                    if (!save) {
                        dao.deleteSavedGameForUser(username);
                    } else {
                        dao.saveGame(new SavedGameEntity(
                                difficultyToSave,
                                timerToSave,
                                strikesToSave,
                                boardStateToSave,
                                initialBoardStateToSave,
                                solutionBoardToSave,
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
            }
        });
        thread.start();
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
        Thread loadThread = new Thread(new Runnable() {
            @Override
            public void run() {
                SavedGameEntity savedGame = DataBase.getInstance(GameActivity.this)
                        .savedGameDao()
                        .getSavedGameForUser(username);

                if (savedGame != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                currentDifficulty = savedGame.getDifficultyLevel();

                                String savedBoard = savedGame.getBoardState();
                                String savedInitial = savedGame.getInitialBoardState();
                                String savedSolution = savedGame.getSolutionBoard();

                                if (savedInitial != null && !savedInitial.isEmpty() && savedInitial.length() == 81 && savedBoard.length() == 81) {
                                    board = new GameManager(savedBoard, savedInitial, savedSolution);
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

                                Thread deleteThread = new Thread(new Runnable() {
                                    @Override
                                    public void run() {
                                        DataBase.getInstance(GameActivity.this)
                                                .savedGameDao()
                                                .deleteSavedGameForUser(username);
                                    }
                                });
                                deleteThread.start();
                                handler.postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        finish();
                                    }
                                }, 3500);
                            }
                        }
                    });
                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            android.widget.Toast.makeText(GameActivity.this, "שגיאה בטעינת המשחק", android.widget.Toast.LENGTH_SHORT).show();
                            SharedPreferences gamePrefs = getSharedPreferences("sudoku_game", MODE_PRIVATE);
                            gamePrefs.edit().putBoolean("hasSavedGame_" + username, false).apply();
                            finish();
                        }
                    });
                }
            }
        });
        loadThread.start();
    }
}