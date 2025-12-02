package com.example.TriviaBattler;

import static org.junit.Assert.*;

import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.database.entities.Stats;

import org.junit.Test;

import java.util.Arrays;

public class EntityLogicTest {

    @Test
    public void equalsHashcode() {
        User u1 = new User("Alice", "pass");
        u1.setUserId(1);

        User u2 = new User("Alice", "pass");
        u2.setUserId(1);

        assertEquals(u1, u2);
        assertEquals(u1.hashCode(), u2.hashCode());
    }

    @Test
    public void stats() {
        Stats s = new Stats(5);
        assertEquals(0, s.getCorrectCount());
        assertEquals(0, s.getWrongCount());
        assertEquals(0, s.getTotalCount());
        assertEquals(0.0, s.getOverallScore(), 0.001);

        s.setCorrectCount(3);
        s.setWrongCount(2);
        s.setTotalCount(5);
        s.setOverallScore(60.0);

        assertEquals(3, s.getCorrectCount());
        assertEquals(2, s.getWrongCount());
        assertEquals(5, s.getTotalCount());
        assertEquals(60.0, s.getOverallScore(), 0.001);
    }

    @Test
    public void question() {
        Question q = new Question(
                "multiple",
                "medium",
                "Science",
                "What is H2O?",
                "Water",
                Arrays.asList("Hydrogen", "Oxygen", "Helium")
        );

        assertEquals("multiple", q.getType());
        assertEquals("medium", q.getDifficulty());
        assertEquals("Science", q.getCategory());
        assertEquals("What is H2O?", q.getQuestion());
        assertEquals("Water", q.getCorrectAnswer());
        assertEquals(3, q.getIncorrectAnswers().size());
    }
}