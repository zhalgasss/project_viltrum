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

        if (boss.canSummon()) {
            boss.summonFlaxans(summons, 3);
            boss.resetSummonCooldown(5.2f);
        }

        if (boss.canUseSpecial() && distance < 165) {
            boss.startTelegraphedAttack(165, 1.5f, 2.0f);
            return;
        }

        if (distance > 64) {
            boss.moveToward(player.getCenterX(), player.getCenterY(), delta, obstacles, 64);
            boss.setAnimationState(AnimationState.WALK);
        } else {
            boss.setAnimationState(AnimationState.IDLE);
        }
    }
}
