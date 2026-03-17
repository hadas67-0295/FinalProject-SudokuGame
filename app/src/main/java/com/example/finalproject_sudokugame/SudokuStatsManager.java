package com.example.finalproject_sudokugame;

import android.content.Context;
import android.content.SharedPreferences;
import androidx.appcompat.app.AppCompatActivity;

public class SudokuStatsManager {
    private final Context context;
    private final String username;

    public SudokuStatsManager(Context context, String username) {
        this.context = context;
        this.username = username;
    }

    public String getUsernamePrefix() {
        return (username == null || username.isEmpty()) ? "guest_" : username + "_";
    }

    public void saveStats(boolean isWin, String level, long elapsedMillis, boolean perfect, int strikes) {
        new Thread(() -> {
            DataBase db = DataBase.getInstance(context);
            UserStatsDao statsDao = db.userStatsDao();

            String finalUsername = (username == null || username.isEmpty()) ? "guest" : username;
            UserStatsEntity stats = statsDao.getStats(finalUsername, level);

            if (stats == null) {
                stats = new UserStatsEntity(finalUsername, level);
            }

            stats.setPlayed(stats.getPlayed() + 1);

            if (isWin) {
                stats.setWins(stats.getWins() + 1);
                stats.setCurrentStreak(stats.getCurrentStreak() + 1);

                if (stats.getCurrentStreak() > stats.getBestStreak()) {
                    stats.setBestStreak(stats.getCurrentStreak());
                }

                if (perfect) {
                    stats.setPerfectWins(stats.getPerfectWins() + 1);
                }

                long currentElapsed = parseTime(stats.getBestTime());
                if (elapsedMillis < currentElapsed) {
                    stats.setBestTime(formatTime(elapsedMillis));
                }
            } else {
                stats.setLosses(stats.getLosses() + 1);
                stats.setCurrentStreak(0);
            }

            statsDao.insertOrUpdate(stats);
        }).start();
    }

    public long parseTime(String time) {
        try {
            if (time == null || time.equals("--:--")) return Long.MAX_VALUE;
            String[] parts = time.split(":");
            int minutes = Integer.parseInt(parts[0]);
            int seconds = Integer.parseInt(parts[1]);
            return (minutes * 60 + seconds) * 1000L;
        } catch (Exception e) {
            return Long.MAX_VALUE;
        }
    }

    public String formatTime(long elapsedMillis) {
        long seconds = (elapsedMillis / 1000) % 60;
        long minutes = (elapsedMillis / 1000) / 60;
        return String.format("%02d:%02d", minutes, seconds);
    }
}
