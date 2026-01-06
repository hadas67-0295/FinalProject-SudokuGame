package com.example.finalproject_sudokugame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_game")
public class SavedGameEntity {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String difficultyLevel;
    private long timer;
    private int mistakes;
    private String boardState;

    public SavedGameEntity(String difficultyLevel, long timer, int mistakes, String boardState) {
        this.difficultyLevel = difficultyLevel;
        this.timer = timer;
        this.mistakes = mistakes;
        this.boardState = boardState;
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

    public long getTimer() {
        return timer;
    }
    public void setTimer(long timer) {
        this.timer = timer;
    }

    public int getMistakes() {
        return mistakes;
    }
    public void setMistakes(int mistakes) {
        this.mistakes = mistakes;
    }

    public String getBoardState() {
        return boardState;
    }
    public void setBoardState(String boardState) {
        this.boardState = boardState;
    }
}
