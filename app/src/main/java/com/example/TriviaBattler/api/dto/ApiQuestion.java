package com.example.TriviaBattler.api.dto;

import java.util.List;

public class ApiQuestion {
    public String type;
    public String difficulty;
    public String category;
    public String question;
    public String correct_answer;
    public List<String> incorrect_answers;
}
