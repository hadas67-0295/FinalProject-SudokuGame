package com.example.finalproject_sudokugame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SavedGameDao {

    @Insert
    void insertSavedGame(SavedGameEntity savedGame);

    @Update
    void updateSavedGame(SavedGameEntity savedGame);

    @Query("SELECT * FROM saved_game WHERE difficultyLevel = :level ORDER BY id DESC LIMIT 1")
    SavedGameEntity getLastSavedGame(String level);

    @Query("DELETE FROM saved_game WHERE id = :id")
    void deleteSavedGame(int id);
}