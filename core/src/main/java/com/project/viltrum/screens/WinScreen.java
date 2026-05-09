package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.managers.MusicManager;

public class WinScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont font;

    public WinScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);
        font = new BitmapFont();
        font.getData().setScale(1.35f);
        MusicManager.getInstance().stopMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.01f, 0.06f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        titleFont.setColor(Color.CYAN);
        titleFont.draw(batch, "EARTH IS SAVED", 420, 430);
        font.setColor(Color.WHITE);
        font.draw(batch, "Regent Thragg has been defeated.", 475, 355);
        font.draw(batch, "PRESS ENTER TO RETURN TO MENU", 455, 295);
        batch.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            game.setScreen(new MenuScreen(game));
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
        titleFont.dispose();
        font.dispose();
    }
}
