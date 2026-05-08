package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class Enemy {
    protected String name;
    protected CharacterStats stats;

    public float x;
    public float y;

    protected float width = 90;
    protected float height = 90;

    protected Texture texture;

    private float attackCooldown = 0;

    public Enemy(String name, CharacterStats stats, float x, float y) {
        this.name = name;
        this.stats = stats;
        this.x = x;
        this.y = y;

        if (name.equals("Flaxan Soldier")) {
            texture = new Texture("characters/flaxan.png");
        } else if (name.equals("Conquest")) {
            texture = new Texture("characters/conquest.png");
            width = 115;
            height = 115;
        } else if (name.equals("Regent Thragg")) {
            texture = new Texture("characters/thragg_idle.png");
            width = 120;
            height = 120;
        }
    }

    public void update(float delta, Player player) {
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }

        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 95) {
            x += dx / length * stats.speed * delta;
            y += dy / length * stats.speed * delta;
        }

        if (getHitbox().overlaps(player.getHitbox()) && attackCooldown <= 0) {
            player.getStats().takeDamage(stats.damage);
            attackCooldown = 1.0f;
        }
    }

    public void render(SpriteBatch batch) {
        if (texture != null) {
            batch.draw(texture, x, y, width, height);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 20, y + 10, 50, 70);
    }

    public void takeDamage(float damage) {
        stats.takeDamage(damage);
    }

    public boolean isDead() {
        return stats.isDead();
    }

    public void dispose() {
        if (texture != null) {
            texture.dispose();
        }
    }

    public String getName() {
        return name;
    }

    public CharacterStats getStats() {
        return stats;
    }
}
