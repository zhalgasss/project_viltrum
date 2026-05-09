package com.project.viltrum.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.project.viltrum.animation.AnimationStateMachine;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;
import com.project.viltrum.managers.MusicManager;
import com.project.viltrum.strategy.AttackStrategy;
import com.project.viltrum.utils.AnimationManager;

import java.util.List;

public class Player {
    private final HeroType type;
    private final CharacterStats stats;
    private final AttackStrategy attackStrategy;
    private final AnimationManager animationManager;
    private final AnimationStateMachine animationStateMachine = new AnimationStateMachine();

    private float x;
    private float y;
    private float width = 64;
    private float height = 82;

    private Direction direction = Direction.DOWN;

    private float attackCooldown = 0;
    private float attackTimer = 0;
    private float invulnerabilityTimer = 0;
    private float hurtTimer = 0;
    private float dashCooldown = 0;
    private float dashTimer = 0;
    private float knockbackX = 0;
    private float knockbackY = 0;

    public Player(HeroType type, CharacterStats stats, AttackStrategy attackStrategy, float x, float y) {
        this.type = type;
        this.stats = stats;
        this.attackStrategy = attackStrategy;
        this.x = x;
        this.y = y;
        this.animationManager = new AnimationManager(type);
    }

    public void update(float delta, List<Enemy> enemies, List<Rectangle> obstacles) {
        update(delta, enemies, obstacles, null);
    }

    public void update(float delta, List<Enemy> enemies, List<Rectangle> obstacles, List<Projectile> projectiles) {
        animationStateMachine.update(delta);

        if (stats.isDead()) {
            return;
        }

        tickTimers(delta);
        applyKnockback(delta, obstacles);

        boolean moving = move(delta, obstacles);

        if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && attackCooldown <= 0) {
            attack(enemies, projectiles);
        }

