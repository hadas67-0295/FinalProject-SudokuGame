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
import android.os.Looper;
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

import com.example.finalproject_sudokugame.GameVisualsManager.HighlightType;

import java.util.ArrayList;
import java.util.Random;

public class GameActivity extends AppCompatActivity {

    private GameManager board;
    private GridLayout gridLayout;
    private TextView timerTextView;
    private TextView game_tvResponse;
    private TextView game_tvBoardSource;
    private Button game_btnReturnHome;
    private Button game_btnHint;
    private TextView game_tvStrikes;

    private static final int MAX_STRIKES = 3;
    private static final int MESSAGE_DELAY_MS = 3000;
    private static final int POST_DELAY_MS = 1000;
    private SudokuStatsManager statsManager;
    private AiBoardManager aiBoardManager;
    private GameVisualsManager visualsManager;

    private int strikes = 0;
    private boolean isGameOver = false;
    private String username;
    private String currentDifficulty;

    private Handler handler = new Handler(Looper.getMainLooper());
    private long startTime = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        game_btnReturnHome = findViewById(R.id.game_btnReturnHome);
        game_btnHint = findViewById(R.id.game_btnHint);
        gridLayout = findViewById(R.id.game_gridLayout);
        timerTextView = findViewById(R.id.game_tvTimer);
        game_tvResponse = findViewById(R.id.game_tvResponse);
        game_tvBoardSource = findViewById(R.id.game_tvBoardSource);
        game_tvStrikes = findViewById(R.id.game_tvStrikes);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        SharedPreferences userPrefs = getSharedPreferences("sudoku_user", MODE_PRIVATE);
        username = userPrefs.getString("username", "");

        statsManager = new SudokuStatsManager(this, username);
        aiBoardManager = new AiBoardManager(this);
        visualsManager = new GameVisualsManager(this);

        currentDifficulty = getIntent().getStringExtra("difficulty_level");
        if (currentDifficulty == null) currentDifficulty = "medium";
        boolean resumeGame = getIntent().getBooleanExtra("resume_game", false);

