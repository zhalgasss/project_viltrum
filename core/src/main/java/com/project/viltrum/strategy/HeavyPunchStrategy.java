package com.project.viltrum.strategy;

import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;

import java.util.List;

public class HeavyPunchStrategy implements AttackStrategy {
    @Override
    public void attack(Player player, List<Enemy> enemies) {
        for (Enemy enemy : enemies) {
            if (player.getAttackBox().overlaps(enemy.getHitbox())) {
                enemy.takeDamage(player.getStats().damage + 15);
            }
        }
    }
}
