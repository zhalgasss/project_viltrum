package com.project.viltrum.factory;

import com.project.viltrum.entities.*;
import com.project.viltrum.strategy.*;

public class HeroFactory {
    public static Player createHero(HeroType type) {
        switch (type) {
            case INVINCIBLE:
                return new Player(
                    HeroType.INVINCIBLE,
                    new CharacterStats(120, 230, 50),
                    new PunchAttackStrategy(),
                    550,
                    250
                );

            case OMNI_MAN:
                return new Player(
                    HeroType.OMNI_MAN,
                    new CharacterStats(180, 190, 40),
                    new HeavyPunchStrategy(),
                    550,
                    250
                );

            case TECHNO_JACKET:
                return new Player(
                    HeroType.TECHNO_JACKET,
                    new CharacterStats(130, 260, 22),
                    new EnergyBlastStrategy(),
                    550,
                    250
                );

            default:
                throw new IllegalArgumentException("This hero is locked");
        }
    }
}
