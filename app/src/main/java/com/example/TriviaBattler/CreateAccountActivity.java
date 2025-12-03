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
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.example.TriviaBattler.database.AppRepository;
import com.example.TriviaBattler.database.entities.User;
import com.example.TriviaBattler.databinding.ActivityCreateAccountBinding;
import com.example.TriviaBattler.databinding.ActivityMainBinding;

public class CreateAccountActivity extends AppCompatActivity {

    private ActivityCreateAccountBinding binding;
    private AppRepository repository;

    /***
     * Sets up on clickers for the 'Back' button and the 'Create Account' button.
     *
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        repository = AppRepository.getRepository(getApplication());


        binding.backCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            }
        });


        binding.createAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                verifyNewUser();
            }
        });
    }

    /***
     * Checks if the username and password that was given is valid.<br>
     * Valid being not empty, not already existing in database, and both passwords matching.<br><br>
     * If valid, a user is created and added to the database. Goes back to Login Activity.<br>
     */
    private void verifyNewUser() {
        String username = binding.usernameCreateAccountEditText.getText().toString().trim().toLowerCase();
        String password = binding.passwordCreateAccountEditText.getText().toString();
        // CHECK FOR EMPTY USERNAME OR PASSWORD
        if (username.isEmpty()) {
            toastMaker("Please enter a username.");
            resetFields();
            return;
        }
        if (password.isEmpty()) {
            toastMaker("Please enter a password.");
            resetFields();
            return;
        }
        // CHECK FOR MATCHING PASSWORDS
        if (!password.equals(binding.confirmPasswordCreateAccountEditText.getText().toString())) {
            toastMaker("Passwords do not match.");
            resetFields();
            return;
        }
        // DB CHECKING...
        LiveData<User> userObserver = repository.getUserByUserName(username);
        userObserver.observe(this, user -> {
            userObserver.removeObservers(this);
            if (user != null) {
                // USER ALREADY IN DB
                toastMaker(String.format("%s is taken.%nChoose a different username!", username));
                resetFields();
            }
            else {
                // ADD ACCOUNT TO DB
                User newUser = new User(username, password);
                repository.insertUser(newUser);
                toastMaker("Account successfully created!");
                // START LOGIN ACTIVITY
                startActivity(LoginActivity.loginIntentFactory(getApplicationContext()));
            }
        });
    }

    /***
     * Resets all text fields.
     */
    private void resetFields() {
        binding.usernameCreateAccountEditText.setText("");
        binding.passwordCreateAccountEditText.setText("");
        binding.confirmPasswordCreateAccountEditText.setText("");
    }


    /***
     * Displays a toast on screen for the short amount of duration.
     *
     * @param message the message to be displayed
     */
    private void toastMaker(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    /***
     * Creates a new Intent for the Create Account Activity.
     *
     * @param context the application context
     * @return the new Intent
     */
    static Intent createAccountIntentFactory(Context context) {
        return new Intent(context, CreateAccountActivity.class);
    }
}