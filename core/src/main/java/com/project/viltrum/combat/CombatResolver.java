package com.project.viltrum.combat;

import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;

public final class CombatResolver {
    private CombatResolver() {
    }

    public static void damageEnemy(Player attacker, Enemy target, float damage, float knockback, boolean heavy) {
        if (target.isDead()) {
            return;
        }

        target.takeDamage(damage, attacker.getCenterX(), attacker.getCenterY(), knockback);
        EventBus.getInstance().publish(GameEvent.point(
            heavy ? GameEventType.HEAVY_HIT : GameEventType.ATTACK_HIT,
            target.getCenterX(),
            target.getCenterY()
        ));
    }
}
