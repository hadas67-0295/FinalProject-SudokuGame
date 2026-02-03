package com.example.finalproject_sudokugame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface SavedGameDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void saveGame(SavedGameEntity savedGame);

    @Query("SELECT * FROM saved_game WHERE username = :username LIMIT 1")
    SavedGameEntity getSavedGameForUser(String username);

    @Query("DELETE FROM saved_game WHERE username = :username")
    void deleteSavedGameForUser(String username);
}
