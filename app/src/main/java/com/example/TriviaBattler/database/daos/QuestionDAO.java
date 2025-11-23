package com.example.TriviaBattler.database.daos;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;


import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.entities.Question;

import java.util.List;

@Dao
public interface QuestionDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Question question);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertALL (List<Question> questions);

    @Query("DELETE FROM " + AppDatabase.QUESTION_TABLE)
    void deleteAll();

    //Random single question from a specified difficulty
    @Query("SELECT * FROM " + AppDatabase.QUESTION_TABLE +
            " WHERE difficulty = :difficulty ORDER BY RANDOM() LIMIT 1")
    LiveData<Question> getRandomByDifficulty(String difficulty);

    //Returns count for the number of questions currently stored (idk if needed but here it is)
    @Query("SELECT COUNT(*) FROM " + AppDatabase.QUESTION_TABLE)
    LiveData<Integer> count();

    //Gets a question by its question id (again, idk if needed but here it is)
    @Query("SELECT * FROM " + AppDatabase.QUESTION_TABLE + " WHERE questionId = :id")
    LiveData<Question> getById(int id);

}
