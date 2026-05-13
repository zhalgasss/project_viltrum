package com.project.viltrum.screens;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.managers.MusicManager;

public class ScreenTransition {
    public static final float DEFAULT_DURATION = 0.65f;

    private final Texture pixel;
    private float fadeInTimer = DEFAULT_DURATION;
    private float fadeOutTimer = 0;
    private float duration = DEFAULT_DURATION;
    private boolean exiting = false;
    private Runnable onFadeOutComplete;

    public ScreenTransition() {
        Pixmap pixmap = new Pixmap(1, 1, Pixmap.Format.RGBA8888);
        pixmap.setColor(Color.WHITE);
        pixmap.fill();
        pixel = new Texture(pixmap);
        pixmap.dispose();
    }

    public void update(float delta) {
        if (fadeInTimer > 0) {
            fadeInTimer = Math.max(0, fadeInTimer - delta);
        }

        if (!exiting) {
            return;
        }

        fadeOutTimer += delta;

        if (fadeOutTimer < duration || onFadeOutComplete == null) {
            return;
        }

        Runnable callback = onFadeOutComplete;
        onFadeOutComplete = null;
        callback.run();
    }

    public void startExit(Runnable onFadeOutComplete) {
        if (exiting) {
            return;
        }

        exiting = true;
        fadeOutTimer = 0;
        this.onFadeOutComplete = onFadeOutComplete;
        MusicManager.getInstance().fadeOut(duration);
    }

    public void draw(SpriteBatch batch, float width, float height) {
        float alpha = getAlpha();

        if (alpha <= 0) {
            return;
        }

        batch.setColor(0f, 0f, 0f, alpha);
        batch.draw(pixel, 0, 0, width, height);
        batch.setColor(Color.WHITE);
    }

    public boolean isExiting() {
        return exiting;
    }

    public void dispose() {
        pixel.dispose();
    }

    private float getAlpha() {
        if (exiting) {
            return Math.min(1f, fadeOutTimer / duration);
        }

        return Math.min(1f, fadeInTimer / duration);
    }
}
