package com.project.viltrum.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.strategy.AttackStrategy;

import java.util.List;

public class Player {
    private HeroType type;
    private CharacterStats stats;
    private AttackStrategy attackStrategy;

    private float x;
    private float y;
    private float width = 90;
    private float height = 90;

    private Direction direction = Direction.DOWN;
    private AnimationState animationState = AnimationState.IDLE;

    private float attackCooldown = 0;

    public Player(HeroType type, CharacterStats stats, AttackStrategy attackStrategy, float x, float y) {
        this.type = type;
        this.stats = stats;
        this.attackStrategy = attackStrategy;
        this.x = x;
        this.y = y;
    }

    public void update(float delta, List<Enemy> enemies) {
        move(delta);

        if (attackCooldown > 0) {
            attackCooldown -= delta;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
            animationState = AnimationState.ATTACK;
            attackStrategy.attack(this, enemies);
            attackCooldown = 0.35f;
        } else {
            animationState = AnimationState.IDLE;
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

        x += dx;
        y += dy;

        if (x < 60) x = 60;
        if (x > 1130) x = 1130;
        if (y < 70) y = 70;
        if (y > 610) y = 610;
    }

    public void render(SpriteBatch batch) {
        // Пока рисуем цветной прямоугольник через текстур нет.
        // Позже сюда подключим sprite animation.
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 20, y + 10, 50, 70);
    }

    public Rectangle getAttackBox() {
        if (direction == Direction.RIGHT) {
            return new Rectangle(x + 70, y + 20, 70, 45);
        }
        if (direction == Direction.LEFT) {
            return new Rectangle(x - 50, y + 20, 70, 45);
        }
        if (direction == Direction.UP) {
            return new Rectangle(x + 20, y + 70, 50, 70);
        }
        return new Rectangle(x + 20, y - 50, 50, 70);
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
