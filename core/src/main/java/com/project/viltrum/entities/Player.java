package com.project.viltrum.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.strategy.AttackStrategy;
import com.project.viltrum.utils.AnimationManager;

import java.util.List;

public class Player {
    private HeroType type;
    private CharacterStats stats;
    private AttackStrategy attackStrategy;

    private float x;
    private float y;
    private float width = 75;
    private float height = 95;

    private Direction direction = Direction.DOWN;
    private AnimationState animationState = AnimationState.IDLE;

    private float attackCooldown = 0;
    private float attackAnimationTimer = 0;

    private AnimationManager animationManager;
    private float animationTimer = 0;
    private int frameIndex = 0;

    public Player(HeroType type, CharacterStats stats, AttackStrategy attackStrategy, float x, float y) {
        this.type = type;
        this.stats = stats;
        this.attackStrategy = attackStrategy;
        this.x = x;
        this.y = y;
        this.animationManager = new AnimationManager(type);
    }

    public void update(float delta, List<Enemy> enemies) {
        move(delta);

        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }

        if (attackAnimationTimer > 0) {
            attackAnimationTimer -= delta;
            animationState = AnimationState.ATTACK;
        } else {
            animationState = AnimationState.IDLE;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
            animationState = AnimationState.ATTACK;
            attackAnimationTimer = 0.35f;
            attackCooldown = 0.45f;
            attackStrategy.attack(this, enemies);
        }
    }

    private void move(float delta) {
        float dx = 0;
        float dy = 0;

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            dy += stats.speed * delta;
            direction = Direction.UP;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            dy -= stats.speed * delta;
            direction = Direction.DOWN;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            dx -= stats.speed * delta;
            direction = Direction.LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            dx += stats.speed * delta;
            direction = Direction.RIGHT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.SHIFT_LEFT)) {
            dx *= 1.8f;
            dy *= 1.8f;
        }

        x += dx;
        y += dy;

        if (x < 70) x = 70;
        if (x > 1130) x = 1130;
        if (y < 70) y = 70;
        if (y > 580) y = 580;
    }

    public void render(SpriteBatch batch) {
        animationTimer += Gdx.graphics.getDeltaTime();

        if (animationTimer > 0.15f) {
            frameIndex++;
            animationTimer = 0;
        }

        batch.draw(
            animationManager.getFrame(animationState, direction, frameIndex),
            x,
            y,
            width,
            height
        );
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 28, y + 15, 45, 70);
    }

    public Rectangle getAttackBox() {
        if (direction == Direction.RIGHT) {
            return new Rectangle(x + 75, y + 25, 80, 50);
        }

        if (direction == Direction.LEFT) {
            return new Rectangle(x - 50, y + 25, 80, 50);
        }

        if (direction == Direction.UP) {
            return new Rectangle(x + 25, y + 80, 60, 80);
        }

        return new Rectangle(x + 25, y - 45, 60, 80);
    }

    public void dispose() {
        animationManager.dispose();
    }

    public HeroType getType() {
        return type;
    }

    public CharacterStats getStats() {
        return stats;
    }

    public Direction getDirection() {
        return direction;
    }

    public AnimationState getAnimationState() {
        return animationState;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
}
