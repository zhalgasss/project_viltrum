package com.project.viltrum.state;

import com.badlogic.gdx.math.Rectangle;
import com.project.viltrum.entities.AnimationState;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Projectile;

import java.util.List;

public class BossChaseState implements BossState {
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
            updateConquest(boss, player, delta, obstacles);
            return;
        }

        updateThragg(boss, player, delta, obstacles, summons, projectiles);
    }

    private void updateConquest(Boss boss, Player player, float delta, List<Rectangle> obstacles) {
        if (boss.shouldEnterRage(0.5f)) {
            boss.enterRageMode();
            return;
        }

        if (boss.updateDash(player, delta, obstacles, 1.35f)) {
            return;
        }

        float distance = boss.distanceTo(player.getCenterX(), player.getCenterY());

        if (boss.canUseSpecial() && distance < 560) {
            boss.startDash(player, 1.8f);
            return;
        }

        if (distance > 62) {
            boss.moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 62);
            boss.setAnimationState(AnimationState.WALK);
        } else {
            boss.setAnimationState(AnimationState.IDLE);
            boss.tryMeleeAttack(player, 0.8f, 1.0f);
        }
    }

    private void updateThragg(
        Boss boss,
        Player player,
        float delta,
        List<Rectangle> obstacles,
        List<Enemy> summons,
        List<Projectile> projectiles
    ) {
        if (boss.shouldEnterRage(0.3f)) {
            boss.enterRageMode();
            return;
        }

        if (boss.updateTelegraphedAttack(player, delta, 1.0f)) {
            return;
        }

        float distance = boss.distanceTo(player.getCenterX(), player.getCenterY());

        if (boss.canUseRepel() && ((boss.hasClosePressure() && distance < 190f) || distance < 58f)) {
            if (boss.tryRepelPlayer(player, 6f, 560f, 2.2f)) {
                return;
            }
        }

        if (boss.canUseSpecial() && distance < 170f) {
            boss.startTelegraphedAttack(155f, 0.4f, 1.35f);
            return;
        }

        if (distance > 260f) {
            if (boss.canUseRanged()) {
                boss.fireProjectileBurst(player, projectiles, 3, 10f, 480f, 8f, 1.35f);
            }

            boss.setAnimationState(AnimationState.IDLE);
            return;
        }

        if (distance > 160f) {
            boss.moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 160f);
            boss.setAnimationState(AnimationState.WALK);
        } else {
            boss.setAnimationState(AnimationState.IDLE);
        }
    }
}
