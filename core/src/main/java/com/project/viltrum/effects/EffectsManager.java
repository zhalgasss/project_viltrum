package com.project.viltrum.effects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.project.viltrum.entities.Boss;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventListener;
import com.project.viltrum.events.GameEventType;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class EffectsManager implements GameEventListener {
    private final List<DamageNumber> damageNumbers = new ArrayList<>();
    private final List<ImpactEffect> impactEffects = new ArrayList<>();
    private final List<MeleeEffect> meleeEffects = new ArrayList<>();
    private final BitmapFont font = new BitmapFont();
    private final ShapeRenderer shapeRenderer = new ShapeRenderer();

    public EffectsManager() {
        font.getData().setScale(1.1f);
        EventBus.getInstance().subscribe(this);
    }

    public void update(float delta) {
        updateList(damageNumbers, delta);
        updateList(impactEffects, delta);
        updateList(meleeEffects, delta);
    }

    public void renderShapes(OrthographicCamera camera) {
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);

        for (MeleeEffect effect : meleeEffects) {
            float alpha = effect.timer / effect.duration;
            shapeRenderer.setColor(1f, 0.86f, 0.25f, 0.28f * alpha);
            shapeRenderer.rect(effect.x, effect.y, effect.width, effect.height);
        }

        for (ImpactEffect effect : impactEffects) {
            float alpha = effect.timer / effect.duration;
            shapeRenderer.setColor(effect.color.r, effect.color.g, effect.color.b, 0.8f * alpha);
            shapeRenderer.circle(effect.x, effect.y, effect.radius * (1f + (1f - alpha) * 1.8f), 18);
        }

        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    public void renderNumbers(SpriteBatch batch) {
        for (DamageNumber number : damageNumbers) {
            font.setColor(number.color.r, number.color.g, number.color.b, number.timer / number.duration);
            font.draw(batch, number.text, number.x, number.y + (1f - number.timer / number.duration) * 28f);
        }

        font.setColor(Color.WHITE);
    }

    public void renderBossTelegraph(OrthographicCamera camera, Boss boss) {
        if (boss == null || !boss.isTelegraphingAttack()) {
            return;
        }

        float alpha = 0.18f + boss.getTelegraphProgress() * 0.32f;
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(1f, 0.05f, 0.02f, alpha);
        shapeRenderer.circle(boss.getCenterX(), boss.getCenterY(), boss.getTelegraphRadius(), 48);
        shapeRenderer.setColor(1f, 0.85f, 0.1f, 0.4f);
        shapeRenderer.circle(boss.getCenterX(), boss.getCenterY(), 10 + boss.getTelegraphProgress() * 8f, 20);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
    }

    @Override
    public void onGameEvent(GameEvent event) {
        if (event.type == GameEventType.ENEMY_DAMAGED || event.type == GameEventType.BOSS_DAMAGED) {
            damageNumbers.add(new DamageNumber(event.x, event.y + 38, "-" + (int) event.value, Color.ORANGE));
            impactEffects.add(new ImpactEffect(event.x, event.y, 10, Color.ORANGE));
        }

        if (event.type == GameEventType.PLAYER_DAMAGED) {
            damageNumbers.add(new DamageNumber(event.x, event.y + 42, "-" + (int) event.value, Color.SCARLET));
            impactEffects.add(new ImpactEffect(event.x, event.y, 12, Color.SCARLET));
        }

        if (event.type == GameEventType.ATTACK_HIT) {
            impactEffects.add(new ImpactEffect(event.x, event.y, 14, Color.WHITE));
        }

        if (event.type == GameEventType.HEAVY_HIT) {
            impactEffects.add(new ImpactEffect(event.x, event.y, 22, Color.GOLD));
        }

        if (event.type == GameEventType.ENEMY_DIED || event.type == GameEventType.BOSS_DIED) {
            for (int i = 0; i < 5; i++) {
                impactEffects.add(new ImpactEffect(event.x + i * 7 - 14, event.y + i * 4 - 8, 9 + i * 2, Color.LIGHT_GRAY));
            }
        }

        if (event.type == GameEventType.PLAYER_ATTACK) {
            meleeEffects.add(new MeleeEffect(event.x, event.y, event.width, event.height));
        }
    }

    public void dispose() {
        EventBus.getInstance().unsubscribe(this);
        font.dispose();
        shapeRenderer.dispose();
    }

    private <T extends TimedEffect> void updateList(List<T> effects, float delta) {
        Iterator<T> iterator = effects.iterator();

        while (iterator.hasNext()) {
            T effect = iterator.next();
            effect.timer -= delta;

            if (effect.timer <= 0) {
                iterator.remove();
            }
        }
    }

    private abstract static class TimedEffect {
        float timer;
        float duration;
    }

    private static class DamageNumber extends TimedEffect {
        final float x;
        final float y;
        final String text;
        final Color color;

        DamageNumber(float x, float y, String text, Color color) {
            this.x = x;
            this.y = y;
            this.text = text;
            this.color = color;
            this.duration = 0.85f;
            this.timer = duration;
        }
    }

    private static class ImpactEffect extends TimedEffect {
        final float x;
        final float y;
        final float radius;
        final Color color;

        ImpactEffect(float x, float y, float radius, Color color) {
            this.x = x;
            this.y = y;
            this.radius = radius;
            this.color = color;
            this.duration = 0.28f;
            this.timer = duration;
        }
    }

    private static class MeleeEffect extends TimedEffect {
        final float x;
        final float y;
        final float width;
        final float height;

        MeleeEffect(float x, float y, float width, float height) {
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
            this.duration = 0.14f;
            this.timer = duration;
        }
    }
}
