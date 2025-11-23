package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.text.HtmlCompat;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.Question;
import com.example.TriviaBattler.databinding.ActivityQuestionsBinding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private AppRepository repository;

    private int userId = -1;
    private String difficulty = null;

    private LiveData<Question> currentLiveQ;
    public static final String EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY";
    public static final String EXTRA_USER_ID = "USER_ID";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());

        userId = getIntent().getIntExtra(EXTRA_USER_ID, -1);
        difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);

        displayQuestion(difficulty); // show the first question

        binding.questionsBackButton.setOnClickListener(v ->
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), userId))
        );
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

            binding.textViewA.setOnClickListener(v -> handleAnswerClick(answers.get(0), correct));
            binding.textViewB.setOnClickListener(v -> handleAnswerClick(answers.get(1), correct));
            binding.textViewC.setOnClickListener(v -> handleAnswerClick(answers.get(2), correct));
            binding.textViewD.setOnClickListener(v -> handleAnswerClick(answers.get(3), correct));
        });
    }

    private void clearAnswerClickListeners() {
        binding.textViewA.setOnClickListener(null);
        binding.textViewB.setOnClickListener(null);
        binding.textViewC.setOnClickListener(null);
        binding.textViewD.setOnClickListener(null);
    }

    private void handleAnswerClick(String selectedAnswer, String correctAnswer) {
        // Compare raw strings (we displayed HTML, but we’re comparing the original values)
        if (selectedAnswer != null && selectedAnswer.equals(correctAnswer)) {
            toastMaker("✅ Correct!");
            repository.recordResult(userId, 1, 0);
        } else {
            toastMaker("❌ Incorrect!");
            repository.recordResult(userId, 0, 1);
        }

        // Load the next question
        displayQuestion(difficulty);
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