package com.project.viltrum.strategy;

import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.combat.CombatResolver;

import java.util.List;

public class HeavyPunchStrategy implements AttackStrategy {
    @Override
    public void attack(Player player, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (player.getAttackBox().overlaps(enemy.getHitbox())) {
                CombatResolver.damageEnemy(player, enemy, player.getStats().damage + 15, 260, true);
            }
        }
    }
}
