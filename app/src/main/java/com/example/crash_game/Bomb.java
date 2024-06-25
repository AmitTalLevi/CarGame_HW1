package com.example.crash_game;

import android.content.Context;
import android.widget.ImageView;

public class Bomb extends GameObject {
    public Bomb(Context context) {
        super(new ImageView(context));
        getView().setImageResource(R.drawable.bomb);
    }
}
