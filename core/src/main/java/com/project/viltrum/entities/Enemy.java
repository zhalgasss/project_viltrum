package com.project.viltrum.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.utils.AnimationManager;

import java.util.List;

public class Enemy {
    protected String name;
    protected CharacterStats stats;

    public float x;
    public float y;

    protected float width = 90;
    protected float height = 90;

    protected Texture texture;
    protected AnimationManager animationManager;
    protected Direction direction = Direction.DOWN;
    protected AnimationState animationState = AnimationState.IDLE;
    protected float animationTimer = 0;
    protected int frameIndex = 0;
    protected float hurtTimer = 0;

    private float attackCooldown = 0;
    private float shootCooldown = 1.2f;

    public Enemy(String name, CharacterStats stats, float x, float y) {
        this.name = name;
        this.stats = stats;
        this.x = x;
        this.y = y;

        if (name.equals("Flaxan Soldier")) {
            animationManager = new AnimationManager("characters/flaxan_sheet.png");
        } else if (name.equals("Conquest")) {
            animationManager = new AnimationManager("characters/conquest_sheet.png");
            width = 115;
            height = 115;
        } else if (name.equals("Regent Thragg")) {
            texture = new Texture("characters/thragg_idle.png");
            width = 120;
            height = 120;
        }
    }

    public void update(float delta, Player player, List<Rectangle> obstacles) {
        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }

        if (shootCooldown > 0) {
            shootCooldown -= delta;
        }

        if (hurtTimer > 0) {
            hurtTimer -= delta;
        }

        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        updateDirection(dx, dy);

        if (length > 95) {
            moveAxis(dx / length * stats.speed * delta, 0, obstacles);
            moveAxis(0, dy / length * stats.speed * delta, obstacles);
        }

        if (getHitbox().overlaps(player.getHitbox()) && attackCooldown <= 0) {
            animationState = AnimationState.ATTACK;
            player.takeDamage(stats.damage);
            attackCooldown = 1.0f;
        } else if (attackCooldown <= 0.65f) {
            animationState = AnimationState.IDLE;
        }
    }

    public Projectile shootAt(Player player) {
        if (!name.equals("Flaxan Soldier") || shootCooldown > 0) {
            return null;
        }

        float dx = player.getX() - x;
        float dy = player.getY() - y;
        float distance = (float) Math.sqrt(dx * dx + dy * dy);

        if (distance < 150 || distance > 650) {
            return null;
        }

        animationState = AnimationState.ATTACK;
        shootCooldown = 1.35f;
        return new Projectile(x + width / 2f, y + height / 2f, player.getX() + 52, player.getY() + 55, 430, 10);
    }

    public void render(SpriteBatch batch) {
        if (animationManager != null) {
            animationTimer += Gdx.graphics.getDeltaTime();

            if (animationTimer > 0.15f) {
                frameIndex++;
                animationTimer = 0;
            }

            TextureRegion frame = animationManager.getFrame(animationState, direction, frameIndex);
            float drawHeight = height;
            float drawWidth = drawHeight * frame.getRegionWidth() / frame.getRegionHeight();
            float drawX = x + (width - drawWidth) / 2f;

            if (hurtTimer > 0) {
                float flash = ((int) (hurtTimer * 18)) % 2 == 0 ? 1f : 0.45f;
                batch.setColor(1f, flash, flash, 1f);
            }

            batch.draw(frame, drawX, y, drawWidth, drawHeight);
            batch.setColor(Color.WHITE);
        } else if (texture != null) {
            if (hurtTimer > 0) {
                float flash = ((int) (hurtTimer * 18)) % 2 == 0 ? 1f : 0.45f;
                batch.setColor(1f, flash, flash, 1f);
            }

            batch.draw(texture, x, y, width, height);
            batch.setColor(Color.WHITE);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 20, y + 10, 50, 70);
    }

    public void takeDamage(float damage) {
        stats.takeDamage(damage);
        hurtTimer = 0.32f;
    }

    public boolean isDead() {
        return stats.isDead();
    }

    public void dispose() {
        if (animationManager != null) {
            animationManager.dispose();
        }

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

    protected void facePlayer(Player player) {
        updateDirection(player.getX() - x, player.getY() - y);
    }

    private void updateDirection(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx >= 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            direction = dy >= 0 ? Direction.UP : Direction.DOWN;
        }
    }

    private void moveAxis(float dx, float dy, List<Rectangle> obstacles) {
        if (dx == 0 && dy == 0) {
            return;
        }

        x += dx;
        y += dy;

        for (Rectangle obstacle : obstacles) {
            if (getHitbox().overlaps(obstacle)) {
                x -= dx;
                y -= dy;
                return;
            }
        }
    }
}
