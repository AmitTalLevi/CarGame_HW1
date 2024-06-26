package com.example.crash_game;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity extends AppCompatActivity implements Game.OnMain {


    private Game game;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton moveLeftBtn = findViewById(R.id.leftArrow);
        FloatingActionButton moveRightBtn = findViewById(R.id.rightArrow);
        game = new Game(this);


        moveLeftBtn.setOnClickListener((v) -> {
            game.movePlayerLeft();
        });
        moveRightBtn.setOnClickListener((v) -> {
            game.movePlayerRight();
        });

    }

    @Override
    public void runOnMainThread(Runnable r) {
        runOnUiThread(r);
    }
}