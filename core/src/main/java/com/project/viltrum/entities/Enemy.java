package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.animation.AnimationStateMachine;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;
import com.project.viltrum.managers.MusicManager;
import com.project.viltrum.utils.AnimationManager;

import java.util.Collections;
import java.util.List;

public class Enemy {
    protected String name;
    protected CharacterStats stats;

    public float x;
    public float y;

    protected float width = 76;
    protected float height = 76;
    protected float hitboxWidth = 34;
    protected float hitboxHeight = 44;
    protected float hitboxOffsetY = 8;

    protected Texture texture;
    protected AnimationManager animationManager;
    protected Direction direction = Direction.DOWN;
    protected AnimationState animationState = AnimationState.IDLE;
    protected final AnimationStateMachine animationStateMachine = new AnimationStateMachine();

    protected float hurtTimer = 0;
    protected float attackCooldown = 0;
    protected float shootCooldown = 1.2f;
    protected float deathTimer = 0;
    protected float knockbackX = 0;
    protected float knockbackY = 0;

    private boolean deathEventPublished = false;

    public Enemy(String name, CharacterStats stats, float x, float y) {
        this.name = name;
        this.stats = stats;
        this.x = x;
        this.y = y;

        if (name.equals("Flaxan Soldier")) {
            animationManager = new AnimationManager("characters/flaxan_sheet.png");
        } else if (name.equals("Conquest")) {
            animationManager = new AnimationManager("characters/conquest_sheet.png");
            width = 94;
            height = 94;
            hitboxWidth = 42;
            hitboxHeight = 52;
            hitboxOffsetY = 10;
        } else if (name.equals("Regent Thragg")) {
            animationManager = new AnimationManager("characters/thragg_idle.png");
            width = 112;
            height = 112;
            hitboxWidth = 48;
            hitboxHeight = 58;
            hitboxOffsetY = 10;
        }
    }

    public void update(float delta, Player player, List<Rectangle> obstacles) {
        update(delta, player, obstacles, Collections.emptyList());
    }

