package com.example.crash_game;

import android.content.Context;
import android.widget.ImageView;

public class Player extends GameObject {


    public Player(Context context) {
        super(new ImageView(context));
        getView().setImageResource(R.drawable.car);
    }
}
