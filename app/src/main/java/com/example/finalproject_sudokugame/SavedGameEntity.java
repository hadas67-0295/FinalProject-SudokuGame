package com.example.finalproject_sudokugame;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "saved_game")
public class SavedGameEntity {

    @PrimaryKey
    @androidx.annotation.NonNull
    private String username;
    
    private String difficultyLevel;
    private long timer;
    private int mistakes;
    private String boardState;
    private String initialBoardState;

    public SavedGameEntity(String difficultyLevel, long timer, int mistakes, String boardState, String initialBoardState, @androidx.annotation.NonNull String username) {
        this.username = username;
        this.difficultyLevel = difficultyLevel;
        this.timer = timer;
        this.mistakes = mistakes;
        this.boardState = boardState;
        this.initialBoardState = initialBoardState;
    }

    @androidx.annotation.NonNull
    public String getUsername(){
        return username;
    }
    public void setUsername(@androidx.annotation.NonNull String username) { this.username = username; }

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

    public String getInitialBoardState() {
        return initialBoardState;
    }
    public void setInitialBoardState(String initialBoardState) {
        this.initialBoardState = initialBoardState;
    }
}
