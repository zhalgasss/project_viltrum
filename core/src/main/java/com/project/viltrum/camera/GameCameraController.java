package com.project.viltrum.camera;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.MathUtils;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventListener;
import com.project.viltrum.events.GameEventType;

public class GameCameraController implements GameEventListener {
    private static final float WORLD_WIDTH = 1280;
    private static final float WORLD_HEIGHT = 720;

    private final OrthographicCamera camera;
    private float shakeTimer = 0;
    private float shakeIntensity = 0;

    public GameCameraController(OrthographicCamera camera) {
        this.camera = camera;
        EventBus.getInstance().subscribe(this);
    }

    public void update(float delta, float targetX, float targetY, boolean bossFight) {
        float targetZoom = bossFight ? 0.9f : 1f;
        camera.zoom += (targetZoom - camera.zoom) * Math.min(1f, delta * 4f);

        float halfWidth = camera.viewportWidth * camera.zoom / 2f;
        float halfHeight = camera.viewportHeight * camera.zoom / 2f;
        float desiredX = MathUtils.clamp(targetX, halfWidth, WORLD_WIDTH - halfWidth);
        float desiredY = MathUtils.clamp(targetY, halfHeight, WORLD_HEIGHT - halfHeight);

        camera.position.x += (desiredX - camera.position.x) * Math.min(1f, delta * 6f);
        camera.position.y += (desiredY - camera.position.y) * Math.min(1f, delta * 6f);

        if (shakeTimer > 0) {
            shakeTimer -= delta;
            float shake = shakeIntensity * (shakeTimer / 0.25f);
            camera.position.x += MathUtils.random(-shake, shake);
            camera.position.y += MathUtils.random(-shake, shake);
        }

        camera.update();
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event.type == GameEventType.HEAVY_HIT) {
            shake(0.25f, 10f);
        }

        if (event.type == GameEventType.PLAYER_DAMAGED) {
            shake(0.18f, 6f);
        }

        if (event.type == GameEventType.DASH) {
            shake(0.1f, 3f);
        }
    }

    public void shake(float duration, float intensity) {
        shakeTimer = Math.max(shakeTimer, duration);
        shakeIntensity = Math.max(shakeIntensity, intensity);
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(this);
    }
}
