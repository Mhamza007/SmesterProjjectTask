package com.mhamza007.videoapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.mhamza007.videoapp.R;
import com.mhamza007.videoapp.SharedPref;
import com.mhamza007.videoapp.ViewModel;
import com.mhamza007.videoapp.db.Database;
import com.mhamza007.videoapp.db.User;

import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    EditText email_et, password_et, password_et2;
    Button signup_btn;
    ProgressBar progress;

    private ViewModel viewModel;
//    private Database database;
    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        sharedPref = new SharedPref(this);

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        password_et2 = findViewById(R.id.password_et2);
        signup_btn = findViewById(R.id.signup_btn);
        progress = findViewById(R.id.progress);

        signup_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                String email = email_et.getText().toString().trim();
                String password1 = password_et.getText().toString().trim();
                String password2 = password_et2.getText().toString().trim();

                signUpUser(email, password1, password2);
            }
        });
    }

    private void signUpUser(String email, String password1, String password2) {
        if (email.isEmpty() || password1.isEmpty() || password2.isEmpty()) {
            Toast.makeText(this, "Some Fields are empty. All Fields are required", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        } else if (!password1.equals(password2)) {
            Toast.makeText(this, "Password doesn't match", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        } else if (userAlreadyExists(email)) {
            Toast.makeText(this, "User Already Exists", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        } else {
            User user = new User(
                    email,
                    password2,
                    0,
                    ""
            );
            viewModel.registerNewUser(user);
            sharedPref.setEmail(email);
            sharedPref.setPassword(password2);

            Toast.makeText(this, "New User registered", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
            startActivity(new Intent(this, LoginActivity.class));
        }
    }

    private boolean userAlreadyExists(String email) {
        boolean userExists = true;
        List<User> allUsers = viewModel.getAllUsers();
        if (allUsers != null && allUsers.size() > 0) {
            for (User user1 : allUsers) {
                userExists = email.equals(user1.getEmail());
            }
        } else {
            userExists = false;
        }
        return userExists;
    }
}