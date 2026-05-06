package com.project.viltrum.strategy;

import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Enemy;

import java.util.List;

public interface AttackStrategy {
    void attack(Player player, List<Enemy> enemies);
}
