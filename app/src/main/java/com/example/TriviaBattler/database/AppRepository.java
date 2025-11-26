package com.example.TriviaBattler.database;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.MainActivity;
import com.example.TriviaBattler.api.ApiClient;
import com.example.TriviaBattler.api.dto.ApiQuestion;
import com.example.TriviaBattler.api.dto.ApiResponse;
import com.example.TriviaBattler.database.daos.QuestionDAO;
import com.example.TriviaBattler.database.daos.StatsDAO;
import com.example.TriviaBattler.database.daos.UserDAO;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.database.entities.Stats;
import com.example.TriviaBattler.database.entities.User;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {

    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;

    private final StatsDAO statsDAO;

    private static AppRepository repository;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.questionDAO = db.questionDAO();
        this.statsDAO = db.statsDAO();
    }

    public static AppRepository getRepository(Application application) {
        if (repository != null) {
            return repository;
        }
        Future<AppRepository> future = AppDatabase.databaseWriteExecutor.submit(
                new Callable<AppRepository>() {
                    @Override
                    public AppRepository call() throws Exception {
                        return new AppRepository(application);
                    }
                }
        );
        try {
            return future.get();
        }
        catch (InterruptedException | ExecutionException e) {
            Log.d(MainActivity.TAG, "Problem getting AppRepository, thread error");
        }
        return null;
    }

    //User related
    public void insertUser(User... user) {
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }


    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId);
    }


    public void setAdmin(String username, boolean isAdmin) { //Promote/demote a user as admin by their username
        AppDatabase.databaseWriteExecutor.execute(()->{
            userDAO.setAdminByUsername(username, isAdmin);
        });
    }

    public boolean checkAvailableUsername(String username) {
        return userDAO.checkUsername(username);
    }


    //Question related
    public void insertQuestion (Question question) { //Add in a single question
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            questionDAO.insert(question);
        });
    }

    public void insertQuestions (List<Question> questions) { //Adds in multiple questions
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            questionDAO.insertALL(questions);
        });
    }

    public LiveData<Question> getRandomQuestionByDifficulty (String difficulty) { //I feel the method name is pretty self explanatory
        return questionDAO.getRandomByDifficulty(difficulty);
    }


    public void recordResult(int userId, int addCorrect, int addWrong) {
        AppDatabase.databaseWriteExecutor.execute(() -> {

            statsDAO.incrementCounts(userId, addCorrect, addWrong);

            Stats s = statsDAO.getByUserId(userId);
            if (s == null) return;

            int total = s.getCorrectCount() + s.getWrongCount();
            s.setTotalCount(total);

            double pct = (total == 0) ? 0.0 : (s.getCorrectCount() * 100.0) / total;
            s.setOverallScore(pct);

            statsDAO.update(s);

            Log.d("STATS", "Updated stats for user=" + userId +
                    " correct=" + s.getCorrectCount() +
                    " wrong=" + s.getWrongCount() +
                    " total=" + s.getTotalCount() +
                    " score=" + s.getOverallScore());
        });
    }

    public LiveData<Stats> getStatsByUserId(int userId) {
        return statsDAO.observeByUserId(userId);
    }

    //API related

    public void refreshQuestionsFromApi(String difficulty, int amount) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            questionDAO.deleteByDifficulty(difficulty);
            Call<ApiResponse> call = ApiClient.getService().getQuestions(amount, difficulty);
        });
    }
    public void apiCall(String difficulty, int amount) {
        ApiClient.getService()
                .getQuestions(amount, difficulty)
                .enqueue(new Callback<ApiResponse>() {
                    @Override
                    public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                        List<Question> toInsert = new ArrayList<>();

                        assert response.body() != null;
                        for (ApiQuestion dto : response.body().results) {
                            toInsert.add(new Question(dto.type, dto.difficulty, dto.category, dto.question,
                                    dto.correct_answer, dto.incorrect_answers));
                        }

                        AppDatabase.databaseWriteExecutor.execute(() ->
                                questionDAO.insertALL(toInsert));

                    }

                    @Override
                    public void onFailure(Call<ApiResponse> call, Throwable t) {
                        Log.e("API_ERROR", "API call failed: " + t.getMessage(), t);
                    }
                });
    }
}
