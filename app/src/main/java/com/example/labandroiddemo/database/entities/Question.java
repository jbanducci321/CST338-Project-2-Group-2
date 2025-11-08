package com.example.labandroiddemo.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.example.labandroiddemo.database.AppDatabase;

import java.time.LocalDateTime;
import java.util.List;

@Entity(tableName = AppDatabase.QUESTION_TABLE)
public class Question {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String category;
    private String type;
    private String difficulty;
    private String question;
    private String correctAnswer;
    private List<String> incorrectAnswers; //Will probably need to set up a type converter (JSON)
    private LocalDateTime importDate;

    //TODO: Figure out best implementation for Question constructor
    public Question(String question, String type, String difficulty, String category, String correctAnswer, List<String> incorrectAnswers) {


        importDate = LocalDateTime.now();
    }

}
