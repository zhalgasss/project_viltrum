package com.project.viltrum.state;

import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Enemy;
import com.project.viltrum.entities.Player;
import com.project.viltrum.entities.Projectile;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public interface BossState {
    void update(
        Boss boss,
        Player player,
        float delta,
        List<Rectangle> obstacles,
        List<Enemy> summons,
        List<Projectile> projectiles
    );
}
