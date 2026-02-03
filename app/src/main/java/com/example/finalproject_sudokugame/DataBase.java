package com.example.finalproject_sudokugame;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import android.content.Context;

@Database(entities = {StatisticsEntity.class, SavedGameEntity.class}, version = 1)
public abstract class DataBase extends RoomDatabase {

    public abstract StatisticsDao statisticsDao();
    public abstract SavedGameDao savedGameDao();

    private static DataBase instance;

    public static synchronized DataBase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            DataBase.class, "sudoku_database")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return instance;

    }
}