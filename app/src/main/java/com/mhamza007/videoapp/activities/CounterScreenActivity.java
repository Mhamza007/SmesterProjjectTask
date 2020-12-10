package com.mhamza007.videoapp.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProviders;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mhamza007.videoapp.R;
import com.mhamza007.videoapp.ViewModel;
import com.mhamza007.videoapp.db.User;

public class CounterScreenActivity extends AppCompatActivity {

    ImageView back;
    TextView count_text;
    Button increment, reset;
    ProgressBar progress;

    String email, password, video;
    int count;

    private ViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_counter_screen);

        viewModel = ViewModelProviders.of(this).get(ViewModel.class);

        try {
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            count = getIntent().getIntExtra("count", 0);
            video = getIntent().getStringExtra("video");
        } catch (Exception e) {
            e.printStackTrace();
        }

        back = findViewById(R.id.back);
        count_text = findViewById(R.id.count_text);
        increment = findViewById(R.id.increment);
        reset = findViewById(R.id.reset);
        progress = findViewById(R.id.progress);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        count_text.setText("" + count);

        increment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                incrementCounter();
            }
        });

        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progress.setVisibility(View.VISIBLE);
                resetCounter();
            }
        });
    }

    private void incrementCounter() {
        count++;
        viewModel.update(new User(email, password, count, video));
        count_text.setText("" + count);
        progress.setVisibility(View.GONE);
    }

    private void resetCounter() {
        viewModel.update(new User(email, password, 0, video));
        count = 0;
        count_text.setText("" + 0);
        progress.setVisibility(View.GONE);
    }
}