package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.camera.GameCameraController;
import com.project.viltrum.effects.EffectsManager;
import com.project.viltrum.entities.HeroType;
import com.project.viltrum.entities.Player;
import com.project.viltrum.events.EventBus;
import com.project.viltrum.events.GameEvent;
import com.project.viltrum.events.GameEventType;
import com.project.viltrum.factory.HeroFactory;
import com.project.viltrum.managers.GameManager;
import com.project.viltrum.managers.MusicManager;
import com.project.viltrum.ui.GameHud;
import com.project.viltrum.world.Room;

public class GameScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final OrthographicCamera camera;
    private final OrthographicCamera uiCamera;
    private final GameCameraController cameraController;
    private final EffectsManager effectsManager;
    private final GameHud hud;

    private Player player;
    private Room room;
    private float waveBannerTimer = 2.2f;
    private float transitionTimer = 0.55f;
    private boolean paused = false;

    public GameScreen(Main game, HeroType selectedHero) {
        this.game = game;
        batch = new SpriteBatch();

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        uiCamera = new OrthographicCamera();
        uiCamera.setToOrtho(false, 1280, 720);

        cameraController = new GameCameraController(camera);
        effectsManager = new EffectsManager();
        hud = new GameHud();

        player = HeroFactory.createHero(selectedHero);
        room = new Room(GameManager.getInstance().getCurrentRoom());
        MusicManager.getInstance().playGameMusic(room.hasBossFight());
    }

    @Override
    public void render(float delta) {
        float frameDelta = Math.min(delta, 1f / 30f);
        update(frameDelta);

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        room.render(batch);
        player.render(batch);
        batch.end();

        effectsManager.renderShapes(camera);
        effectsManager.renderBossTelegraph(camera, room.getBoss());

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        effectsManager.renderNumbers(batch);
        batch.end();

        hud.render(uiCamera, batch, player, room, paused, waveBannerTimer, getTransitionAlpha());
    }

    private void update(float delta) {
        handlePauseInput();

        if (paused) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
                game.setScreen(new MenuScreen(game));
            }

            cameraController.update(delta, player.getCenterX(), player.getCenterY(), room.hasBossFight());
            effectsManager.update(delta);
            return;
        }

        if (transitionTimer > 0) {
            transitionTimer -= delta;
        }

        if (waveBannerTimer > 0) {
            waveBannerTimer -= delta;
        }

        player.update(delta, room.getEnemies(), room.getObstacles(), room.getProjectiles());
        room.update(delta, player);
        effectsManager.update(delta);
        cameraController.update(delta, player.getCenterX(), player.getCenterY(), room.hasBossFight());

        if (player.getStats().isDead()) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        if (room.isCleared()) {
            advanceRoom();
        }
    }

    private void handlePauseInput() {
        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.P)) {
            paused = !paused;
        }
    }

    private void advanceRoom() {
        EventBus.getInstance().publish(GameEvent.point(GameEventType.ROOM_CLEARED, player.getCenterX(), player.getCenterY()));

        if (GameManager.getInstance().getCurrentRoom() < 4) {
            room.dispose();
            GameManager.getInstance().nextRoom();
            room = new Room(GameManager.getInstance().getCurrentRoom());
            waveBannerTimer = 2.2f;
            transitionTimer = 0.55f;
            MusicManager.getInstance().playGameMusic(room.hasBossFight());
        } else {
            game.setScreen(new WinScreen(game));
        }
    }

    private float getTransitionAlpha() {
        if (transitionTimer <= 0) {
            return 0;
        }

        return Math.min(0.8f, transitionTimer / 0.55f);
    }

    @Override
    public void show() {
    }

    @Override
    public void resize(int width, int height) {
        camera.setToOrtho(false, 1280, 720);
        uiCamera.setToOrtho(false, 1280, 720);
    }

    @Override
    public void pause() {
        paused = true;
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
        hud.dispose();
        effectsManager.dispose();
        cameraController.dispose();
        room.dispose();
        player.dispose();
    }
}
