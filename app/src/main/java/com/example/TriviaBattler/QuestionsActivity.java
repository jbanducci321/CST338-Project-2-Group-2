package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.text.HtmlCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

    public static final String EXTRA_DIFFICULTY = "EXTRA_DIFFICULTY";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());

        int userId = getIntent().getIntExtra("USER_ID", -1);
        String difficulty = getIntent().getStringExtra(EXTRA_DIFFICULTY);

        displayQuestion(difficulty); //Displays the question with the specified difficulty

        binding.questionsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), userId));
            }
        });

    }

    private void displayQuestion (String difficulty) {
        LiveData<Question> liveQ = repository.getRandomQuestionByDifficulty(difficulty);
        liveQ.observe(this, q -> {
            if (q == null) {
                binding.textViewMainQuestionDisplay.setText("No question found for: " + difficulty);
                binding.textViewA.setText("");
                binding.textViewB.setText("");
                binding.textViewC.setText("");
                binding.textViewD.setText("");
                return;
            }

            // Build 4 options (1 correct + 3 incorrect), and shuffle
            List<String> answers = new ArrayList<>();
            if (q.getCorrectAnswer() != null) answers.add(q.getCorrectAnswer());
            if (q.getIncorrectAnswers() != null) answers.addAll(q.getIncorrectAnswers());
            while (answers.size() < 4) answers.add("");
            if (answers.size() > 4) answers = answers.subList(0, 4);
            Collections.shuffle(answers);


            binding.textViewMainQuestionDisplay.setText(html(q.getQuestion()));
            binding.textViewA.setText(html(answers.get(0)));
            binding.textViewB.setText(html(answers.get(1)));
            binding.textViewC.setText(html(answers.get(2)));
            binding.textViewD.setText(html(answers.get(3)));
        });
    }

    private CharSequence html(String str) {
        if (str == null) str = "";
        return HtmlCompat.fromHtml(str, HtmlCompat.FROM_HTML_MODE_LEGACY);
    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent questionsIntentFactory(Context context, String difficulty) {
        Intent intent = new Intent(context, QuestionsActivity.class);
        intent.putExtra(EXTRA_DIFFICULTY, difficulty);
        return intent;
    }

}