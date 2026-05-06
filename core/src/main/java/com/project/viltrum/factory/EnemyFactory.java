package com.project.viltrum.factory;

import com.project.viltrum.entities.*;
import com.project.viltrum.state.BossChaseState;

public class EnemyFactory {
    public static Enemy createFlaxan(float x, float y) {
        return new Enemy(
            "Flaxan Soldier",
            new CharacterStats(45, 120, 8),
            x,
            y
        );
    }

    public static Boss createConquest(float x, float y) {
        return new Boss(
            "Conquest",
            new CharacterStats(320, 150, 28),
            x,
            y,
            new BossChaseState()
        );
    }

    public static Boss createThragg(float x, float y) {
        return new Boss(
            "Regent Thragg",
            new CharacterStats(550, 170, 35),
            x,
            y,
            new BossChaseState()
        );
    }
}
