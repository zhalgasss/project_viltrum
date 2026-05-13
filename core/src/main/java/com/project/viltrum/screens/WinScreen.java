package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.project.viltrum.Main;
import com.project.viltrum.managers.MusicManager;

public class WinScreen implements Screen {
    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont font;
    private final Texture background;
    private final GlyphLayout layout;
    private final ScreenTransition transition;

    public WinScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(3.2f);
        font = new BitmapFont();
        font.getData().setScale(1.35f);
        background = new Texture("backgrounds/victory.png");
        layout = new GlyphLayout();
        transition = new ScreenTransition();
        MusicManager.getInstance().playVictoryMusic();
    }

    @Override
    public void render(float delta) {
        float frameDelta = Math.min(delta, 1f / 30f);
        MusicManager.getInstance().update(frameDelta);
        transition.update(frameDelta);

        Gdx.gl.glClearColor(0.01f, 0.06f, 0.05f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        drawCoverBackground();
        float centerX = Gdx.graphics.getWidth() / 2f;
        float centerY = Gdx.graphics.getHeight() / 2f;
        drawCentered(titleFont, "EARTH IS SAVED", centerX, centerY + 78, Color.CYAN);
        drawCentered(font, "Regent Thragg has been defeated.", centerX, centerY + 5, Color.WHITE);
        drawCentered(font, "PRESS ENTER TO RETURN TO MENU", centerX, centerY - 58, Color.WHITE);
        transition.draw(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (!transition.isExiting() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            MusicManager.getInstance().playSound("menu_select");
            transition.startExit(() -> game.setScreen(new MenuScreen(game)));
        }
    }

    private void drawCoverBackground() {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float screenRatio = screenWidth / screenHeight;
        float textureRatio = (float) background.getWidth() / background.getHeight();
        float drawWidth;
        float drawHeight;

        if (textureRatio > screenRatio) {
            drawHeight = screenHeight;
            drawWidth = drawHeight * textureRatio;
        } else {
            drawWidth = screenWidth;
            drawHeight = drawWidth / textureRatio;
        }

        batch.draw(background, (screenWidth - drawWidth) / 2f, (screenHeight - drawHeight) / 2f, drawWidth, drawHeight);
    }

    private void drawCentered(BitmapFont activeFont, String text, float centerX, float centerY, Color color) {
        layout.setText(activeFont, text);
        float x = centerX - layout.width / 2f;
        float y = centerY + layout.height / 2f;

        activeFont.setColor(0f, 0f, 0f, 0.85f);
        activeFont.draw(batch, text, x + 3f, y - 3f);
        activeFont.setColor(color);
        activeFont.draw(batch, text, x, y);
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
        transition.dispose();
    }
}
