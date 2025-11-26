package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class QuestionsActivity extends AppCompatActivity {

    private static final int LOGGED_OUT = -1;
    private int loggedInUserId = -LOGGED_OUT;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(findViewById(R.id.toolbar));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        repository = AppRepository.getRepository(getApplication());

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);

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

        if (user == null) {
            userItem.setVisible(false);
            adminItem.setVisible(false);
            return true;
        }

        userItem.setVisible(true);
        View actionView = userItem.getActionView();
        if (actionView != null) {
            TextView usernameView = actionView.findViewById(R.id.usernameTitle);
            if (usernameView != null) usernameView.setText(user.getUsername());
        }

        adminItem.setVisible(user.isAdmin());
        return true;
    }

    //Options for menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            finish();
            return true;
        } else if (id == R.id.logout) {
            startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            finish();
            return true;
        } else if (id == R.id.stats) {
            Intent intent = Statistics.statsIntentFactory(this, loggedInUserId);
            startActivity(intent);
            return true;
        }else if (id == R.id.admin) {
            Intent intent = new Intent(this, AdminLanding.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void displayQuestion(String difficulty) {
        if (currentLiveQ != null) {
            currentLiveQ.removeObservers(this);
        }

        currentLiveQ = repository.getRandomQuestionByDifficulty(difficulty);
        currentLiveQ.observe(this, q -> {
            if (q == null) {
                binding.textViewMainQuestionDisplay.setText("No question found for: " + difficulty);
                binding.textViewA.setText("");
                binding.textViewB.setText("");
                binding.textViewC.setText("");
                binding.textViewD.setText("");
                clearAnswerClickListeners();
                return;
            }

            List<String> tempAnswers = new ArrayList<>();
            if (q.getCorrectAnswer() != null) tempAnswers.add(q.getCorrectAnswer());
            if (q.getIncorrectAnswers() != null) tempAnswers.addAll(q.getIncorrectAnswers());
            while (tempAnswers.size() < 4) tempAnswers.add("");
            if (tempAnswers.size() > 4) tempAnswers = tempAnswers.subList(0, 4);
            Collections.shuffle(tempAnswers);
            final List<String> answers = tempAnswers;

            binding.textViewMainQuestionDisplay.setText(html(q.getQuestion()));
            binding.textViewA.setText(html(answers.get(0)));
            binding.textViewB.setText(html(answers.get(1)));
            binding.textViewC.setText(html(answers.get(2)));
            binding.textViewD.setText(html(answers.get(3)));

            final String correct = q.getCorrectAnswer() == null ? "" : q.getCorrectAnswer();

            binding.textViewA.setOnClickListener(v -> answerClick(answers.get(0), correct));
            binding.textViewB.setOnClickListener(v -> answerClick(answers.get(1), correct));
            binding.textViewC.setOnClickListener(v -> answerClick(answers.get(2), correct));
            binding.textViewD.setOnClickListener(v -> answerClick(answers.get(3), correct));
        });
    }

    private void clearAnswerClickListeners() {
        binding.textViewA.setOnClickListener(null);
        binding.textViewB.setOnClickListener(null);
        binding.textViewC.setOnClickListener(null);
        binding.textViewD.setOnClickListener(null);
    }

    private void answerClick(String selectedAnswer, String correctAnswer) {
        if (selectedAnswer != null && selectedAnswer.equals(correctAnswer)) {
            toastMaker("Correct!");
            repository.recordResult(userId, 1, 0);
        } else {
            toastMaker("Incorrect!");
            repository.recordResult(userId, 0, 1);
        }

        if (currentQuestionIndex >= TOTAL_QUESTIONS) {
            Toast.makeText(this, "Quiz complete!", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        currentQuestionIndex++;
        updateQuestionCountDisplay();
        displayQuestion(difficulty);
    }

    private void updateQuestionCountDisplay() {
        TextView countView = findViewById(R.id.questionCount);
        countView.setText(currentQuestionIndex + "/" + TOTAL_QUESTIONS);
    }

    private CharSequence html(String str) {
        if (str == null) str = "";
        return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent questionsIntentFactory(Context context, int userId, String difficulty) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(EXTRA_USER_ID, userId);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        return intent;
    }
}