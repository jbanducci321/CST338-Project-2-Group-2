package com.example.TriviaBattler.api.dto;

import com.example.TriviaBattler.database.entities.Question;

import java.util.List;

public class ApiQuestion {
    public String type;
    public String difficulty;
    public String category;
    public String question;
    public String correct_answer;
    public List<String> incorrect_answers;

    public Question toQuestionEntity() {
        return new Question(type, difficulty, category, question, correct_answer, incorrect_answers);
    }
}
