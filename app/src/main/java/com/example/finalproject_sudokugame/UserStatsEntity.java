package com.example.finalproject_sudokugame;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "user_stats", primaryKeys = {"username", "level"})
public class UserStatsEntity {

    @NonNull
    private String username;

    @NonNull
    private String level;

    private int played;
    private int wins;
    private int losses;
    private int currentStreak;
    private int bestStreak;
    private int perfectWins;
    private String bestTime;

    public UserStatsEntity(@NonNull String username, @NonNull String level) {
        this.username = username;
        this.level = level;
        this.bestTime = "--:--";
    }

    @NonNull
    public String getUsername() {
        return username;
    }

    public void setUsername(@NonNull String username) {
        this.username = username;
    }

    @NonNull
    public String getLevel() {
        return level;
    }

    public void setLevel(@NonNull String level) {
        this.level = level;
    }

    public int getPlayed() {
        return played;
    }

    public void setPlayed(int played) {
        this.played = played;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public int getLosses() {
        return losses;
    }

    public void setLosses(int losses) {
        this.losses = losses;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public int getBestStreak() {
        return bestStreak;
    }

    public void setBestStreak(int bestStreak) {
        this.bestStreak = bestStreak;
    }

    public int getPerfectWins() {
        return perfectWins;
    }

    public void setPerfectWins(int perfectWins) {
        this.perfectWins = perfectWins;
    }

    public String getBestTime() {
        return bestTime;
    }

    public void setBestTime(String bestTime) {
        this.bestTime = bestTime;
    }
}
