package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;

import java.util.List;

public class Projectile {
    private float x;
    private float y;
    private float velocityX;
    private float velocityY;
    private float damage;
    private float width = 28;
    private float height = 14;
    private boolean active = true;
    private boolean fromPlayer = false;

    public Projectile(float x, float y, float targetX, float targetY, float speed, float damage) {
        this(x, y, targetX, targetY, speed, damage, false);
    }

    public Projectile(float x, float y, float targetX, float targetY, float speed, float damage, boolean fromPlayer) {
        this.x = x;
        this.y = y;
        this.damage = damage;
        this.fromPlayer = fromPlayer;

        if (fromPlayer) {
            width = 34;
            height = 12;
        }

        float dx = targetX - x;
        float dy = targetY - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length == 0) {
            velocityX = speed;
            velocityY = 0;
        } else {
            velocityX = dx / length * speed;
            velocityY = dy / length * speed;
        }
    }

    public void update(float delta, Player player, List<Rectangle> obstacles) {
        update(delta, player, null, obstacles);
    }

    public void update(float delta, Player player, List<Enemy> enemies, List<Rectangle> obstacles) {
        if (!active) {
            return;
        }

        x += velocityX * delta;
        y += velocityY * delta;

        if (x < 0 || x > 1280 || y < 0 || y > 720) {
            active = false;
            return;
        }

        Rectangle hitbox = getHitbox();

        for (Rectangle obstacle : obstacles) {
            if (hitbox.overlaps(obstacle)) {
                active = false;
                EventBus.getInstance().publish(GameEvent.point(GameEventType.ATTACK_HIT, x, y));
                return;
            }
        }

        if (fromPlayer) {
            damageEnemies(enemies, hitbox);
        } else if (hitbox.overlaps(player.getHitbox())) {
            player.takeDamage(damage, x, y);
            active = false;
            EventBus.getInstance().publish(GameEvent.point(GameEventType.ATTACK_HIT, x, y));
        }
    }

    public void render(SpriteBatch batch, Texture texture) {
        if (!active) {
            return;
        }

        float angle = (float) Math.toDegrees(Math.atan2(velocityY, velocityX));
        if (fromPlayer) {
            batch.setColor(Color.CYAN);
        }
        batch.draw(texture, x, y, width / 2f, height / 2f, width, height, 1, 1, angle, 0, 0,
            texture.getWidth(), texture.getHeight(), false, false);
        batch.setColor(Color.WHITE);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isFromPlayer() {
        return fromPlayer;
    }

    private void damageEnemies(List<Enemy> enemies, Rectangle hitbox) {
        if (enemies == null) {
            return;
        }

        for (Enemy enemy : enemies) {
            if (!enemy.isDead() && hitbox.overlaps(enemy.getHitbox())) {
                enemy.takeDamage(damage, x, y, 170);
                active = false;
                EventBus.getInstance().publish(GameEvent.point(GameEventType.ATTACK_HIT, x, y));
                return;
            }
        }
    }
}
