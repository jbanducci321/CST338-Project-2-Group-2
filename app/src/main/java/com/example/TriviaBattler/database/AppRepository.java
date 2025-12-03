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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.function.Consumer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AppRepository {
    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;

    private final StatsDAO statsDAO;

    private static AppRepository repository;

    /**
     * App Repository
     * @param application application
     */

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.questionDAO = db.questionDAO();
        this.statsDAO = db.statsDAO();
    }

    /**
     * get repository
     * @param application application
     * @return repository
     */

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

    /**
     * user related
     * @param user user
     */
    public void insertUser(User... user) {
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            userDAO.insert(user);
        });
    }

    public LiveData<User> getUserByUserName(String username) {
        return userDAO.getUserByUserName(username);
    }

    /**
     * get user id
     * @param userId user id
     * @param callback callback
     */
    public void getUserByUserIdNotLive(int userId, Consumer<User> callback) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            User user = userDAO.getUserByUserIdNotLive(userId);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> callback.accept(user));
        });

    }

    /**
     * get user by user id
     * @param userId user id
     * @return user
     */
    public LiveData<User> getUserByUserId(int userId) {
        return userDAO.getUserByUserId(userId);
    }

    /**
     * get stats live
     * @return stats
     */
    public LiveData<List<Stats>> getAllStatsLive() {
        return statsDAO.getAllStatsLive();
    }

    /**
     * get users
     * @return users
     */
    public ArrayList<User> getAllUsers(){
        Future<ArrayList<User>> future = AppDatabase.databaseWriteExecutor.submit(
                new Callable<ArrayList<User>>() {
                    @Override
                    public ArrayList<User> call() throws Exception {
                        return (ArrayList<User>) userDAO.getAllUsers();
                    }
                });
        try{
            return future.get();
        } catch (InterruptedException| ExecutionException e){
            Log.i(MainActivity.TAG,"I don't know what to do to fix");
        }
        return null;

    }

    /**
     * set admin
     * @param username of user
     * @param isAdmin are they admin
     */
    public void setAdmin(String username, boolean isAdmin) { //Promote/demote a user as admin by their username
        AppDatabase.databaseWriteExecutor.execute(()->{
            userDAO.setAdminByUsername(username, isAdmin);
        });
    }

    /**
     * check username
     * @param username username
     * @return user dao
     */
    public boolean checkAvailableUsername(String username) {
        return userDAO.checkUsername(username);
    }


    /**
     * question insert
     * @param question a question
     */
    public void insertQuestion (Question question) {
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            questionDAO.insert(question);
        });
    }

    /**
     * multiple insert questions
     * @param questions multiple questions
     */

    public void insertQuestions (List<Question> questions) {
        AppDatabase.databaseWriteExecutor.execute(()->
        {
            questionDAO.insertALL(questions);
        });
    }

    /**
     * get questions by difficulty
     * @param difficulty difficulty
     * @return questions
     */
    public LiveData<Question> getRandomQuestionByDifficulty (String difficulty) { //I feel the method name is pretty self explanatory
        return questionDAO.getRandomByDifficulty(difficulty);
    }

    /**
     * record result
     * @param userId user id
     * @param addCorrect right answers
     * @param addWrong wrong answers
     */
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

    /**
     * get stats by user id
     * @param userId user id
     * @return stats dao
     */

    public LiveData<Stats> getStatsByUserId(int userId) {
        return statsDAO.observeByUserId(userId);
    }

    /**
     * questions from api
     * @param difficulty difficulty
     * @param amount amount
     */
    public void refreshQuestionsFromApi(String difficulty, int amount) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            questionDAO.deleteByDifficulty(difficulty);
            Call<ApiResponse> call = ApiClient.getService().getQuestions(amount, difficulty);
        });
    }

    /**
     * api call
     * @param difficulty difficulty
     * @param amount amount
     */
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

    /**
     * An api call used in the add question activity that lets you select category
     * @param difficulty difficulty
     * @param amount amount
     * @param categoryId category
     * @return true or false
     */
    public boolean apiCallAdminCategory(String difficulty, int amount, int categoryId) {
        try {
            ApiClient.getService()
                    .getQuestionCategory(amount, difficulty, categoryId)
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
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

}
