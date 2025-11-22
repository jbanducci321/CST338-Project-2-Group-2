package com.example.TriviaBattler.database;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.MainActivity;
import com.example.TriviaBattler.database.daos.QuestionDAO;
import com.example.TriviaBattler.database.daos.UserDAO;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.database.entities.User;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class AppRepository {

    private final UserDAO userDAO;
    private final QuestionDAO questionDAO;

    private static AppRepository repository;

    public AppRepository(Application application) {
        AppDatabase db = AppDatabase.getDatabase(application);
        this.userDAO = db.userDAO();
        this.questionDAO = db.questionDAO();
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


    //Stats related
    //TODO: Add stats stuff here

}
