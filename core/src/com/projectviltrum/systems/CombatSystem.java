package com.projectviltrum.systems;

import com.projectviltrum.entities.Entity;

public class CombatSystem {

    public void attack(Entity attacker, Entity target) {
        target.health -= 10;
        System.out.println("Attack happened!");
    }
}