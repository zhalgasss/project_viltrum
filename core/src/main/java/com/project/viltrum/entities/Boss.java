package com.project.viltrum.entities;

import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;
import com.project.viltrum.factory.EnemyFactory;
import com.project.viltrum.managers.MusicManager;
import com.project.viltrum.state.BossRageState;
import com.project.viltrum.state.BossState;

import java.util.Collections;
import java.util.List;

public class Boss extends Enemy {
    private BossState state;
    private float specialCooldown = 1.2f;
    private float rangedCooldown = 1.6f;
    private float summonCooldown = 4.5f;
    private float dashTimer = 0;
    private float dashTargetX = 0;
    private float dashTargetY = 0;
    private boolean dashHitPlayer = false;
    private boolean rageStarted = false;
    private float telegraphTimer = 0;
    private float telegraphDuration = 1.5f;
    private float telegraphRadius = 130;
    private float postAttackTimer = 0;
    private float postAttackDuration = 2.0f;
    private boolean telegraphHitResolved = false;

    public Boss(String name, CharacterStats stats, float x, float y, BossState state) {
        super(name, stats, x, y);
        this.state = state;
    }

    @Override
    public void update(float delta, Player player, List<Rectangle> obstacles) {
        update(delta, player, obstacles, Collections.emptyList(), Collections.emptyList());
    }

    public void update(
        float delta,
        Player player,
        List<Rectangle> obstacles,
        List<Enemy> summons,
        List<Projectile> projectiles
    ) {
        updateCoreTimers(delta, obstacles);

        if (stats.isDead()) {
            return;
        }

        tickBossCooldowns(delta);
        facePlayer(player);
        state.update(this, player, delta, obstacles, summons, projectiles);
    }

    public void setState(BossState state) {
        this.state = state;
    }

    public boolean shouldEnterRage(float threshold) {
        return getHealthRatio() <= threshold;
    }

    public void enterRageMode() {
        if (rageStarted) {
            return;
        }

        rageStarted = true;
        setState(new BossRageState());
        EventBus.getInstance().publish(GameEvent.point(GameEventType.HEAVY_HIT, getCenterX(), getCenterY()));
        MusicManager.getInstance().playSound("boss_roar");
    }

    public boolean isRageStarted() {
        return rageStarted;
    }

    public float getHealthRatio() {
        return stats.hp / stats.maxHp;
    }

    public boolean canUseSpecial() {
        return specialCooldown <= 0;
    }

    public boolean canUseRanged() {
        return rangedCooldown <= 0;
    }

    public boolean canSummon() {
        return summonCooldown <= 0;
    }

    public void resetSpecialCooldown(float cooldown) {
        specialCooldown = cooldown;
    }

    public void resetRangedCooldown(float cooldown) {
        rangedCooldown = cooldown;
    }

    public void resetSummonCooldown(float cooldown) {
        summonCooldown = cooldown;
    }

    public void startDash(Player player, float cooldown) {
        dashTimer = 0.26f;
        dashTargetX = player.getCenterX();
        dashTargetY = player.getCenterY();
        dashHitPlayer = false;
        resetSpecialCooldown(cooldown);
        playLockedAnimation(AnimationState.ATTACK, 0.3f);
        EventBus.getInstance().publish(GameEvent.point(GameEventType.DASH, getCenterX(), getCenterY()));
        MusicManager.getInstance().playSound("dash");
    }

    public boolean updateDash(Player player, float delta, List<Rectangle> obstacles, float damageMultiplier) {
        if (dashTimer <= 0) {
            return false;
        }

        dashTimer -= delta;
        moveTowardFast(dashTargetX, dashTargetY, delta, obstacles, 0, stats.speed * (rageStarted ? 4.2f : 3.35f));

        if (!dashHitPlayer && getHitbox().overlaps(player.getHitbox())) {
            dashHitPlayer = true;

            if (player.takeDamage(stats.damage * damageMultiplier, getCenterX(), getCenterY())) {
                EventBus.getInstance().publish(GameEvent.point(GameEventType.HEAVY_HIT, player.getCenterX(), player.getCenterY()));
            }
        }

        return true;
    }

    public Projectile fireProjectile(Player player, float speed, float damage) {
        playLockedAnimation(AnimationState.ATTACK, 0.28f);
        EventBus.getInstance().publish(GameEvent.point(GameEventType.PROJECTILE_FIRED, getCenterX(), getCenterY()));
        return new Projectile(getCenterX(), getCenterY(), player.getCenterX(), player.getCenterY(), speed, damage);
    }

    public void startTelegraphedAttack(float radius, float warningDuration, float cooldownAfter) {
        telegraphRadius = radius;
        telegraphDuration = warningDuration;
        telegraphTimer = warningDuration;
        postAttackDuration = cooldownAfter;
        postAttackTimer = 0;
        telegraphHitResolved = false;
        resetSpecialCooldown(warningDuration + cooldownAfter);
        playLockedAnimation(AnimationState.ATTACK, warningDuration + 0.2f);
    }

    public boolean updateTelegraphedAttack(Player player, float delta, float damageMultiplier) {
        if (telegraphTimer > 0) {
            telegraphTimer -= delta;
            setAnimationState(AnimationState.ATTACK);

            if (telegraphTimer <= 0 && !telegraphHitResolved) {
                telegraphHitResolved = true;
                postAttackTimer = postAttackDuration;

                if (distanceTo(player.getCenterX(), player.getCenterY()) <= telegraphRadius) {
                    if (player.takeDamage(stats.damage * damageMultiplier, getCenterX(), getCenterY())) {
                        EventBus.getInstance().publish(GameEvent.point(GameEventType.HEAVY_HIT, player.getCenterX(), player.getCenterY()));
                    }
                } else {
                    EventBus.getInstance().publish(GameEvent.point(GameEventType.HEAVY_HIT, getCenterX(), getCenterY()));
                }
            }

            return true;
        }

        if (postAttackTimer > 0) {
            postAttackTimer -= delta;
            setAnimationState(AnimationState.IDLE);
            return true;
        }

        return false;
    }

    public boolean isTelegraphingAttack() {
        return telegraphTimer > 0;
    }

    public float getTelegraphRadius() {
        return telegraphRadius;
    }

    public float getTelegraphProgress() {
        if (telegraphDuration <= 0) {
            return 1;
        }

        return 1f - Math.max(0, telegraphTimer) / telegraphDuration;
    }

    public void summonFlaxans(List<Enemy> summons, int count) {
        float[][] offsets = {
            {-95, -65},
            {105, -55},
            {-85, 70},
            {95, 75}
        };

        for (int i = 0; i < count && i < offsets.length; i++) {
            summons.add(EnemyFactory.createFlaxan(getCenterX() + offsets[i][0], getCenterY() + offsets[i][1]));
        }

        EventBus.getInstance().publish(GameEvent.point(GameEventType.HEAVY_HIT, getCenterX(), getCenterY()));
        MusicManager.getInstance().playSound("boss_roar");
    }

    private void tickBossCooldowns(float delta) {
        if (specialCooldown > 0) specialCooldown -= delta;
        if (rangedCooldown > 0) rangedCooldown -= delta;
        if (summonCooldown > 0) summonCooldown -= delta;
    }
}
