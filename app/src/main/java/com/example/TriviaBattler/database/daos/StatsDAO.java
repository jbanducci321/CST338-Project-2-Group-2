package com.example.TriviaBattler.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.entities.Stats;
import com.example.TriviaBattler.database.entities.User;

import java.util.List;

@Dao
public interface StatsDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insert(Stats stats);

    @Update
    int update(Stats stats);

    @Query("SELECT * FROM " + AppDatabase.STATS_TABLE + " WHERE userId = :userId LIMIT 1")
    LiveData<Stats> observeByUserId(int userId);

    @Query("SELECT * FROM " + AppDatabase.STATS_TABLE + " WHERE userId = :userId LIMIT 1")
    LiveData<Stats> getByUserIdLive(int userId);

    @Query("SELECT * FROM " + AppDatabase.STATS_TABLE + " WHERE userId = :userId LIMIT 1")
    Stats getByUserId(int userId);

    //TODO: probably want to split this up more or change the logic around a bit
    @Query("UPDATE " + AppDatabase.STATS_TABLE +
            " SET correctCount = correctCount + :addCorrect, " +
            " wrongCount = wrongCount + :addWrong, " +
            " totalCount = correctCount + wrongCount " +
            "WHERE userId = :userId")
    void incrementCounts(int userId, int addCorrect, int addWrong);

    @Query("DELETE FROM " + AppDatabase.STATS_TABLE + " WHERE userId = :userId")
    void deleteForUser(int userId);

    @Query("SELECT * from " + AppDatabase.STATS_TABLE)
    LiveData<List<Stats>> getAllStatsLive();

}
