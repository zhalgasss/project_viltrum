package com.project.viltrum.screens;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Texture.TextureFilter;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.video.VideoPlayer;
import com.badlogic.gdx.video.VideoPlayerCreator;
import com.project.viltrum.Main;
import com.project.viltrum.managers.MusicManager;

public class WinScreen implements Screen {
    private enum State {
        VICTORY_VIDEO,
        CREDITS
    }

    private static final String[] CREDITS = {
        "=== VICTORY ===",
        "",
        "EARTH HAS BEEN SAVED",
        "",
        "Mark Grayson stood against the Viltrum Empire...",
        "and won.",
        "",
        "The cities are rebuilding.",
        "Humanity survives another day.",
        "",
        "But somewhere in deep space...",
        "the remaining Viltrumites are watching.",
        "",
        "TO BE CONTINUED...",
        "",
        "========================",
        "",
        "Lead Developer:",
        "Akzhol Khassengaziyev",
        "",
        "Programmer 2:",
        "Zhumagaliuly Zhalgas",
        "",
        "Sprite & Visual:",
        "Oryntai Makpal",
        "",
        "Inspired by:",
        "Invincible Universe",
        "",
        "Powered by:",
        "Java + LibGDX",
        "",
        "THANK YOU FOR PLAYING"
    };

    private static final float CREDITS_SCROLL_SPEED = 42f;
    private static final float CREDITS_LINE_SPACING = 38f;
    private static final float CREDITS_TITLE_SPACING = 58f;
    private static final float CREDITS_SECTION_SPACING = 24f;
    private static final float CREDITS_RESET_PADDING = 120f;
    private static final String VICTORY_VIDEO_PATH = "backgrounds/victory.webm";

    private final Main game;
    private final SpriteBatch batch;
    private final BitmapFont titleFont;
    private final BitmapFont font;
    private final GlyphLayout layout;
    private final ScreenTransition transition;
    private final VideoPlayer videoPlayer;
    private State state = State.VICTORY_VIDEO;
    private float creditsHeadY = -CREDITS_RESET_PADDING;
    private boolean creditsStarted = false;

    public WinScreen(Main game) {
        this.game = game;
        batch = new SpriteBatch();
        titleFont = new BitmapFont();
        titleFont.getData().setScale(2.6f);
        font = new BitmapFont();
        font.getData().setScale(1.35f);
        layout = new GlyphLayout();
        transition = new ScreenTransition();
        videoPlayer = VideoPlayerCreator.createVideoPlayer();
        if (startVictoryVideo()) {
            MusicManager.getInstance().playVictoryMusic();
        }
    }

    @Override
    public void render(float delta) {
        float frameDelta = Math.min(delta, 1f / 30f);
        MusicManager.getInstance().update(frameDelta);
        transition.update(frameDelta);

        if (state == State.VICTORY_VIDEO) {
            videoPlayer.update();
        } else {
            updateCredits(frameDelta);
        }

        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        float centerX = Gdx.graphics.getWidth() / 2f;

        if (state == State.VICTORY_VIDEO) {
            drawVictoryVideo();
        } else {
            drawCredits(centerX);
            drawCentered(font, "PRESS ENTER TO RETURN TO MENU", centerX, 34f, Color.WHITE);
        }

        transition.draw(batch, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        batch.end();

        if (state == State.CREDITS && !transition.isExiting() && Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
            MusicManager.getInstance().playSound("menu_select");
            transition.startExit(() -> game.setScreen(new MenuScreen(game)));
        }
    }

    private boolean startVictoryVideo() {
        FileHandle videoFile = Gdx.files.internal(VICTORY_VIDEO_PATH);

        try {
            if (!videoPlayer.load(videoFile)) {
                startCredits();
                return false;
            }

            videoPlayer.setLooping(false);
            videoPlayer.setFilter(TextureFilter.Linear, TextureFilter.Linear);
            videoPlayer.setVolume(0f);
            videoPlayer.setOnCompletionListener(file -> startCredits());
            videoPlayer.play();
            return true;
        } catch (Exception exception) {
            Gdx.app.error("WinScreen", "Could not play victory video: " + VICTORY_VIDEO_PATH, exception);
            startCredits();
            return false;
        }
    }

    private void startCredits() {
        if (creditsStarted) {
            return;
        }

        creditsStarted = true;
        state = State.CREDITS;
        creditsHeadY = getCreditsStartY();
        videoPlayer.stop();
    }

    private void drawVictoryVideo() {
        Texture frame = videoPlayer.getTexture();

        if (frame == null) {
            return;
        }

        drawCoverTexture(frame, videoPlayer.getVideoWidth(), videoPlayer.getVideoHeight());
    }

    private void drawCoverTexture(Texture texture, float sourceWidth, float sourceHeight) {
        float screenWidth = Gdx.graphics.getWidth();
        float screenHeight = Gdx.graphics.getHeight();
        float screenRatio = screenWidth / screenHeight;
        float textureRatio = sourceWidth / sourceHeight;
        float drawWidth;
        float drawHeight;

        if (textureRatio > screenRatio) {
            drawHeight = screenHeight;
            drawWidth = drawHeight * textureRatio;
        } else {
            drawWidth = screenWidth;
            drawHeight = drawWidth / textureRatio;
        }

        float x = (screenWidth - drawWidth) / 2f;
        float y = (screenHeight - drawHeight) / 2f;
        batch.draw(texture, x, y, drawWidth, drawHeight, 0, 0, (int) sourceWidth, (int) sourceHeight, false, false);
    }

    private void updateCredits(float delta) {
        creditsHeadY += CREDITS_SCROLL_SPEED * delta;

        float creditsTailY = creditsHeadY - getCreditsBlockHeight();
        if (creditsTailY > Gdx.graphics.getHeight() + CREDITS_RESET_PADDING) {
            creditsHeadY = getCreditsStartY();
        }
    }

    private float getCreditsStartY() {
        return 0f;
    }

    private void drawCredits(float centerX) {
        float y = creditsHeadY;

        for (String line : CREDITS) {
            if (line.isEmpty()) {
                y -= CREDITS_SECTION_SPACING;
                continue;
            }

            BitmapFont activeFont = getCreditFont(line);
            drawCentered(activeFont, line, centerX, y, Color.WHITE);
            y -= getCreditSpacing(line);
        }
    }

    private float getCreditsBlockHeight() {
        float height = 0;

        for (String line : CREDITS) {
            height += line.isEmpty() ? CREDITS_SECTION_SPACING : getCreditSpacing(line);
        }

        return height;
    }

    private BitmapFont getCreditFont(String line) {
        if (line.startsWith("===") || line.equals("EARTH HAS BEEN SAVED") || line.equals("THANK YOU FOR PLAYING")) {
            return titleFont;
        }

        return font;
    }

    private float getCreditSpacing(String line) {
        if (line.startsWith("===") || line.equals("EARTH HAS BEEN SAVED") || line.equals("THANK YOU FOR PLAYING")) {
            return CREDITS_TITLE_SPACING;
        }

        return CREDITS_LINE_SPACING;
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

    @Override
    public void pause() {
        if (state == State.VICTORY_VIDEO) {
            videoPlayer.pause();
        }
    }

    @Override
    public void resume() {
        if (state == State.VICTORY_VIDEO) {
            videoPlayer.play();
        }
    }

    @Override
    public void hide() {
        videoPlayer.pause();
    }

    @Override
    public void dispose() {
        batch.dispose();
        titleFont.dispose();
        font.dispose();
        videoPlayer.dispose();
        transition.dispose();
    }
}
