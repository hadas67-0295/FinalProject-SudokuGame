package com.example.finalproject_sudokugame;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.widget.GridLayout;

import androidx.appcompat.app.AppCompatActivity;

public class GameVisualsManager {
    private final AppCompatActivity activity;
    private static final long VIBRATION_DURATION_MS = 200;
    private static final long ANIMATION_DELAY_BASE = 50;

    public enum HighlightType {
        NONE, SELECTED, SAME_NUMBER, RELATED
    }

    public HighlightType getHighlightType(int row, int col, int selectedRow, int selectedCol, int selectedValue, int cellValue) {
        if (selectedRow < 0 || selectedCol < 0) return HighlightType.NONE;
        if (row == selectedRow && col == selectedCol) return HighlightType.SELECTED;

        if (cellValue != 0 && selectedValue != 0 && cellValue == selectedValue) {
            return HighlightType.SAME_NUMBER;
        }

        if (row == selectedRow || col == selectedCol) return HighlightType.RELATED;

        int boxRowStart = (selectedRow / 3) * 3;
        int boxColStart = (selectedCol / 3) * 3;
        if (row >= boxRowStart && row < boxRowStart + 3 && col >= boxColStart && col < boxColStart + 3) {
            return HighlightType.RELATED;
        }

        return HighlightType.NONE;
    }

    public GameVisualsManager(AppCompatActivity activity) {
        this.activity = activity;
    }

    public void vibrateIfEnabled() {
        SharedPreferences prefs = activity.getSharedPreferences("sudoku_settings", Context.MODE_PRIVATE);
        boolean vibrationEnabled = prefs.getBoolean("vibration_enabled", true);

        if (!vibrationEnabled) return;

        Vibrator vibrator = (Vibrator) activity.getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null && vibrator.hasVibrator()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATION_DURATION_MS, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                vibrator.vibrate(VIBRATION_DURATION_MS);
            }
        }
    }

    public void animateVictory(GridLayout gridLayout) {
        int rows = 9;
        int cols = 9;
        for (int i = 0; i < gridLayout.getChildCount(); i++) {
            View cell = gridLayout.getChildAt(i);
            int row = i / cols;
            int col = i % cols;
            long delay = (row + col) * ANIMATION_DELAY_BASE;

            new Handler().postDelayed(() -> startManualJump(cell), delay);
        }
    }

    private void startManualJump(View view) {
        final Handler jumpHandler = new Handler();
        final int jumpHeight = 30;
        final int steps = 10;
        final int delayPerStep = 10;
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            jumpHandler.postDelayed(() -> {
                float y = -(jumpHeight * ((float) step / steps));
                view.setTranslationY(y);
            }, i * delayPerStep);
        }
        for (int i = 1; i <= steps; i++) {
            final int step = i;
            jumpHandler.postDelayed(() -> {
                float y = -jumpHeight + (jumpHeight * ((float) step / steps));
                view.setTranslationY(y);
            }, (steps * delayPerStep) + (i * delayPerStep));
        }
    }

    public Drawable createCellBackground(int row, int col, HighlightType highlightType) {
        float density = activity.getResources().getDisplayMetrics().density;
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
                backgroundColor = activity.getResources().getColor(R.color.cell_selected, activity.getTheme());
                break;
            case SAME_NUMBER:
                backgroundColor = activity.getResources().getColor(R.color.cell_same_number, activity.getTheme());
                break;
            case RELATED:
                backgroundColor = activity.getResources().getColor(R.color.cell_related, activity.getTheme());
                break;
            default:
                backgroundColor = activity.getResources().getColor(R.color.cell_default, activity.getTheme());
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
}
