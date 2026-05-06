package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    protected String name;
    protected CharacterStats stats;
    public float x;
    public float y;
    protected float width = 80;
    protected float height = 80;

    public Enemy(String name, CharacterStats stats, float x, float y) {
        this.name = name;
        this.stats = stats;
        this.x = x;
        this.y = y;
    }

    public void update(float delta, Player player) {
        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 5) {
            x += dx / length * stats.speed * delta;
            y += dy / length * stats.speed * delta;
        }

        if (getHitbox().overlaps(player.getHitbox())) {
            player.getStats().takeDamage(stats.damage * delta);
        }
    }

    public void render(SpriteBatch batch) {
        // Later sprite draw
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 15, y + 10, 50, 65);
    }

    public void takeDamage(float damage) {
        stats.takeDamage(damage);
    }

    public boolean isDead() {
        return stats.isDead();
    }

    public String getName() {
        return name;
    }

    public CharacterStats getStats() {
        return stats;
    }
}
