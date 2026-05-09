package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.managers.MusicManager;

public class GameOverScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont font;
    private final Texture background;

    public GameOverScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);
        font = new BitmapFont();
        font.getData().setScale(1.4f);
        background = new Texture("backgrounds/game_over.png");
        MusicManager.getInstance().stopMusic();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(background, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        titleFont.setColor(Color.SCARLET);
        titleFont.draw(batch, "EARTH HAS FALLEN", Gdx.graphics.getWidth() / 2f - 280, Gdx.graphics.getHeight() / 2f + 30);
        font.setColor(Color.WHITE);
        font.draw(batch, "The Viltrumite assault overwhelmed the defense.", Gdx.graphics.getWidth() / 2f - 260, Gdx.graphics.getHeight() / 2f - 35);
        font.draw(batch, "PRESS ENTER TO RETURN TO MENU", Gdx.graphics.getWidth() / 2f - 235, Gdx.graphics.getHeight() / 2f - 95);
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
        background.dispose();
    }
}
