package com.project.viltrum.world;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.HealthPickup;
import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Projectile;
import com.project.viltrum.factory.EnemyFactory;

import java.util.ArrayList;
import java.util.List;

public class Room {
    private int roomNumber;
    private Texture background;
    private Texture bulletTexture;
    private Texture healthTexture;

    private List<Enemy> enemies = new ArrayList<>();
    private List<Rectangle> obstacles = new ArrayList<>();
    private List<Projectile> projectiles = new ArrayList<>();
    private List<HealthPickup> healthPickups = new ArrayList<>();
    private Boss boss;
    private float healthSpawnTimer = 4f;

    public Room(int roomNumber) {
        this.roomNumber = roomNumber;
        background = new Texture("backgrounds/room" + roomNumber + ".png");
        bulletTexture = createBulletTexture();
        healthTexture = createHealthTexture();
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
            boss = EnemyFactory.createConquest(640, 520);
            addReactorRoomObstacles();
        }

        if (roomNumber == 4) {
            boss = EnemyFactory.createThragg(815, 470);
            addCommandRoomObstacles();
        }

        spawnHealthPickup();
    }

    public void update(float delta, Player player) {
        for (Enemy enemy : enemies) {
            enemy.update(delta, player, obstacles, enemies);

            Projectile projectile = enemy.shootAt(player);
            if (projectile != null) {
                projectiles.add(projectile);
            }
        }

        enemies.removeIf(Enemy::canRemove);

        if (boss != null) {
            boss.update(delta, player, obstacles, enemies, projectiles);

            if (boss.canRemove()) {
                boss = null;
            }
        }

        for (Projectile projectile : projectiles) {
            projectile.update(delta, player, getEnemies(), obstacles);
        }

        projectiles.removeIf(projectile -> !projectile.isActive());

        updateHealthPickups(delta, player);
    }

    public void render(SpriteBatch batch) {
        batch.draw(background, 0, 0, 1280, 720);

        for (HealthPickup healthPickup : healthPickups) {
            healthPickup.render(batch, healthTexture);
        }

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

    public Boss getBoss() {
        return boss;
    }

    public boolean hasBossFight() {
        return boss != null && !boss.isDead();
    }

    public List<Rectangle> getObstacles() {
        return obstacles;
    }

    public List<Projectile> getProjectiles() {
        return projectiles;
    }

    public void dispose() {
        background.dispose();
        bulletTexture.dispose();
        healthTexture.dispose();

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
        obstacles.add(new Rectangle(500, 315, 250, 72));
        obstacles.add(new Rectangle(295, 235, 110, 55));
        obstacles.add(new Rectangle(910, 400, 120, 58));
        obstacles.add(new Rectangle(950, 610, 90, 52));
    }

    private void addReactorRoomObstacles() {
        obstacles.add(new Rectangle(0, 650, 1280, 70));
        obstacles.add(new Rectangle(0, 0, 1280, 48));
        obstacles.add(new Rectangle(0, 0, 48, 720));
        obstacles.add(new Rectangle(1232, 0, 48, 720));

        obstacles.add(new Rectangle(520, 300, 240, 106));
        obstacles.add(new Rectangle(552, 405, 176, 88));
        obstacles.add(new Rectangle(495, 260, 290, 46));

        obstacles.add(new Rectangle(325, 397, 105, 54));
        obstacles.add(new Rectangle(845, 397, 110, 54));
        obstacles.add(new Rectangle(285, 210, 118, 52));
        obstacles.add(new Rectangle(865, 210, 120, 52));

        obstacles.add(new Rectangle(167, 484, 80, 74));
        obstacles.add(new Rectangle(1035, 484, 82, 74));
        obstacles.add(new Rectangle(148, 97, 125, 56));
        obstacles.add(new Rectangle(1002, 98, 132, 56));
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

    private void updateHealthPickups(float delta, Player player) {
        for (HealthPickup healthPickup : healthPickups) {
            healthPickup.update(player);
        }

        healthPickups.removeIf(healthPickup -> !healthPickup.isActive());

        healthSpawnTimer -= delta;
        if (healthSpawnTimer > 0) {
            return;
        }

        if (healthPickups.size() >= 2) {
            return;
        }

        healthSpawnTimer = spawnHealthPickup() ? MathUtils.random(8f, 12f) : 2f;
    }

    private boolean spawnHealthPickup() {
        for (int attempt = 0; attempt < 18; attempt++) {
            float x = MathUtils.random(120f, 1120f);
            float y = MathUtils.random(105f, 575f);
            Rectangle pickupArea = new Rectangle(x, y, 28, 28);

            boolean blocked = false;
            for (Rectangle obstacle : obstacles) {
                if (pickupArea.overlaps(obstacle)) {
                    blocked = true;
                    break;
                }
            }

            if (!blocked) {
                healthPickups.add(new HealthPickup(x, y, 28));
                return true;
            }
        }

        return false;
    }

    private Texture createHealthTexture() {
        Pixmap pixmap = new Pixmap(32, 32, Pixmap.Format.RGBA8888);
        pixmap.setBlending(Pixmap.Blending.None);
        pixmap.setColor(0, 0, 0, 0);
        pixmap.fill();
        pixmap.setColor(0.95f, 0.95f, 0.95f, 1f);
        pixmap.fillRectangle(7, 12, 18, 8);
        pixmap.fillRectangle(12, 7, 8, 18);
        pixmap.setColor(0.1f, 0.8f, 0.2f, 1f);
        pixmap.drawRectangle(6, 11, 20, 10);
        pixmap.drawRectangle(11, 6, 10, 20);

        Texture texture = new Texture(pixmap);
        pixmap.dispose();
        return texture;
    }
}