    public void update(float delta, Player player, List<Rectangle> obstacles, List<Enemy> allies) {
        updateCoreTimers(delta, obstacles);

        if (stats.isDead()) {
            return;
        }

        separateFromAllies(allies, obstacles);

        float distance = distanceTo(player.getCenterX(), player.getCenterY());

        if (distance > 58) {
            moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 58);
            animationStateMachine.setLocomotion(true);
        } else {
            animationStateMachine.setLocomotion(false);
            tryMeleeAttack(player, 1.0f, 1f);
        }
    }

    public Projectile shootAt(Player player) {
        if (!name.equals("Flaxan Soldier") || shootCooldown > 0 || stats.isDead()) {
            return null;
        }

        float distance = distanceTo(player.getCenterX(), player.getCenterY());

        if (distance < 180 || distance > 640) {
            return null;
        }

        animationStateMachine.playLocked(AnimationState.ATTACK, 0.28f);
        shootCooldown = 1.55f;
        EventBus.getInstance().publish(GameEvent.point(GameEventType.PROJECTILE_FIRED, getCenterX(), getCenterY()));
        return new Projectile(getCenterX(), getCenterY(), player.getCenterX(), player.getCenterY(), 430, 10);
    }

    public void render(SpriteBatch batch) {
        if (animationManager != null) {
            TextureRegion frame = animationManager.getFrame(
                animationStateMachine.getState(),
                direction,
                animationStateMachine.getStateTime()
            );
            float drawHeight = height;
            float drawWidth = drawHeight * frame.getRegionWidth() / frame.getRegionHeight();
            float drawX = x + (width - drawWidth) / 2f;

            applyHurtColor(batch);
            batch.draw(frame, drawX, y, drawWidth, drawHeight);
            batch.setColor(Color.WHITE);
        } else if (texture != null) {
            applyHurtColor(batch);
            batch.draw(texture, x, y, width, height);
            batch.setColor(Color.WHITE);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x + (width - hitboxWidth) / 2f, y + hitboxOffsetY, hitboxWidth, hitboxHeight);
    }

    public void takeDamage(float damage) {
        takeDamage(damage, getCenterX(), getCenterY(), 0);
    }

    public void takeDamage(float damage, float sourceX, float sourceY, float knockback) {
        if (stats.isDead()) {
            return;
        }

        stats.takeDamage(damage);
        hurtTimer = 0.22f;
        applyKnockbackFrom(sourceX, sourceY, knockback);
        animationStateMachine.playLocked(stats.isDead() ? AnimationState.DEATH : AnimationState.HIT, stats.isDead() ? 0.5f : 0.18f);

        EventBus.getInstance().publish(GameEvent.damage(
            isBoss() ? GameEventType.BOSS_DAMAGED : GameEventType.ENEMY_DAMAGED,
            getCenterX(),
            getCenterY(),
            damage
        ));
        MusicManager.getInstance().playSound("hit");

        if (stats.isDead()) {
            deathTimer = 0.5f;
            publishDeathEvent();
        }
    }

    public boolean isDead() {
        return stats.isDead();
    }

    public boolean canRemove() {
        return stats.isDead() && deathTimer <= 0;
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

    public float getCenterX() {
        Rectangle hitbox = getHitbox();
        return hitbox.x + hitbox.width / 2f;
    }

    public float getCenterY() {
        Rectangle hitbox = getHitbox();
        return hitbox.y + hitbox.height / 2f;
    }

    public void setAnimationState(AnimationState animationState) {
        this.animationState = animationState;
        animationStateMachine.play(animationState);
    }

    public void playLockedAnimation(AnimationState animationState, float duration) {
        this.animationState = animationState;
        animationStateMachine.playLocked(animationState, duration);
    }

    public void moveToward(float targetX, float targetY, float delta, List<Rectangle> obstacles, float stopDistance) {
        moveTowardFast(targetX, targetY, delta, obstacles, stopDistance, stats.speed);
    }

    public void moveTowardFast(float targetX, float targetY, float delta, List<Rectangle> obstacles, float stopDistance, float speed) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        float length = (float) Math.sqrt(dx * dx + dy * dy);
        updateDirection(dx, dy);

        if (length > stopDistance && length > 0) {
            moveAxis(dx / length * speed * delta, 0, obstacles);
            moveAxis(0, dy / length * speed * delta, obstacles);
        }
    }

    public boolean tryMeleeAttack(Player player, float cooldown, float damageMultiplier) {
        if (attackCooldown > 0 || stats.isDead()) {
            return false;
        }

        attackCooldown = cooldown;
        animationStateMachine.playLocked(AnimationState.ATTACK, 0.28f);

        if (getHitbox().overlaps(player.getHitbox())) {
            boolean damaged = player.takeDamage(stats.damage * damageMultiplier, getCenterX(), getCenterY());

            if (damaged) {
                EventBus.getInstance().publish(GameEvent.point(GameEventType.ATTACK_HIT, player.getCenterX(), player.getCenterY()));
            }
        }

        return true;
    }

    protected void updateCoreTimers(float delta, List<Rectangle> obstacles) {
        animationStateMachine.update(delta);
        animationState = animationStateMachine.getState();

        if (attackCooldown > 0) attackCooldown -= delta;
        if (shootCooldown > 0) shootCooldown -= delta;
        if (hurtTimer > 0) hurtTimer -= delta;
        if (deathTimer > 0) deathTimer -= delta;

        applyKnockback(delta, obstacles);
    }

    protected void facePlayer(Player player) {
        updateDirection(player.getCenterX() - getCenterX(), player.getCenterY() - getCenterY());
    }

    public float distanceTo(float targetX, float targetY) {
        float dx = targetX - getCenterX();
        float dy = targetY - getCenterY();
        return (float) Math.sqrt(dx * dx + dy * dy);
    }

    protected void moveAxis(float dx, float dy, List<Rectangle> obstacles) {
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

    private void applyHurtColor(SpriteBatch batch) {
        if (hurtTimer > 0) {
            float flash = ((int) (hurtTimer * 24)) % 2 == 0 ? 1f : 0.45f;
            batch.setColor(1f, flash, flash, 1f);
        }
    }

    private void applyKnockbackFrom(float sourceX, float sourceY, float knockback) {
        if (knockback <= 0) {
            return;
        }

        float dx = getCenterX() - sourceX;
        float dy = getCenterY() - sourceY;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 0) {
            knockbackX = dx / length * knockback;
            knockbackY = dy / length * knockback;
        }
    }

    private void applyKnockback(float delta, List<Rectangle> obstacles) {
        if (Math.abs(knockbackX) < 1f && Math.abs(knockbackY) < 1f) {
            knockbackX = 0;
            knockbackY = 0;
            return;
        }

        moveAxis(knockbackX * delta, 0, obstacles);
        moveAxis(0, knockbackY * delta, obstacles);
        knockbackX *= 0.84f;
        knockbackY *= 0.84f;
    }

    private void separateFromAllies(List<Enemy> allies, List<Rectangle> obstacles) {
        for (Enemy ally : allies) {
            if (ally == this || ally.isDead()) {
                continue;
            }

            Rectangle own = getHitbox();
            Rectangle other = ally.getHitbox();

            if (!own.overlaps(other)) {
                continue;
            }

            float dx = getCenterX() - ally.getCenterX();
            float dy = getCenterY() - ally.getCenterY();
            float length = (float) Math.sqrt(dx * dx + dy * dy);

            if (length == 0) {
                dx = 1;
                length = 1;
            }

            moveAxis(dx / length * 18f, 0, obstacles);
            moveAxis(0, dy / length * 18f, obstacles);
        }
    }

    private void updateDirection(float dx, float dy) {
        if (Math.abs(dx) > Math.abs(dy)) {
            direction = dx >= 0 ? Direction.RIGHT : Direction.LEFT;
        } else {
            direction = dy >= 0 ? Direction.UP : Direction.DOWN;
        }
    }

    private void publishDeathEvent() {
        if (deathEventPublished) {
            return;
        }

        deathEventPublished = true;
        EventBus.getInstance().publish(GameEvent.point(
            isBoss() ? GameEventType.BOSS_DIED : GameEventType.ENEMY_DIED,
            getCenterX(),
            getCenterY()
        ));
        MusicManager.getInstance().playSound(isBoss() ? "boss_roar" : "enemy_death");
    }

    private boolean isBoss() {
        return this instanceof Boss;
    }
}
