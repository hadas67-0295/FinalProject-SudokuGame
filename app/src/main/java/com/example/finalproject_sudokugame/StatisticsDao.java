package com.example.finalproject_sudokugame;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface StatisticsDao {

    @Insert
    void insertStatistics(StatisticsEntity statistics);

    @Update
    void updateStatistics(StatisticsEntity statistics);

    @Query("SELECT * FROM statistics WHERE difficultyLevel = :level LIMIT 1")
    StatisticsEntity getStatisticsByLevel(String level);

    @Query("SELECT * FROM statistics")
    java.util.List<StatisticsEntity> getAllStatistics();
}
