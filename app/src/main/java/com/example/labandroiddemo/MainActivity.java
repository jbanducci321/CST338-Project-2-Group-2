package com.example.labandroiddemo;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.labandroiddemo.database.AppRepository;
import com.example.labandroiddemo.database.entities.User;
import com.example.labandroiddemo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {


    private static final int LOGGED_OUT = -1;
    private ActivityMainBinding binding;
    private AppRepository repository;

    private int loggedInUserId = -1;
    private User user;

    public static final String TAG = "DAC_APP";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

    }

    private void loginUser(Bundle savedInstanceState) {

    }

}