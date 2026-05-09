package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.project.viltrum.Main;
import com.project.viltrum.entities.HeroType;
import com.project.viltrum.managers.GameManager;
import com.project.viltrum.managers.MusicManager;

public class MenuScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final ShapeRenderer shapeRenderer;
    private final BitmapFont titleFont;
    private final BitmapFont menuFont;
    private final BitmapFont smallFont;
    private final Texture background;
    private final OrthographicCamera camera;

    private int selectedHero = 0;

    public MenuScreen(Main game) {
        this.game = game;

        batch = new SpriteBatch();
        shapeRenderer = new ShapeRenderer();
        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.2f);

        menuFont = new BitmapFont();
        menuFont.getData().setScale(1.45f);

        smallFont = new BitmapFont();
        smallFont.getData().setScale(1.05f);

        background = new Texture("backgrounds/main_menu.jpg");
        GameManager.getInstance().reset();
        MusicManager.getInstance().playMenuMusic();
    }

    @Override
    public void render(float delta) {
        handleInput();

        Gdx.gl.glClearColor(0.01f, 0.01f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        batch.draw(background, 0, 0, 1280, 720);
        batch.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.setProjectionMatrix(camera.combined);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.42f);
        shapeRenderer.rect(470, 208, 360, 360);
        shapeRenderer.setColor(0.15f, 0.75f, 1f, 0.36f);
        shapeRenderer.rect(500, 470 - selectedHero * 54, 285, 38);
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);

        batch.begin();
        titleFont.setColor(Color.WHITE);
        titleFont.draw(batch, "INVINCIBLE: FALL OF EARTH", 350, 640);

        menuFont.setColor(Color.CYAN);
        menuFont.draw(batch, "CHOOSE YOUR HERO", 520, 555);

        drawHeroOption("Invincible / Mark", 0, 500);
        drawHeroOption("Omni-Man / Nolan", 1, 446);
        drawHeroOption("Techno Jacket", 2, 392);

        menuFont.setColor(Color.LIGHT_GRAY);
        menuFont.draw(batch, "LOCKED HEROES", 850, 530);
        smallFont.setColor(Color.GRAY);
        smallFont.draw(batch, "Atom Eve", 850, 490);
        smallFont.draw(batch, "Oliver", 850, 460);
        smallFont.draw(batch, "Allen", 850, 430);
        smallFont.draw(batch, "Dinosaurus", 850, 400);
        smallFont.draw(batch, "Battle Beast", 850, 370);
        smallFont.draw(batch, "Cosmic Rider", 850, 340);
        smallFont.draw(batch, "Thaedus", 850, 310);

        smallFont.setColor(Color.WHITE);
        smallFont.draw(batch, "WASD - Move | SPACE - Attack | SHIFT - Dash | P - Pause", 390, 116);
        smallFont.draw(batch, "UP / DOWN to select, ENTER or 1-3 to start", 430, 78);
        batch.end();
    }

    private void drawHeroOption(String text, int index, float y) {
        menuFont.setColor(index == selectedHero ? Color.WHITE : Color.LIGHT_GRAY);
        menuFont.draw(batch, (index == selectedHero ? "> " : "  ") + (index + 1) + " - " + text, 505, y);
    }

    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.UP)) {
            selectedHero = (selectedHero + 2) % 3;
            MusicManager.getInstance().playSound("menu_select");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) {
            selectedHero = (selectedHero + 1) % 3;
            MusicManager.getInstance().playSound("menu_select");
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_1)) {
            selectedHero = 0;
            startGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_2)) {
            selectedHero = 1;
            startGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.NUM_3)) {
            selectedHero = 2;
            startGame();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            startGame();
        }
    }

    private void startGame() {
        MusicManager.getInstance().playSound("menu_select");
        game.setScreen(new GameScreen(game, selectedHeroType()));
    }

    private HeroType selectedHeroType() {
        if (selectedHero == 0) return HeroType.INVINCIBLE;
        if (selectedHero == 1) return HeroType.OMNI_MAN;
        return HeroType.TECHNO_JACKET;
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1280, 720);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        batch.dispose();
        shapeRenderer.dispose();
        titleFont.dispose();
        menuFont.dispose();
        smallFont.dispose();
        background.dispose();
    }
}
