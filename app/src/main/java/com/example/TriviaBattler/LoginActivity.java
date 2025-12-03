package com.example.TriviaBattler;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityLoginBinding;
import com.example.TriviaBattler.databinding.ActivityMainBinding;

public class LoginActivity extends AppCompatActivity {

    private static final String MAIN_ACTIVITY_USER_ID = "com.example.labandroiddemo.MAIN_ACTIVITY_USER_ID";
    static final String SAVED_INSTANCE_STATE_USERID_KEY = "com.example.labandroiddemo.SAVED_INSTANCE_STATE_USERID_KEY";
    private static final int LOGGED_OUT = -1;

    private int loggedInUserId = -LOGGED_OUT;
    private User user;

    public static final String TAG = "DAC_APP";

    String preference_file_key = "preference_file_key";

    private ActivityLoginBinding binding;
    private AppRepository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());
        loginUser(savedInstanceState);

        binding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyUser();
            }
        });

        binding.createNewAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(CreateAccountActivity
                        .createAccountIntentFactory(getApplicationContext()));
            }
        });
    }

    /**
     * Logs in the user using stored or passed userId values.
     * @param savedInstanceState Restored data containing a previous userId, if available.
     */
    public void loginUser(Bundle savedInstanceState) {
        // === GymLog-style userId retrieval order: SharedPrefs -> savedInstance -> Intent ===
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);

        loggedInUserId = sharedPreferences.getInt(
                getString(R.string.preference_userId_key), LOGGED_OUT);

        if (loggedInUserId == LOGGED_OUT && savedInstanceState != null
                && savedInstanceState.containsKey(SAVED_INSTANCE_STATE_USERID_KEY)) {
            loggedInUserId = savedInstanceState.getInt(SAVED_INSTANCE_STATE_USERID_KEY, LOGGED_OUT);
        }

        if (loggedInUserId == LOGGED_OUT) {
            loggedInUserId = getIntent().getIntExtra(MAIN_ACTIVITY_USER_ID, LOGGED_OUT);
        }else{
            startActivity(MainActivity
                    .mainActivityIntentFactory(getApplicationContext(), loggedInUserId));
        }

    }

    /**
     * Verifies login credentials and signs in valid users.
     */
    private void verifyUser() {
        String username = binding.usernameLoginEditText.getText().toString().trim().toLowerCase();
        if (username.isEmpty()) {
            toastMaker("Please enter a username");
            return;
        }

        LiveData<User> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, user -> {
            if (user != null) {
                String password = binding.passwordLoginEditText.getText().toString();
                if (password.equals(user.getPassword())) {
                    startActivity(MainActivity
                            .mainActivityIntentFactory(getApplicationContext(), user.getUserId()));
                }
                else {
                    toastMaker("Invalid password");
                    binding.passwordLoginEditText.setSelection(0);
                }
            } else {
                toastMaker(String.format("%s is not a valid username.", username));
                binding.usernameLoginEditText.setSelection(0);
            }
        });
    }

    /**
     * Displays a short toast message.
     * @param message Text to show in the toast.
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates an intent for launching this activity.
     * @param context Context used to build the intent.
     */
    static Intent loginIntentFactory(Context context) {
        return new Intent(context, LoginActivity.class);
    }
}