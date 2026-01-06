package com.example.finalproject_sudokugame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "statistics")
public class StatisticsEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String difficultyLevel;
    private int gamesPlayed;
    private int gamesWon;
    private int gamesLost;
    private long bestTime;
    private int bestWinStreak;
    private int perfectWins;

    public StatisticsEntity(String difficultyLevel, int gamesPlayed, int gamesWon, int gamesLost, long bestTime, int bestWinStreak, int perfectWins) {
        this.difficultyLevel = difficultyLevel;
        this.gamesPlayed = gamesPlayed;
        this.gamesWon = gamesWon;
        this.gamesLost = gamesLost;
        this.bestTime = bestTime;
        this.bestWinStreak = bestWinStreak;
        this.perfectWins = perfectWins;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getDifficultyLevel() {
        return difficultyLevel;
    }
    public void setDifficultyLevel(String difficultyLevel) {
        this.difficultyLevel = difficultyLevel;
    }

    public int getGamesPlayed() {
        return gamesPlayed;
    }
    public void setGamesPlayed(int gamesPlayed) {
        this.gamesPlayed = gamesPlayed;
    }

    public int getGamesWon() {
        return gamesWon;
    }
    public void setGamesWon(int gamesWon) {
        this.gamesWon = gamesWon;
    }

    public int getGamesLost() {
        return gamesLost;
    }
    public void setGamesLost(int gamesLost) {
        this.gamesLost = gamesLost;
    }

    public long getBestTime() {
        return bestTime;
    }
    public void setBestTime(long bestTime) {
        this.bestTime = bestTime;
    }

    public int getBestWinStreak() {
        return bestWinStreak;
    }
    public void setBestWinStreak(int bestWinStreak) {
        this.bestWinStreak = bestWinStreak;
    }

    public int getPerfectWins() {
        return perfectWins;
    }
    public void setPerfectWins(int perfectWins) {
        this.perfectWins = perfectWins;
    }
}