        if (resumeGame) {
            Toast.makeText(this, "Resume feature disabled", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            updateStrikesUI();
        }

        game_btnReturnHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                handler.removeCallbacks(updateTimerRunnable);
                finish();
            }
        });

        game_btnHint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                useHint();
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

        if (!resumeGame && savedInstanceState == null) {
            initializeGameWithAi();
        }
    }

    private void initializeGameWithAi() {
        aiBoardManager.fetchAiBoard(currentDifficulty, new AiBoardManager.AiBoardCallback() {
            @Override
            public void onSuccess(int[][] puzzle, int[][] solution) {
                board = new GameManager(puzzle, solution);
                onGameInitialized(true);
            }

            @Override
            public void onError(String error) {
                fallbackToPool(error);
            }
        });
    }

    private void fallbackToPool(String error) {
        String fallbackMsg = getString(R.string.fallback_message, error);
        Toast.makeText(this, fallbackMsg, Toast.LENGTH_LONG).show();

        SudokuPuzzlePool.PuzzlePair pair = SudokuPuzzlePool.getRandomPuzzle(currentDifficulty);
        board = new GameManager(pair.puzzle, pair.solution);

        onGameInitialized(false);
    }

    private void onGameInitialized(boolean fromAi) {
        if (isFinishing() || isDestroyed()) return;

        strikes = 0;
        updateStrikesUI();
        startTime = System.currentTimeMillis();
        handler.postDelayed(updateTimerRunnable, POST_DELAY_MS);
        showBoardSource(fromAi);
        drawBoard();
    }

    private void showBoardSource(boolean fromAi) {
        game_tvBoardSource.setText(fromAi ? R.string.board_source_ai : R.string.board_source_pool);
        game_tvBoardSource.setVisibility(View.VISIBLE);
    }

    private Runnable updateTimerRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            long seconds = (elapsedMillis / 1000) % 60;
            long minutes = (elapsedMillis / 1000) / 60;

            timerTextView.setText(String.format(getString(R.string.timer_format), minutes, seconds));
            handler.postDelayed(this, POST_DELAY_MS);
        }
    };

    private void drawBoard() {
        if (isGameOver || board == null)
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

                int cellValue = board.getCell(row, col);
                HighlightType highlightType = visualsManager.getHighlightType(row, col, selectedRow, selectedCol, selectedValue, cellValue);

                Drawable background = visualsManager.createCellBackground(row, col, highlightType);
                cell.setBackground(background);

                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(row, 1f);
                params.columnSpec = GridLayout.spec(col, 1f);
                params.setMargins(0, 0, 0, 0);
                cell.setLayoutParams(params);

                cell.setText(cellValue == 0 ? "" : String.valueOf(cellValue));

                if (cellValue != 0) {
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
                    cell.setOnClickListener(v -> {
                        board.selectCell(finalRow, finalCol);
                        drawBoard();
                    });
                }
                gridLayout.addView(cell);
            }
        }
    }

    private void endGame(boolean isWin, long elapsedMillis, boolean perfect) {
        isGameOver = true;
        handler.removeCallbacks(updateTimerRunnable);
        statsManager.saveStats(isWin, currentDifficulty, elapsedMillis, perfect, strikes);

        if (isWin) {
            visualsManager.animateVictory(gridLayout);
            handler.postDelayed(() -> showEndDialog(true), 2500);
        } else {
            for (int i = 0; i < 25; i++) {
                int delay = i * 200;
                handler.postDelayed(() -> gridLayout.setTranslationX(20), delay);
                handler.postDelayed(() -> gridLayout.setTranslationX(-20), delay + 100);
            }
            handler.postDelayed(() -> {
                gridLayout.setTranslationX(0);
                showEndDialog(false);
            }, 5000);
        }
    }

    private void showEndDialog(boolean isWin) {
        if (isFinishing() || isDestroyed()) return;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(isWin ? getString(R.string.victory_title) : getString(R.string.game_over_title));
        builder.setMessage(isWin ? getString(R.string.victory_message)
                : getString(R.string.game_over_message));
        builder.setCancelable(false);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        builder.show();
    }





    private void placeNumber(int number) {
        if (isGameOver) return;

        int selectedRow = board.getSelectedRow();
        int selectedCol = board.getSelectedCol();

        if (selectedRow == -1 || selectedCol == -1) {
            showMessage(getString(R.string.select_cell_message));
            return;
        }

        String reason = board.getInvalidMoveReason(selectedRow, selectedCol, number);
        if (reason != null) {
            addStrike();
            if (reason.equals("Duplicate in row")) showMessage(getString(R.string.duplicate_row));
            else if (reason.equals("Duplicate in column")) showMessage(getString(R.string.duplicate_col));
            else if (reason.equals("Duplicate in 3x3 block")) showMessage(getString(R.string.duplicate_block));
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
            showMessage(getString(R.string.invalid_number));
        }
    }

    private void useHint() {
        if (isGameOver || board == null) return;

        ArrayList<int[]> emptyCells = new ArrayList<>();
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                if (board.getCell(row, col) == 0) {
                    emptyCells.add(new int[]{row, col});
                }
            }
        }

        if (emptyCells.isEmpty()) return;

        Random random = new Random();
        int[] cell = emptyCells.get(random.nextInt(emptyCells.size()));
        int row = cell[0];
        int col = cell[1];
        int correctValue = board.getSolutionCell(row, col);

        board.setCell(row, col, correctValue);
        drawBoard();

        if (board.isComplete()) {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            boolean perfect = strikes == 0;
            endGame(true, elapsedMillis, perfect);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(updateTimerRunnable);
    }

    private void showMessage(String message) {
        game_tvResponse.setText(message);
        game_tvResponse.setVisibility(View.VISIBLE);

        game_tvResponse.postDelayed(() -> game_tvResponse.setVisibility(View.GONE), MESSAGE_DELAY_MS);
    }

    private void updateStrikesUI() {
        game_tvStrikes.setText(getString(R.string.strikes_format, strikes, MAX_STRIKES));
    }

    private void addStrike() {
        strikes++;
        updateStrikesUI();
        visualsManager.vibrateIfEnabled();

        if (strikes >= MAX_STRIKES) {
            long elapsedMillis = System.currentTimeMillis() - startTime;
            endGame(false, elapsedMillis, false);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!isGameOver && board != null) {
            handler.removeCallbacks(updateTimerRunnable);
            handler.postDelayed(updateTimerRunnable, POST_DELAY_MS);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(updateTimerRunnable);
    }
}