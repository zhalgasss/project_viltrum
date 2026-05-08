package com.project.viltrum.utils;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.project.viltrum.entities.AnimationState;
import com.project.viltrum.entities.Direction;
import com.project.viltrum.entities.HeroType;

public class AnimationManager {
    private Texture sheet;
    private TextureRegion frame;

    public AnimationManager(HeroType type) {
        if (type == HeroType.INVINCIBLE) {
            sheet = new Texture("characters/mark.png");

            // Один нормальный кадр Mark из верхнего левого idle блока
            frame = new TextureRegion(sheet, 70, 70, 120, 210);

        } else if (type == HeroType.OMNI_MAN) {
            sheet = new Texture("characters/omniman.png");

            // Один нормальный кадр Omni-Man
            frame = new TextureRegion(sheet, 75, 70, 120, 210);

        } else {
            sheet = new Texture("characters/techno_jacket.png");

            // Один нормальный кадр Techno Jacket
            frame = new TextureRegion(sheet, 75, 70, 120, 210);
        }
    }

    public TextureRegion getFrame(AnimationState state, Direction direction, int frameIndex) {
        return frame;
    }

    public void dispose() {
        sheet.dispose();
    }
}
