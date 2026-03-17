package com.example.finalproject_sudokugame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserStatsDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertOrUpdate(UserStatsEntity stats);

    @Query("SELECT * FROM user_stats WHERE username = :username AND level = :level LIMIT 1")
    UserStatsEntity getStats(String username, String level);

    @Query("SELECT * FROM user_stats WHERE username = :username")
    List<UserStatsEntity> getAllStatsForUser(String username);
}
