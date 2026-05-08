package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;

public class GameOverScreen implements Screen {

    private Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private Texture background;

    public GameOverScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(3);

        background = new Texture("backgrounds/game_over.png");
    }

    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();

        // ФОН
        batch.draw(background, 0, 0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight());

        // ТЕКСТ
        font.draw(batch, "EARTH HAS FALLEN",
            Gdx.graphics.getWidth() / 2f - 220,
            Gdx.graphics.getHeight() / 2f + -80);

        font.draw(batch, "PRESS ENTER TO MENU",
            Gdx.graphics.getWidth() / 2f - 270,
            Gdx.graphics.getHeight() / 2f + -150);

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
        font.dispose();
        background.dispose();
    }
}
