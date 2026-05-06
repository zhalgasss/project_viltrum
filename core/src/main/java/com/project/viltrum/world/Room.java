package com.project.viltrum.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.entities.*;
import com.project.viltrum.factory.EnemyFactory;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int roomNumber;
    private Texture background;

    private List<Enemy> enemies = new ArrayList<>();
    private Boss boss;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;

        if (Gdx.files.internal("rooms/room" + roomNumber + ".png").exists()) {
            background = new Texture("rooms/room" + roomNumber + ".png");
        }

        spawn();
    }

    private void spawn() {
        if (roomNumber == 1) {
            enemies.add(EnemyFactory.createFlaxan(250, 300));
            enemies.add(EnemyFactory.createFlaxan(850, 300));
        }

        if (roomNumber == 2) {
            enemies.add(EnemyFactory.createFlaxan(220, 400));
            enemies.add(EnemyFactory.createFlaxan(700, 250));
            enemies.add(EnemyFactory.createFlaxan(950, 430));
        }

        if (roomNumber == 3) {
            enemies.add(EnemyFactory.createFlaxan(250, 300));
            enemies.add(EnemyFactory.createFlaxan(850, 300));
            boss = EnemyFactory.createConquest(650, 420);
        }

        if (roomNumber == 4) {
            boss = EnemyFactory.createThragg(650, 420);
        }
    }

    public void update(float delta, Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(delta, player);
        }

        enemies.removeIf(Enemy::isDead);

        if (boss != null) {
            boss.update(delta, player);

            if (boss.isDead()) {
                boss = null;
            }
        }
    }

    public void render(SpriteBatch batch) {
        if (background != null) {
            batch.draw(background, 0, 0, 1280, 720);
        }

        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }

        if (boss != null) {
            boss.render(batch);
        }
    }

    public boolean isCleared() {
        return enemies.isEmpty() && boss == null;
    }

    public List<Enemy> getEnemies() {
        List<Enemy> all = new ArrayList<>(enemies);

        if (boss != null) {
            all.add(boss);
        }

        return all;
    }

    public int getRoomNumber() {
        return roomNumber;
    }

    public void dispose() {
        if (background != null) {
            background.dispose();
        }
    }
}
