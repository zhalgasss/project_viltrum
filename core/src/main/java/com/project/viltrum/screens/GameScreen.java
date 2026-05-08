package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.entities.HeroType;
import com.project.viltrum.entities.Player;
import com.project.viltrum.factory.HeroFactory;
import com.project.viltrum.managers.GameManager;
import com.project.viltrum.world.Room;

public class GameScreen implements Screen {
    private Main game;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera camera;

    private Player player;
    private Room room;

    public GameScreen(Main game, HeroType selectedHero) {
        this.game = game;
        batch = new SpriteBatch();

        font = new BitmapFont();
        font.getData().setScale(1.5f);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, 1280, 720);

        player = HeroFactory.createHero(selectedHero);
        room = new Room(GameManager.getInstance().getCurrentRoom());
    }

    @Override
    public void render(float delta) {
        update(delta);

        Gdx.gl.glClearColor(0.02f, 0.02f, 0.04f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.setProjectionMatrix(camera.combined);
        batch.begin();

        room.render(batch);
        player.render(batch);

        font.draw(batch, "Room: " + room.getRoomNumber() + " / 4", 30, 690);
        font.draw(batch, "HP: " + (int) player.getStats().hp + " / " + (int) player.getStats().maxHp, 30, 650);

        if (room.getRoomNumber() == 3) {
            font.draw(batch, "MINI BOSS: CONQUEST", 500, 690);
        }

        if (room.getRoomNumber() == 4) {
            font.draw(batch, "FINAL BOSS: REGENT THRAGG", 450, 690);
        }

        batch.end();
    }

    private void update(float delta) {
        player.update(delta, room.getEnemies());
        room.update(delta, player);

        if (player.getStats().isDead()) {
            game.setScreen(new GameOverScreen(game));
            return;
        }

        if (room.isCleared()) {
            if (GameManager.getInstance().getCurrentRoom() < 4) {
                room.dispose();
                GameManager.getInstance().nextRoom();
                room = new Room(GameManager.getInstance().getCurrentRoom());
            } else {
                game.setScreen(new WinScreen(game));
            }
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
        room.dispose();
        player.dispose();
    }
}
