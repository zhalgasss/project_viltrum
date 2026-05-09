package com.project.viltrum.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Player;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventListener;
import com.project.viltrum.events.GameEventType;
import com.project.viltrum.world.Room;

public class GameHud implements GameEventListener {
    private final BitmapFont font = new BitmapFont();
    private final BitmapFont titleFont = new BitmapFont();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();
    private String notification = "";
    private float notificationTimer = 0;

    public GameHud() {
        font.getData().setScale(1.2f);
        titleFont.getData().setScale(2.4f);
        EventBus.getInstance().subscribe(this);
    }

    public void render(
        OrthographicCamera uiCamera,
        SpriteBatch batch,
        Player player,
        Room room,
        boolean paused,
        float waveBannerTimer,
        float transitionAlpha
    ) {
        shapeRenderer.setProjectionMatrix(uiCamera.combined);
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        drawBar(30, 662, 250, 18, player.getStats().hp / player.getStats().maxHp, Color.RED, Color.DARK_GRAY);

        Boss boss = room.getBoss();
        if (boss != null) {
            drawBar(390, 666, 500, 16, boss.getStats().hp / boss.getStats().maxHp, Color.SCARLET, Color.DARK_GRAY);
        }

        drawRoomProgress(room.getRoomNumber());

        if (paused) {
            shapeRenderer.setColor(0f, 0f, 0f, 0.62f);
            shapeRenderer.rect(0, 0, 1280, 720);
        }

        if (transitionAlpha > 0) {
            shapeRenderer.setColor(0f, 0f, 0f, transitionAlpha);
            shapeRenderer.rect(0, 0, 1280, 720);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.setProjectionMatrix(uiCamera.combined);
        batch.begin();
        font.setColor(Color.WHITE);
        font.draw(batch, "HP " + (int) player.getStats().hp + " / " + (int) player.getStats().maxHp, 38, 692);
        font.draw(batch, "ROOM " + room.getRoomNumber() + " / 4", 1080, 692);

        if (boss != null) {
            String bossName = boss.getName().equals("Conquest") ? "MINI BOSS: CONQUEST" : "FINAL BOSS: REGENT THRAGG";
            font.draw(batch, bossName, 520, 692);
        }

        if (notificationTimer > 0) {
            font.setColor(Color.GOLD);
            font.draw(batch, notification, 560, 625);
            notificationTimer -= Math.min(0.033f, Gdx.graphics.getDeltaTime());
        }

        renderWaveBanner(batch, room.getRoomNumber(), waveBannerTimer);

        if (paused) {
            titleFont.draw(batch, "PAUSED", 555, 410);
            font.draw(batch, "ESC / P - Resume", 545, 345);
            font.draw(batch, "ENTER - Return to menu", 515, 305);
        }

        batch.end();
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(this);
        font.dispose();
        titleFont.dispose();
        shapeRenderer.dispose();
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event.type == GameEventType.ROOM_CLEARED) {
            notification = "ROOM CLEARED";
            notificationTimer = 1.5f;
        }

        if (event.type == GameEventType.BOSS_DIED) {
            notification = "BOSS DEFEATED";
            notificationTimer = 1.8f;
        }
    }

    private void drawRoomProgress(int roomNumber) {
        for (int i = 1; i <= 4; i++) {
            float x = 1030 + i * 34;
            shapeRenderer.setColor(i <= roomNumber ? Color.CYAN : Color.DARK_GRAY);
            shapeRenderer.rect(x, 640, 22, 8);
        }
    }

    private void renderWaveBanner(SpriteBatch batch, int roomNumber, float waveBannerTimer) {
        if (waveBannerTimer <= 0) {
            return;
        }

        float progress = Math.min(1f, waveBannerTimer / 2.2f);
        float alpha = progress < 0.25f ? progress / 0.25f : 1f;

        titleFont.setColor(0.35f, 0.85f, 1f, alpha);
        titleFont.draw(batch, "WAVE " + roomNumber, 540, 385);
        titleFont.setColor(Color.WHITE);
    }

    private void drawBar(float x, float y, float width, float height, float ratio, Color fill, Color back) {
        float clamped = Math.max(0, Math.min(1, ratio));
        shapeRenderer.setColor(back);
        shapeRenderer.rect(x, y, width, height);
        shapeRenderer.setColor(fill);
        shapeRenderer.rect(x, y, width * clamped, height);
        shapeRenderer.setColor(Color.WHITE);
        shapeRenderer.rect(x, y + height, width, 2);
    }
}
