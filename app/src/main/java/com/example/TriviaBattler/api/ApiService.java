package com.example.TriviaBattler.api;

import com.example.TriviaBattler.api.dto.ApiResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiService {

    @GET("api.php?type=multiple")
    Call<ApiResponse> getQuestions(
            @Query("amount") int amount,
            @Query("difficulty") String difficulty
            //@Query("type") String type
    );

    @GET("api.php?type=multiple")
    Call<ApiResponse> getQuestionCategory(
            @Query("amount") int amount,
            @Query("difficulty") String difficulty,
            @Query("category") int category
    );

}
