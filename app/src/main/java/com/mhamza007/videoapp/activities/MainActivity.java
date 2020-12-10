package com.mhamza007.videoapp.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.mhamza007.videoapp.R;

public class MainActivity extends AppCompatActivity {

    Button counter_screen, video_player;

    String email, password, video;
    int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            email = getIntent().getStringExtra("email");
            password = getIntent().getStringExtra("password");
            count = getIntent().getIntExtra("count", 0);
            video = getIntent().getStringExtra("video");
        } catch (Exception e) {
            e.printStackTrace();
        }

        counter_screen = findViewById(R.id.counter_screen);
        video_player = findViewById(R.id.video_player);

        counter_screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CounterScreenActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("count", count);
                intent.putExtra("video", video);
                startActivity(intent);
            }
        });

        video_player.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, VideoPlayerActivity.class);
                intent.putExtra("email", email);
                intent.putExtra("password", password);
                intent.putExtra("count", count);
                intent.putExtra("video", video);
                startActivity(intent);
            }
        });
    }
}