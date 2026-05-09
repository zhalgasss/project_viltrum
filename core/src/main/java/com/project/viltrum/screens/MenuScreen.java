package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.entities.HeroType;
import com.project.viltrum.managers.GameManager;

public class MenuScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private BitmapFont titleFont;
    private BitmapFont menuFont;
    private BitmapFont smallFont;
    private Texture background;
    private OrthographicCamera camera;

    public MenuScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);

        menuFont = new BitmapFont();
        menuFont.getData().setScale(1.5f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.1f);

        background = new Texture("backgrounds/main_menu.jpg");
        GameManager.getInstance().reset();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.01f, 0.01f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        batch.draw(
            background,
            0,
            0,
            Gdx.graphics.getWidth(),
            Gdx.graphics.getHeight()
        );

        titleFont.draw(batch, "INVINCIBLE: FALL OF EARTH", 600, 760);

        menuFont.draw(batch, "CHOOSE YOUR HERO", 620, 620);

        menuFont.draw(batch, "1 - Invincible / Mark", 520, 550);
        menuFont.draw(batch, "2 - Omni-Man / Nolan", 520, 500);
        menuFont.draw(batch, "3 - Techno Jacket", 520, 450);

        menuFont.draw(batch, "LOCKED HEROES", 760, 560);
        smallFont.draw(batch, "Atom Eve - Available after updates", 760, 510);
        smallFont.draw(batch, "Oliver - Available after updates", 760, 475);
        smallFont.draw(batch, "Allen - Available after updates", 760, 440);
        smallFont.draw(batch, "Dinosaurus - Available after updates", 760, 405);
        smallFont.draw(batch, "Battle Beast - Available after updates", 760, 370);
        smallFont.draw(batch, "Cosmic Rider - Available after updates", 760, 335);
        smallFont.draw(batch, "Thaedus - Available after updates", 760, 300);

        smallFont.draw(batch,
            "WASD - Move | SPACE - Attack | SHIFT - Dash",
            550,
            120);

        smallFont.draw(batch,
            "Press 1, 2, or 3 to start",
            630,
            80);

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
    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, width, height);
    }
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        menuFont.dispose();
        smallFont.dispose();
        background.dispose();
    }
}
