package com.example.TriviaBattler.database.entities;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import com.example.TriviaBattler.database.AppDatabase;
import com.example.TriviaBattler.database.AppRepository;

@Entity(tableName = AppDatabase.STATS_TABLE,
        foreignKeys = @ForeignKey(
                entity = com.example.TriviaBattler.database.entities.User.class,
                parentColumns = "userId",
                childColumns = "userId",
                onDelete = ForeignKey.CASCADE),
        indices = {@Index(value = "userId", unique = true)}
)
public class Stats {

    @PrimaryKey(autoGenerate = true)
    private int statsId;
    private int userId; //Foreign key to the user

    private int correctCount;
    private int wrongCount;
    private int totalCount;

    private double overallScore; //TODO: figure out what this is actually going to be


    public Stats(int userId) { //Defaults all score related fields to 0
        this.userId = userId;
        correctCount = 0;
        wrongCount = 0;
        totalCount = 0;
        overallScore = 0.0;
    }

    /**
     * sets and gets
     * @return sets and gets
     */
    public int getStatsId() {
        return statsId;
    }

    public void setStatsId(int statsId) {
        this.statsId = statsId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCorrectCount() {
        return correctCount;
    }

    public void setCorrectCount(int correctCount) {
        this.correctCount = correctCount;
    }

    public int getWrongCount() {
        return wrongCount;
    }

    public void setWrongCount(int wrongCount) {
        this.wrongCount = wrongCount;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public double getOverallScore() {
        return overallScore;
    }

    public void setOverallScore(double overallScore) {
        this.overallScore = overallScore;
    }

    @Override
    public String toString() {
        return  "Correct Count=" + correctCount +"\n\t"+
                "Wrong Count=" + wrongCount +"\n\t"+
                "Total Count=" + totalCount +"\n\t"+
                "Oververall Score=" + overallScore+"\n";
    }
}
