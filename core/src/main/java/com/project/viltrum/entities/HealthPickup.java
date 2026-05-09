package com.project.viltrum.entities;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public class HealthPickup {
    private final float x;
    private final float y;
    private final float size = 28;
    private final float healAmount;
    private boolean active = true;

    public HealthPickup(float x, float y, float healAmount) {
        this.x = x;
        this.y = y;
        this.healAmount = healAmount;
    }

    public void update(Player player) {
        if (!active || player.getStats().hp >= player.getStats().maxHp) {
            return;
        }

        if (getHitbox().overlaps(player.getHitbox())) {
            player.getStats().heal(healAmount);
            active = false;
        }
    }

    public void render(SpriteBatch batch, Texture texture) {
        if (active) {
            batch.draw(texture, x, y, size, size);
        }
    }

    public Rectangle getHitbox() {
        return new Rectangle(x, y, size, size);
    }

    public boolean isActive() {
        return active;
    }
}