        if (!animationStateMachine.isLocked()) {
            animationStateMachine.setLocomotion(moving || dashTimer > 0);
        }
    }

    public void render(SpriteBatch batch) {
        TextureRegion frame = animationManager.getFrame(
            animationStateMachine.getState(),
            direction,
            animationStateMachine.getStateTime()
        );
        float drawHeight = height;
        float drawWidth = drawHeight * frame.getRegionWidth() / frame.getRegionHeight();
        float drawX = x + (width - drawWidth) / 2f;

        if (invulnerabilityTimer > 0 || hurtTimer > 0) {
            float flash = ((int) ((invulnerabilityTimer + hurtTimer) * 20)) % 2 == 0 ? 1f : 0.35f;
            batch.setColor(1f, flash, flash, invulnerabilityTimer > 0 ? 0.72f : 1f);
        }

        batch.draw(frame, drawX, y, drawWidth, drawHeight);
        batch.setColor(Color.WHITE);
    }

    public boolean takeDamage(float damage) {
        return takeDamage(damage, getCenterX(), getCenterY());
    }

    public boolean takeDamage(float damage, float sourceX, float sourceY) {
        if (stats.isDead() || invulnerabilityTimer > 0) {
            return false;
        }

        stats.takeDamage(damage);
        invulnerabilityTimer = 0.7f;
        hurtTimer = 0.26f;

        float dx = getCenterX() - sourceX;
        float dy = getCenterY() - sourceY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            knockbackX = dx / length * 165f;
            knockbackY = dy / length * 165f;
        }

        EventBus.getInstance().publish(GameEvent.damage(GameEventType.PLAYER_DAMAGED, getCenterX(), getCenterY(), damage));
        MusicManager.getInstance().playSound("hit");

        if (stats.isDead()) {
            animationStateMachine.playLocked(AnimationState.DEATH, Float.MAX_VALUE);
        } else {
            animationStateMachine.playLocked(AnimationState.HIT, 0.2f);
        }

        return true;
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + 15, y + 9, 34, 48);
    }

    public Rectangle getAttackBox() {
        Rectangle hitbox = getHitbox();
        float centerX = hitbox.x + hitbox.width / 2f;
        float centerY = hitbox.y + hitbox.height / 2f;

        if (direction == Direction.RIGHT) {
            return new Rectangle(hitbox.x + hitbox.width, centerY - 22, 70, 44);
        }

        if (direction == Direction.LEFT) {
            return new Rectangle(hitbox.x - 70, centerY - 22, 70, 44);
        }

        if (direction == Direction.UP) {
            return new Rectangle(centerX - 25, hitbox.y + hitbox.height, 50, 70);
        }

        return new Rectangle(centerX - 25, hitbox.y - 70, 50, 70);
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
        return animationStateMachine.getState();
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCenterX() {
        Rectangle hitbox = getHitbox();
        return hitbox.x + hitbox.width / 2f;
    }

    public float getCenterY() {
        Rectangle hitbox = getHitbox();
        return hitbox.y + hitbox.height / 2f;
    }

    public boolean isAttacking() {
        return attackTimer > 0;
    }

    private void tickTimers(float delta) {
        if (attackCooldown > 0) attackCooldown -= delta;
        if (attackTimer > 0) attackTimer -= delta;
        if (invulnerabilityTimer > 0) invulnerabilityTimer -= delta;
        if (hurtTimer > 0) hurtTimer -= delta;
        if (dashCooldown > 0) dashCooldown -= delta;
        if (dashTimer > 0) dashTimer -= delta;
    }

    private boolean move(float delta, List<Rectangle> obstacles) {
        Vector2 input = readMovementInput();

        if (input.isZero()) {
            return false;
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.SHIFT_LEFT) && dashCooldown <= 0) {
            dashTimer = 0.13f;
            dashCooldown = 0.75f;
            MusicManager.getInstance().playSound("dash");
            EventBus.getInstance().publish(GameEvent.point(GameEventType.DASH, getCenterX(), getCenterY()));
        }

        float speed = dashTimer > 0 ? stats.speed * 2.85f : stats.speed;
        moveAxis(input.x * speed * delta, 0, obstacles);
        moveAxis(0, input.y * speed * delta, obstacles);
        clampToRoom();
        return true;
    }

    private Vector2 readMovementInput() {
        Vector2 input = new Vector2();

        if (Gdx.input.isKeyPressed(Input.Keys.W)) {
            input.y += 1;
            direction = Direction.UP;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.S)) {
            input.y -= 1;
            direction = Direction.DOWN;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            input.x -= 1;
            direction = Direction.LEFT;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            input.x += 1;
            direction = Direction.RIGHT;
        }

        if (!input.isZero()) {
            input.nor();
        }

        return input;
    }

    private void attack(List<Enemy> enemies, List<Projectile> projectiles) {
        attackTimer = 0.18f;
        attackCooldown = 0.42f;
        animationStateMachine.playLocked(AnimationState.ATTACK, 0.28f);

        if (type == HeroType.TECHNO_JACKET && projectiles != null) {
            fireTechnoShot(projectiles);
            return;
        }

        Rectangle attackBox = getAttackBox();
        EventBus.getInstance().publish(GameEvent.area(
            GameEventType.PLAYER_ATTACK,
            attackBox.x,
            attackBox.y,
            attackBox.width,
            attackBox.height
        ));

        attackStrategy.attack(this, enemies);
    }

    private void fireTechnoShot(List<Projectile> projectiles) {
        float targetX = getCenterX();
        float targetY = getCenterY();

        if (direction == Direction.RIGHT) targetX += 720;
        if (direction == Direction.LEFT) targetX -= 720;
        if (direction == Direction.UP) targetY += 720;
        if (direction == Direction.DOWN) targetY -= 720;

        projectiles.add(new Projectile(getCenterX(), getCenterY(), targetX, targetY, 650, stats.damage + 18, true));
        EventBus.getInstance().publish(GameEvent.point(GameEventType.PROJECTILE_FIRED, getCenterX(), getCenterY()));
    }

    private void applyKnockback(float delta, List<Rectangle> obstacles) {
        if (Math.abs(knockbackX) < 1f && Math.abs(knockbackY) < 1f) {
            knockbackX = 0;
            knockbackY = 0;
            return;
        }

        moveAxis(knockbackX * delta, 0, obstacles);
        moveAxis(0, knockbackY * delta, obstacles);
        knockbackX *= 0.82f;
        knockbackY *= 0.82f;
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

    private void clampToRoom() {
        if (x < 70) x = 70;
        if (x > 1130) x = 1130;
        if (y < 70) y = 70;
        if (y > 580) y = 580;
    }
}
