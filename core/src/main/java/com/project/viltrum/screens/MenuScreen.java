package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.entities.HeroType;

public class MenuScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private BitmapFont font;

    public MenuScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        font = new BitmapFont();
        font.getData().setScale(2);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.01f, 0.01f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        font.draw(batch, "INVINCIBLE: FALL OF EARTH", 360, 650);
        font.draw(batch, "Choose your hero:", 100, 560);

        font.draw(batch, "1 - Invincible / Mark", 120, 500);
        font.draw(batch, "2 - Omni-Man / Nolan", 120, 450);
        font.draw(batch, "3 - Techno Jacket", 120, 400);

        font.draw(batch, "LOCKED HEROES", 720, 560);
        font.draw(batch, "Atom Eve - Available after updates", 720, 500);
        font.draw(batch, "Oliver - Available after updates", 720, 460);
        font.draw(batch, "Allen - Available after updates", 720, 420);
        font.draw(batch, "Dinosaurus - Available after updates", 720, 380);
        font.draw(batch, "Battle Beast - Available after updates", 720, 340);
        font.draw(batch, "Cosmic Rider - Available after updates", 720, 300);
        font.draw(batch, "Thaedus - Available after updates", 720, 260);

        font.draw(batch, "WASD - Move | SPACE - Attack", 360, 90);

        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            game.setScreen(new GameScreen(game, HeroType.INVINCIBLE));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            game.setScreen(new GameScreen(game, HeroType.OMNI_MAN));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            game.setScreen(new GameScreen(game, HeroType.TECHNO_JACKET));
        }
    }

    @Override public void show() {}
    @Override public void resize(int width, int height) {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
