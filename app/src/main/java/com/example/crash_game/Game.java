package com.example.crash_game;

import android.content.Context;
import android.os.Handler;
import android.view.View;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.Random;

public class Game {

    public interface OnMain {
        void runOnMainThread(Runnable r);
    }

    public static final int LANES = 3;
    public static final int ROWS = 12;
    public static final int MAX_LIVES = 3;

    private GameObject[][] gameObjects;
    private GridLayout gameLayout;
    private LinearLayout livesLayout;
    private int[] bomb_lanes;
    private boolean[] active_bomb_lanes;
    private int player_lane;
    private OnMain onMain;
    private int lives;
    private Context context;

    private Handler handler = new Handler();
    private Random rand = new Random();

    public Game(MainActivity context) {
        this.context = context;

        player_lane = LANES / 2;
        lives = 3;
        bomb_lanes = new int[LANES];
        active_bomb_lanes = new boolean[LANES];
        livesLayout = context.findViewById(R.id.livesLayout);
        gameLayout = context.findViewById(R.id.gameLayout);

        gameLayout.setRowCount(ROWS);
        gameLayout.setColumnCount(LANES);

        gameObjects = new GameObject[ROWS][LANES];
        gameObjects[ROWS - 1][player_lane] = new Player(context);
        gameObjects[ROWS - 1][player_lane].setPosition(ROWS - 1, LANES / 2);

        for (int i = 0; i < LANES; i++) {
            gameObjects[0][i] = new Bomb(context);
            gameObjects[0][i].setPosition(0, i);
        }

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < LANES; j++) {
                if (gameObjects[i][j] == null) {
                    gameObjects[i][j] = new GameObject(new ImageView(context));
                    gameObjects[i][j].setPosition(i, j);
                }
                gameLayout.addView(gameObjects[i][j].getView());
            }
        }
        onMain = context;

        startBombTask();
    }

    private void startBombTask() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (activeLanes() < LANES - 1) {
                    int randomLane = Math.min(LANES - 1, rand.nextInt(LANES));
                    if (active_bomb_lanes[randomLane]) {
                        handler.postDelayed(this, 500);
                        return;
                    }
                    active_bomb_lanes[randomLane] = true;
                    final int movementLane = randomLane;

                    moveBombTask(movementLane);
                }
                handler.postDelayed(this, 500);
            }
        }, 500);
    }

    private void moveBombTask(final int lane) {
        final int delay = 300;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onMain.runOnMainThread(() -> {
                    if (!moveBomb(lane)) {
                        active_bomb_lanes[lane] = false;
                    } else {
                        handler.postDelayed(this, delay); // Schedule the next move
                    }
                });
            }
        }, delay);
    }

    private int activeLanes() {
        int active = 0;
        for (boolean activeLane : active_bomb_lanes)
            if (activeLane) active++;
        return active;
    }

    public void movePlayerLeft() {
        if (player_lane == 0) return;
        int row = ROWS - 1;
        GameObject o = gameObjects[row][player_lane - 1];
        if (o instanceof Bomb) {
            resetBomb(player_lane - 1);
            playerHit();

        }
        swap(row, player_lane, row, player_lane - 1);
        player_lane--;
    }

    public void movePlayerRight() {
        if (player_lane == LANES - 1) return;
        int row = ROWS - 1;
        GameObject o = gameObjects[row][player_lane + 1];
        if (o instanceof Bomb) {
            resetBomb(player_lane + 1);
            playerHit();
        }
        swap(row, player_lane, row, player_lane + 1);
        player_lane++;
    }

    private void playerHit() {
        GameUtils.vibrate(context);
        GameUtils.toast(context,"YOU CRASH");

        lives--;
        if (lives == 0)
            lives = MAX_LIVES;
        updateLives();
    }

    private void resetBomb(int lane) {
        int row = bomb_lanes[lane];
        bomb_lanes[lane] = 0;
        swap(row, lane, 0, lane);
    }

    private boolean moveBomb(int lane) {
        int row = bomb_lanes[lane];
        if (row + 1 >= ROWS) {
            resetBomb(lane);
            return false; // moved up (reset)
        }

        // check collision
        GameObject o = gameObjects[row + 1][lane];
        if (o instanceof Player) {
            resetBomb(lane);
            playerHit();
            updateLives();
            return false;
        }

        bomb_lanes[lane]++;
        swap(row, lane, row + 1, lane);
        return true;
    }

    private void updateLives() {
        for (int i = 0; i < MAX_LIVES; i++) {
            livesLayout.getChildAt(i).setVisibility(View.INVISIBLE);
        }
        for (int i = lives - 1; i >= 0; i--) {
            livesLayout.getChildAt(i).setVisibility(View.VISIBLE);
        }
    }

    private void swap(int r1, int c1, int r2, int c2) {
        GameObject temp = gameObjects[r1][c1];
        gameObjects[r1][c1] = gameObjects[r2][c2];
        gameObjects[r2][c2] = temp;
        gameObjects[r1][c1].setPosition(r1, c1);
        gameObjects[r2][c2].setPosition(r2, c2);
    }
}
