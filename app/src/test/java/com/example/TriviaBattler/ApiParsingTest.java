package com.example.TriviaBattler;

import static org.junit.Assert.*;

import com.example.TriviaBattler.api.dto.ApiQuestion;
import com.example.TriviaBattler.api.dto.ApiResponse;
import com.example.TriviaBattler.database.entities.Question;
import com.google.gson.Gson;

import org.junit.Test;

import java.util.List;

public class ApiParsingTest {

    @Test
    public void apiResponse() {

        // Example Question
        String json = "{\n" +
                "  \"response_code\": 0,\n" +
                "  \"results\": [\n" +
                "    {\n" +
                "      \"type\": \"multiple\",\n" +
                "      \"difficulty\": \"easy\",\n" +
                "      \"category\": \"General Knowledge\",\n" +
                "      \"question\": \"What color is the sky?\",\n" +
                "      \"correct_answer\": \"Blue\",\n" +
                "      \"incorrect_answers\": [\"Red\", \"Green\", \"Yellow\"]\n" +
                "    }\n" +
                "  ]\n" +
                "}";

        // Turn JSON into ApiResponse object
        ApiResponse response = new Gson().fromJson(json, ApiResponse.class);

        // Check response code and result size
        assertEquals(0, response.response_code);
        assertEquals(1, response.results.size());

        // Turn results into Question object
        ApiQuestion r = response.results.get(0);
        Question q = r.toQuestionEntity();

        // Check if results data is correct
        assertEquals("multiple", q.getType());
        assertEquals("easy", q.getDifficulty());
        assertEquals("General Knowledge", q.getCategory());
        assertEquals("What color is the sky?", q.getQuestion());
        assertEquals("Blue", q.getCorrectAnswer());

        List<String> incorrect = q.getIncorrectAnswers();
        assertEquals(3, incorrect.size());
        assertTrue(incorrect.contains("Red"));
    }
}