package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

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

    public Projectile(float x, float y, float targetX, float targetY, float speed, float damage) {
        this.x = x;
        this.y = y;
        this.damage = damage;

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
                return;
            }
        }

        if (hitbox.overlaps(player.getHitbox())) {
            player.takeDamage(damage);
            active = false;
        }
    }

    public void render(SpriteBatch batch, Texture texture) {
        if (!active) {
            return;
        }

        float angle = (float) Math.toDegrees(Math.atan2(velocityY, velocityX));
        batch.draw(texture, x, y, width / 2f, height / 2f, width, height, 1, 1, angle, 0, 0,
            texture.getWidth(), texture.getHeight(), false, false);
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isActive() {
        return active;
    }
}
