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
import com.mhamza007.videoapp.db.User;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    EditText email_et, password_et;
    Button login_btn;
    ProgressBar progress;

    private ViewModel viewModel;

    private SharedPref sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);
        sharedPref = new SharedPref(this);

        email_et = findViewById(R.id.email_et);
        password_et = findViewById(R.id.password_et);
        login_btn = findViewById(R.id.login_btn);
        progress = findViewById(R.id.progress);

        email_et.setText(sharedPref.getEmail());
        password_et.setText(sharedPref.getPassword());

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);

                String email = email_et.getText().toString().trim();
                String password = password_et.getText().toString().trim();

                loginUser(email, password);
            }
        });
    }

    private void loginUser(String email, String password) {
        User user = new User(email, password);

        if (userExists(user)) {
            progress.setVisibility(View.GONE);

            sharedPref.setEmail(email);
            sharedPref.setPassword(password);

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("email", user.getEmail());
            intent.putExtra("password", user.getPassword());
            intent.putExtra("count", user.getCount());
            intent.putExtra("video", user.getVideo());
            startActivity(intent);
        } else {
            Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show();
            progress.setVisibility(View.GONE);
        }
    }

    private boolean userExists(User user) {
        boolean userExists = true;
        List<User> allUsers = viewModel.getAllUsers();
        if (allUsers != null && allUsers.size() > 0) {
            for (User user1 : allUsers) {
                userExists = user.getEmail().equals(user1.getEmail())
                        && user.getPassword().equals(user1.getPassword());
            }
        } else {
            userExists = false;
        }
        return userExists;
    }
}