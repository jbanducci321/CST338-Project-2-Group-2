package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.databinding.ActivityQuestionsBinding;

public class QuestionsActivity extends AppCompatActivity {

    private ActivityQuestionsBinding binding;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuestionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());

        int userId = getIntent().getIntExtra("USER_ID", -1);

        binding.questionsBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), userId));
            }
        });

    }

    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    static Intent questionsIntentFactory(Context context) {
        return new Intent(context, QuestionsActivity.class);
    }

}