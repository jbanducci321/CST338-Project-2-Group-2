package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityQuestionsBinding;
import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuestionsActivity extends AppCompatActivity {
    private static final int LOGGED_OUT = -1;

    private AppRepository repository;
    private User user;
    private ActivityQuestionsBinding binding;

    private int userId = -1;
    private String difficulty = null;

    private LiveData<Question> currentLiveQ;
    public static final String EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY";
    public static final String EXTRA_USER_ID = "USER_ID";

    private int currentQuestionIndex = 1;
    private static final int TOTAL_QUESTIONS = 10;

    private int defaultColor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Get default button color so clicked button's color can be reset after it became red or green
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(androidx.appcompat.R.attr.colorPrimary, typedValue, true);
        defaultColor = typedValue.data;

        // Set up toolbar and enable back arrow
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Get user id from previous activity and check if it's valid
        repository = AppRepository.getRepository(getApplication());
        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        if (userId != -1) {
            // Refresh menu if user exists
            LiveData<User> userObserver = repository.getUserByUserId(userId);
            userObserver.observe(this, u -> {
                user = u;

                if (user != null) invalidateOptionsMenu();
            });
        }

        // Get difficulty level from previous activity
        difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);

        // Update question display and present 1st question
        updateQuestionCountDisplay();
        displayQuestion(difficulty);
    }

    //Menu Inflater
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    //Visibility of menu items
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem userItem = menu.findItem(R.id.logoutMenuItem);
        MenuItem adminItem = menu.findItem(R.id.admin);

        // Hide user and admin options if user doesn't exist
        if (user == null) {
            userItem.setVisible(false);
            adminItem.setVisible(false);
            return true;
        }

        // Display user's name
        userItem.setVisible(true);
        View actionView = userItem.getActionView();
        if (actionView != null) {
            TextView usernameView = actionView.findViewById(R.id.usernameTitle);
            if (usernameView != null) usernameView.setText(user.getUsername());
        }

        // Display admin stuff if user is admin
        adminItem.setVisible(user.isAdmin());
        return true;
    }

    //Options for menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        // Switch activities depending on what was selected
        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.logout) {
            logout();
            return true;
        } else if (id == R.id.stats) {
            Intent intent = Statistics.statsIntentFactory(this, userId);
            startActivity(intent);
            return true;
        }else if (id == R.id.admin) {
            startActivity(AdminLanding.adminLandingIntentFactory(getApplicationContext(),userId));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Displays a random question by difficulty and sets answer controls.
     * @param difficulty Difficulty level used to fetch a question.
     */
    private void displayQuestion(String difficulty) {
        // Kill question observers
        if (currentLiveQ != null) {
            currentLiveQ.removeObservers(this);
        }

        // Set up question and answer buttons
        currentLiveQ = repository.getRandomQuestionByDifficulty(difficulty);
        currentLiveQ.observe(this, q -> {
            // Default
            if (q == null) {
                binding.textViewMainQuestionDisplay.setText("No question found for: " + difficulty);
                binding.textViewA.setText("");
                binding.textViewB.setText("");
                binding.textViewC.setText("");
                binding.textViewD.setText("");
                clearAnswerClickListeners();
                return;
            }

            // Gather all answers
            List<String> tempAnswers = new ArrayList<>();
            if (q.getCorrectAnswer() != null) tempAnswers.add(q.getCorrectAnswer());
            if (q.getIncorrectAnswers() != null) tempAnswers.addAll(q.getIncorrectAnswers());
            while (tempAnswers.size() < 4) tempAnswers.add("");
            if (tempAnswers.size() > 4) tempAnswers = tempAnswers.subList(0, 4);
            Collections.shuffle(tempAnswers);
            final List<String> answers = tempAnswers;

            // Set buttons and main question text
            binding.textViewMainQuestionDisplay.setText(html(q.getQuestion()));
            binding.textViewA.setText(html(answers.get(0)));
            binding.textViewB.setText(html(answers.get(1)));
            binding.textViewC.setText(html(answers.get(2)));
            binding.textViewD.setText(html(answers.get(3)));

            // Set on click listeners for the answer buttons
            final String correct = q.getCorrectAnswer() == null ? "" : q.getCorrectAnswer();
            binding.textViewA.setOnClickListener(v -> answerClick(binding.textViewA, correct));
            binding.textViewB.setOnClickListener(v -> answerClick(binding.textViewB, correct));
            binding.textViewC.setOnClickListener(v -> answerClick(binding.textViewC, correct));
            binding.textViewD.setOnClickListener(v -> answerClick(binding.textViewD, correct));
        });
    }

    /**
     * Removes click listeners from all answer buttons.
     */
    private void clearAnswerClickListeners() {
        binding.textViewA.setOnClickListener(null);
        binding.textViewB.setOnClickListener(null);
        binding.textViewC.setOnClickListener(null);
        binding.textViewD.setOnClickListener(null);
    }

    /**
     * Handles answer selection, updates score, and loads the next question.
     * @param button The pressed answer button.
     * @param correctAnswer The correct answer text.
     */
    private void answerClick(Button button, String correctAnswer) {
        // Depending on answer, notify user if it's correct or not
        String selectedAnswer = button.getText().toString();
        if (selectedAnswer.equals(correctAnswer)) {
            toastMaker("Correct!");
            button.setBackgroundColor(Color.GREEN);
            repository.recordResult(userId, 1, 0);
        } else {
            toastMaker("Incorrect!");
            button.setBackgroundColor(Color.RED);
            repository.recordResult(userId, 0, 1);
        }

        // Wait 1 second before preparing the next question or ending quiz
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Reset button color
                button.setBackgroundColor(defaultColor);

                // End quiz if all questions were answered
                if (currentQuestionIndex >= TOTAL_QUESTIONS) {
                    toastMaker("Quiz complete!");
                    finish();
                    return;
                }

                // Else, prepare for next question
                currentQuestionIndex++;
                updateQuestionCountDisplay();
                displayQuestion(difficulty);
            }
        }, 1000);
    }

    /**
     * Updates the on-screen question counter display.
     */
    private void updateQuestionCountDisplay() {
        TextView countView = findViewById(R.id.questionCount);
        countView.setText(currentQuestionIndex + "/" + TOTAL_QUESTIONS);
    }

    /**
     * Converts HTML-formatted text into a displayable CharSequence.
     * @param str Raw HTML string to convert.
     */
    private CharSequence html(String str) {
        if (str == null) str = "";
        return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    /**
     * Shows a short toast message.
     * @param message Text to display in the toast.
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates an intent for launching the Questions activity.
     * @param context Context used to build the intent.
     * @param userId User ID passed to the activity.
     * @param difficulty Chosen difficulty level.
     * @return Intent configured for QuestionsActivity.
     */
    static Intent questionsIntentFactory(Context context, int userId, String difficulty) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        return intent;
    }

    /**
     * Logs out the user and returns to the login screen.
     */
    private void logout() {
        SharedPreferences sp = getSharedPreferences(getString(R.string.preference_file_key), MODE_PRIVATE);
        sp.edit().putInt(getString(R.string.preference_userId_key), LOGGED_OUT).apply();
        Intent intent = LoginActivity.loginIntentFactory(getApplicationContext());
        startActivity(intent);
        finish();
    }
}