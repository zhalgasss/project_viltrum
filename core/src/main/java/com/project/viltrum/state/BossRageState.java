package com.project.viltrum.state;

import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.entities.AnimationState;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Projectile;

import java.util.List;

public class BossRageState implements BossState {
    @Override
    public void update(
        Boss boss,
        Player player,
        float delta,
        List<Rectangle> obstacles,
        List<Enemy> summons,
        List<Projectile> projectiles
    ) {
        if (boss.getName().equals("Conquest")) {
            updateConquestRage(boss, player, delta, obstacles);
            return;
        }

        updateThraggRage(boss, player, delta, obstacles, summons, projectiles);
    }

    private void updateConquestRage(Boss boss, Player player, float delta, List<Rectangle> obstacles) {
        boss.getStats().speed = 240;
        boss.getStats().damage = 36;

        if (boss.updateDash(player, delta, obstacles, 1.55f)) {
            return;
        }

        float distance = boss.distanceTo(player.getCenterX(), player.getCenterY());

        if (boss.canUseSpecial() && distance < 640) {
            boss.startDash(player, 1.05f);
            return;
        }

        if (distance > 56) {
            boss.moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 56);
            boss.setAnimationState(AnimationState.WALK);
        } else {
            boss.tryMeleeAttack(player, 0.58f, 1.2f);
        }
    }

    private void updateThraggRage(
        Boss boss,
        Player player,
        float delta,
        List<Rectangle> obstacles,
        List<Enemy> summons,
        List<Projectile> projectiles
    ) {
        boss.getStats().speed = 245;
        boss.getStats().damage = 28;

        if (boss.updateTelegraphedAttack(player, delta, 1.25f)) {
            return;
        }

        float distance = boss.distanceTo(player.getCenterX(), player.getCenterY());

        if (boss.canUseRepel() && ((boss.hasClosePressure() && distance < 205f) || distance < 62f)) {
            if (boss.tryRepelPlayer(player, 8f, 640f, 1.75f)) {
                return;
            }
        }

        if (boss.canUseSpecial() && distance < 190f) {
            boss.startTelegraphedAttack(175f, 0.4f, 1.1f);
            return;
        }

        if (distance > 285f) {
            if (boss.canUseRanged()) {
                boss.fireProjectileBurst(player, projectiles, 4, 11f, 540f, 9f, 1.05f);
            }

            boss.setAnimationState(AnimationState.IDLE);
            return;
        }

        if (distance > 175f) {
            boss.moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 175f);
            boss.setAnimationState(AnimationState.WALK);
        } else {
            boss.setAnimationState(AnimationState.IDLE);
        }
    }
}
