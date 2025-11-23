package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.databinding.ActivityAddQuestionsAdminBinding;

public class AddQuestionsAdminActivity extends AppCompatActivity {

    private ActivityAddQuestionsAdminBinding binding;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddQuestionsAdminBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());
        int userId = getIntent().getIntExtra("USER_ID", -1);

        binding.buttonTempBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(MainActivity.mainActivityIntentFactory(getApplicationContext(), userId));
            }
        });

    }

    static Intent addQuestionIntentFactory(Context context) {
        Intent intent = new Intent(context, AddQuestionsAdminActivity.class);
        return intent;
    }

}