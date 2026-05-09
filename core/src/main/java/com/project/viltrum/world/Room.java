package com.project.viltrum.world;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Projectile;
import com.project.viltrum.factory.EnemyFactory;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int roomNumber;
    private Texture background;
    private Texture bulletTexture;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Rectangle> obstacles = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private Boss boss;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        background = new Texture("backgrounds/room" + roomNumber + ".png");
        bulletTexture = createBulletTexture();
        spawn();
    }

    private void spawn() {
        if (roomNumber == 1) {
            enemies.add(EnemyFactory.createFlaxan(250, 300));
            enemies.add(EnemyFactory.createFlaxan(850, 300));
            addHangarObstacles();
        }

        if (roomNumber == 2) {
            enemies.add(EnemyFactory.createFlaxan(220, 400));
            enemies.add(EnemyFactory.createFlaxan(700, 250));
            enemies.add(EnemyFactory.createFlaxan(950, 430));
            addHangarObstacles();
        }

        if (roomNumber == 3) {
            enemies.add(EnemyFactory.createFlaxan(250, 300));
            enemies.add(EnemyFactory.createFlaxan(850, 300));
            boss = EnemyFactory.createConquest(650, 420);
            addHangarObstacles();
        }

        if (roomNumber == 4) {
            boss = EnemyFactory.createThragg(650, 420);
            addCommandRoomObstacles();
        }
    }

    public void update(float delta, Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(delta, player, obstacles);

            Projectile projectile = enemy.shootAt(player);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        enemies.removeIf(Enemy::isDead);

        if (boss != null) {
            boss.update(delta, player, obstacles);

            if (boss.isDead()) {
                boss = null;
            }
        }

        for (Projectile projectile : projectiles) {
            projectile.update(delta, player, obstacles);
        }

        projectiles.removeIf(projectile -> !projectile.isActive());
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, 1280, 720);

        for (Enemy enemy : enemies) {
            enemy.render(batch);
        }

        if (boss != null) {
            boss.render(batch);
        }

        for (Projectile projectile : projectiles) {
            projectile.render(batch, bulletTexture);
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

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public void dispose() {
        background.dispose();
        bulletTexture.dispose();

        for (Enemy enemy : enemies) {
            enemy.dispose();
        }

        if (boss != null) {
            boss.dispose();
        }
    }

    private void addHangarObstacles() {
        obstacles.add(new Rectangle(416, 493, 125, 58));
        obstacles.add(new Rectangle(754, 420, 164, 135));
        obstacles.add(new Rectangle(1020, 439, 68, 46));
        obstacles.add(new Rectangle(275, 408, 78, 62));
        obstacles.add(new Rectangle(552, 393, 178, 55));
        obstacles.add(new Rectangle(968, 306, 82, 105));
        obstacles.add(new Rectangle(256, 215, 150, 78));
        obstacles.add(new Rectangle(822, 184, 122, 50));
        obstacles.add(new Rectangle(1090, 24, 86, 45));
        obstacles.add(new Rectangle(259, 638, 55, 50));
        obstacles.add(new Rectangle(438, 511, 95, 42));
    }

    private void addCommandRoomObstacles() {
        obstacles.add(new Rectangle(468, 295, 322, 135));
        obstacles.add(new Rectangle(318, 414, 190, 66));
        obstacles.add(new Rectangle(275, 240, 160, 75));
        obstacles.add(new Rectangle(900, 384, 176, 72));
        obstacles.add(new Rectangle(870, 195, 150, 72));
        obstacles.add(new Rectangle(957, 603, 112, 70));
        obstacles.add(new Rectangle(640, 125, 68, 50));
        obstacles.add(new Rectangle(520, 165, 72, 58));
        obstacles.add(new Rectangle(410, 542, 130, 66));
        obstacles.add(new Rectangle(606, 498, 92, 62));
    }

    private Texture createBulletTexture() {
        Pixmap pixmap = new Pixmap(32, 16, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(1f, 0.92f, 0.25f, 1);
        pixmap.fillRectangle(6, 5, 20, 6);
        pixmap.setColor(1f, 0.45f, 0.05f, 1);
        pixmap.fillRectangle(0, 6, 8, 4);
        pixmap.setColor(1f, 1f, 0.78f, 1);
        pixmap.fillRectangle(20, 4, 10, 8);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
