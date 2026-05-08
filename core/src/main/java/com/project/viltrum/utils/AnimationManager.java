package com.project.viltrum.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.project.viltrum.entities.AnimationState;
import com.project.viltrum.entities.Direction;
import com.project.viltrum.entities.HeroType;

public class AnimationManager {
    private Texture sheet;

    public AnimationManager(HeroType type) {
        if (type == HeroType.INVINCIBLE) {
            sheet = new Texture("characters/mark.png");
        } else if (type == HeroType.OMNI_MAN) {
            sheet = new Texture("characters/omniman.png");
        } else {
            sheet = new Texture("characters/techno_jacket.png");
        }
    }

    public TextureRegion getFrame(AnimationState state, Direction direction, int frameIndex) {
        TextureRegion[][] regions = TextureRegion.split(sheet, sheet.getWidth() / 4, sheet.getHeight() / 8);

        int row = 0;

        if (state == AnimationState.IDLE) {
            if (direction == Direction.DOWN) row = 0;
            if (direction == Direction.LEFT) row = 1;
            if (direction == Direction.RIGHT) row = 2;
            if (direction == Direction.UP) row = 3;
        }

        if (state == AnimationState.ATTACK) {
            if (direction == Direction.DOWN) row = 4;
            if (direction == Direction.LEFT) row = 5;
            if (direction == Direction.RIGHT) row = 6;
            if (direction == Direction.UP) row = 7;
        }

        int col = frameIndex % 4;
        return regions[row][col];
    }

    public void dispose() {
        sheet.dispose();
    }
}
