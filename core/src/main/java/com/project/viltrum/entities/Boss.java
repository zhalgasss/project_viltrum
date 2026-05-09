package com.project.viltrum.entities;

import com.project.viltrum.state.BossState;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

public class Boss extends Enemy {
    private BossState state;

    public Boss(String name, CharacterStats stats, float x, float y, BossState state) {
        super(name, stats, x, y);
        this.state = state;
    }

    @Override
    public void update(float delta, Player player, List<Rectangle> obstacles) {
        facePlayer(player);
        state.update(this, player, delta);
    }

    public void setState(BossState state) {
        this.state = state;
    }
}
