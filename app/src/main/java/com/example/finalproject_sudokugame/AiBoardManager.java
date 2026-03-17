package com.example.finalproject_sudokugame;

import android.app.ProgressDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.Toast;

public class AiBoardManager {
    private final AppCompatActivity activity;
    private boolean isAiRequestInProgress = false;

    public AiBoardManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public interface AiBoardCallback {
        void onSuccess(int[][] puzzle, int[][] solution);
        void onError(String error);
    }

    public void fetchAiBoard(String difficulty, AiBoardCallback callback) {
        if (isAiRequestInProgress) return;
        isAiRequestInProgress = true;

        ProgressDialog progressDialog = new ProgressDialog(activity);
        progressDialog.setMessage(activity.getString(R.string.ai_loading));
        progressDialog.setCancelable(false);
        progressDialog.show();

        int emptyCells = getEmptyCellsForDifficulty(difficulty);

        SudokuAiService aiService = SudokuAiService.getInstance(BuildConfig.GOOGLE_API_KEY);
        aiService.generateSudoku(emptyCells, new SudokuAiService.SudokuCallback() {
            @Override
            public void onSuccess(int[][] puzzle, int[][] solution) {
                isAiRequestInProgress = false;
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    progressDialog.dismiss();
                    callback.onSuccess(puzzle, solution);
                });
            }

            @Override
            public void onError(String error) {
                isAiRequestInProgress = false;
                activity.runOnUiThread(() -> {
                    if (activity.isFinishing() || activity.isDestroyed()) return;
                    progressDialog.dismiss();
                    callback.onError(error);
                });
            }
        });
    }

    private int getEmptyCellsForDifficulty(String difficulty) {
        if (difficulty.equalsIgnoreCase("easy")) return 20;
        if (difficulty.equalsIgnoreCase("medium")) return 40;
        return 50;
    }
}
