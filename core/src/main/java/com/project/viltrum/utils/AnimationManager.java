package com.project.viltrum.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.utils.Array;
import com.project.viltrum.entities.AnimationState;
import com.project.viltrum.entities.Direction;
import com.project.viltrum.entities.HeroType;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AnimationManager {
    private static final float FRAME_DURATION = 0.13f;

    private final Map<AnimationState, Map<Direction, Animation<TextureRegion>>> animations = new EnumMap<>(AnimationState.class);
    private final List<Texture> textures = new ArrayList<>();
    private final SheetLayout layout;

    public AnimationManager(HeroType type) {
        this(getHeroSheetPath(type), type == HeroType.INVINCIBLE ? SheetLayout.INVINCIBLE : SheetLayout.AUTO);
    }

    public AnimationManager(String sheetPath) {
        this(sheetPath, getSheetLayout(sheetPath));
    }

    private AnimationManager(String sheetPath, SheetLayout layout) {
        this.layout = layout;

        for (AnimationState state : AnimationState.values()) {
            animations.put(state, new EnumMap<>(Direction.class));
        }

        FileHandle file = Gdx.files.internal(sheetPath);
        Pixmap pixmap = new Pixmap(file);
        loadFrames(pixmap);
        pixmap.dispose();
        ensureFallbackAnimations();
    }

    public TextureRegion getFrame(AnimationState state, Direction direction, int frameIndex) {
        return getFrame(state, direction, frameIndex * FRAME_DURATION);
    }

    public TextureRegion getFrame(AnimationState state, Direction direction, float stateTime) {
        Animation<TextureRegion> animation = getAnimation(state, direction);
        return animation.getKeyFrame(stateTime);
    }

    public void dispose() {
        for (Texture texture : textures) {
            texture.dispose();
        }
    }

    private static String getHeroSheetPath(HeroType type) {
        if (type == HeroType.INVINCIBLE) {
            return "characters/mark_sheet.png";
        }

        if (type == HeroType.OMNI_MAN) {
            return "characters/omniman_sheet.png";
        }

        return "characters/techno_jacket_sheet.png";
    }

    private static SheetLayout getSheetLayout(String sheetPath) {
        if (sheetPath.contains("thragg_idle")) {
            return SheetLayout.THRAGG_IDLE;
        }

        return sheetPath.contains("conquest_sheet") ? SheetLayout.CONQUEST : SheetLayout.AUTO;
    }

    private void loadFrames(Pixmap pixmap) {
        if (layout == SheetLayout.INVINCIBLE) {
            loadInvincibleSheet(pixmap);
        } else if (layout == SheetLayout.CONQUEST) {
            loadConquestSheet(pixmap);
        } else if (layout == SheetLayout.THRAGG_IDLE) {
            loadThraggIdleSheet(pixmap);
        } else if (pixmap.getWidth() > pixmap.getHeight()) {
            loadWideSheet(pixmap);
        } else {
            loadSquareSheet(pixmap);
        }
    }

    private void loadInvincibleSheet(Pixmap pixmap) {
        int quarter = pixmap.getWidth() / 4;
        addSection(pixmap, AnimationState.IDLE, Direction.DOWN, 0, 65, quarter, 295, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.LEFT, quarter, 65, quarter, 295, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.RIGHT, quarter * 2, 65, quarter, 295, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.UP, quarter * 3, 65, pixmap.getWidth() - quarter * 3, 295, 3);

        int half = pixmap.getWidth() / 2;
        addSection(pixmap, AnimationState.ATTACK, Direction.DOWN, 0, 410, half, 300, 4);
        addSection(pixmap, AnimationState.ATTACK, Direction.LEFT, half, 410, pixmap.getWidth() - half, 300, 4);
        addSection(pixmap, AnimationState.ATTACK, Direction.RIGHT, 0, 735, half, 285, 4);
        addSection(pixmap, AnimationState.ATTACK, Direction.UP, half, 735, pixmap.getWidth() - half, 285, 4);
    }

    private void loadWideSheet(Pixmap pixmap) {
        int quarter = pixmap.getWidth() / 4;
        addSection(pixmap, AnimationState.IDLE, Direction.DOWN, 0, 55, quarter, 305, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.LEFT, quarter, 55, quarter, 305, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.RIGHT, quarter * 2, 55, quarter, 305, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.UP, quarter * 3, 55, pixmap.getWidth() - quarter * 3, 305, 3);

        int half = pixmap.getWidth() / 2;
        addSection(pixmap, AnimationState.ATTACK, Direction.DOWN, 0, 400, half, 285, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.LEFT, half, 400, pixmap.getWidth() - half, 285, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.RIGHT, 0, 715, half, pixmap.getHeight() - 715, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.UP, half, 715, pixmap.getWidth() - half, pixmap.getHeight() - 715, 5);
    }

    private void loadSquareSheet(Pixmap pixmap) {
        int quarter = pixmap.getWidth() / 4;
        addSection(pixmap, AnimationState.IDLE, Direction.DOWN, 0, 75, quarter, 315, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.LEFT, quarter, 75, quarter, 315, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.RIGHT, quarter * 2, 75, quarter, 315, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.UP, quarter * 3, 75, pixmap.getWidth() - quarter * 3, 315, 3);

        int half = pixmap.getWidth() / 2;
        addSection(pixmap, AnimationState.ATTACK, Direction.DOWN, 0, 480, half, 300, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.LEFT, half, 480, pixmap.getWidth() - half, 300, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.RIGHT, 0, 830, half, pixmap.getHeight() - 830, 5);
        addSection(pixmap, AnimationState.ATTACK, Direction.UP, half, 830, pixmap.getWidth() - half, pixmap.getHeight() - 830, 5);
    }

    private void loadConquestSheet(Pixmap pixmap) {
        int quarter = pixmap.getWidth() / 4;
        addSection(pixmap, AnimationState.IDLE, Direction.DOWN, 0, 70, quarter, 285, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.LEFT, quarter, 70, quarter, 285, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.RIGHT, quarter * 2, 70, quarter, 285, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.UP, quarter * 3, 70, pixmap.getWidth() - quarter * 3, 285, 3);

        int half = pixmap.getWidth() / 2;
        addSection(pixmap, AnimationState.ATTACK, Direction.DOWN, 0, 420, half, 290, 3);
        addSection(pixmap, AnimationState.ATTACK, Direction.LEFT, half, 420, pixmap.getWidth() - half, 290, 3);
        addSection(pixmap, AnimationState.ATTACK, Direction.RIGHT, 0, 785, half, 320, 3);
        addSection(pixmap, AnimationState.ATTACK, Direction.UP, half, 785, pixmap.getWidth() - half, 320, 3);
    }

    private void loadThraggIdleSheet(Pixmap pixmap) {
        int quarter = pixmap.getHeight() / 4;
        addSection(pixmap, AnimationState.IDLE, Direction.DOWN, 0, 0, pixmap.getWidth(), quarter, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.LEFT, 0, quarter, pixmap.getWidth(), quarter, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.RIGHT, 0, quarter * 2, pixmap.getWidth(), quarter, 3);
        addSection(pixmap, AnimationState.IDLE, Direction.UP, 0, quarter * 3, pixmap.getWidth(), pixmap.getHeight() - quarter * 3, 3);
    }

    private Animation<TextureRegion> getAnimation(AnimationState state, Direction direction) {
        Map<Direction, Animation<TextureRegion>> byDirection = animations.get(state);
        Animation<TextureRegion> animation = byDirection == null ? null : byDirection.get(direction);

        if (animation != null) {
            return animation;
        }

        animation = animations.get(AnimationState.IDLE).get(direction);

        if (animation != null) {
            return animation;
        }

        return animations.get(AnimationState.IDLE).get(Direction.DOWN);
    }

    private void ensureFallbackAnimations() {
        for (Direction direction : Direction.values()) {
            copyMissing(AnimationState.WALK, direction, AnimationState.IDLE);
            copyMissing(AnimationState.HIT, direction, AnimationState.ATTACK);
            copyMissing(AnimationState.HIT, direction, AnimationState.IDLE);
            copyMissing(AnimationState.DEATH, direction, AnimationState.HIT);
            copyMissing(AnimationState.DEATH, direction, AnimationState.IDLE);
        }
    }

    private void copyMissing(AnimationState target, Direction direction, AnimationState fallback) {
        Map<Direction, Animation<TextureRegion>> targetAnimations = animations.get(target);

        if (targetAnimations.get(direction) != null) {
            return;
        }

        Animation<TextureRegion> fallbackAnimation = animations.get(fallback).get(direction);

        if (fallbackAnimation != null) {
            targetAnimations.put(direction, fallbackAnimation);
        }
    }

    private void addSection(
        Pixmap pixmap,
        AnimationState state,
        Direction direction,
        int x,
        int y,
        int width,
        int height,
        int frameCount
    ) {
        List<TextureRegion> result = new ArrayList<>();
        int frameWidth = width / frameCount;

        for (int i = 0; i < frameCount; i++) {
            int frameX = x + i * frameWidth;
            int currentFrameWidth = i == frameCount - 1 ? x + width - frameX : frameWidth;
            TextureRegion frame = cropFrame(pixmap, frameX, y, currentFrameWidth, height);

            if (frame != null) {
                result.add(frame);
            }
        }

        if (result.isEmpty()) {
            Texture fallback = createFrameTexture(pixmap, x, y, width, height, new boolean[height][width]);
            textures.add(fallback);
            result.add(new TextureRegion(fallback));
        }

        Animation<TextureRegion> animation = new Animation<>(FRAME_DURATION, new Array<>(result.toArray(new TextureRegion[0])));
        animation.setPlayMode(isLooping(state) ? Animation.PlayMode.LOOP : Animation.PlayMode.NORMAL);
        animations.get(state).put(direction, animation);
    }

    private boolean isLooping(AnimationState state) {
        return state == AnimationState.IDLE || state == AnimationState.WALK;
    }

    private TextureRegion cropFrame(Pixmap pixmap, int x, int y, int width, int height) {
        int cellX = Math.max(0, x);
        int cellY = Math.max(0, y);
        int cellWidth = Math.min(width, pixmap.getWidth() - cellX);
        int cellHeight = Math.min(height, pixmap.getHeight() - cellY);
        boolean[][] background = findConnectedBackground(pixmap, cellX, cellY, cellWidth, cellHeight);

        int minX = cellWidth;
        int minY = cellHeight;
        int maxX = 0;
        int maxY = 0;
        int contentPixels = 0;

        for (int localY = 0; localY < cellHeight; localY++) {
            for (int localX = 0; localX < cellWidth; localX++) {
                if (!background[localY][localX]) {
                    minX = Math.min(minX, localX);
                    minY = Math.min(minY, localY);
                    maxX = Math.max(maxX, localX);
                    maxY = Math.max(maxY, localY);
                    contentPixels++;
                }
            }
        }

        if (contentPixels < 80) {
            return null;
        }

        int margin = 8;
        minX = Math.max(0, minX - margin);
        minY = Math.max(0, minY - margin);
        maxX = Math.min(cellWidth - 1, maxX + margin);
        maxY = Math.min(cellHeight - 1, maxY + margin);

        int croppedWidth = maxX - minX + 1;
        int croppedHeight = maxY - minY + 1;
        boolean[][] croppedBackground = new boolean[croppedHeight][croppedWidth];

        for (int localY = 0; localY < croppedHeight; localY++) {
            for (int localX = 0; localX < croppedWidth; localX++) {
                croppedBackground[localY][localX] = background[minY + localY][minX + localX];
            }
        }

        Texture texture = createFrameTexture(pixmap, cellX + minX, cellY + minY, croppedWidth, croppedHeight, croppedBackground);
        textures.add(texture);
        return new TextureRegion(texture);
    }

    private boolean[][] findConnectedBackground(Pixmap pixmap, int x, int y, int width, int height) {
        boolean[][] background = new boolean[height][width];
        ArrayDeque<int[]> queue = new ArrayDeque<>();

        for (int localX = 0; localX < width; localX++) {
            enqueueBackgroundPixel(pixmap, x, y, localX, 0, background, queue);
            enqueueBackgroundPixel(pixmap, x, y, localX, height - 1, background, queue);
        }

        for (int localY = 0; localY < height; localY++) {
            enqueueBackgroundPixel(pixmap, x, y, 0, localY, background, queue);
            enqueueBackgroundPixel(pixmap, x, y, width - 1, localY, background, queue);
        }

        int[] offsets = {1, 0, -1, 0, 0, 1, 0, -1};

        while (!queue.isEmpty()) {
            int[] pixel = queue.removeFirst();

            for (int i = 0; i < offsets.length; i += 2) {
                int nextX = pixel[0] + offsets[i];
                int nextY = pixel[1] + offsets[i + 1];

                if (nextX < 0 || nextX >= width || nextY < 0 || nextY >= height || background[nextY][nextX]) {
                    continue;
                }

                enqueueBackgroundPixel(pixmap, x, y, nextX, nextY, background, queue);
            }
        }

        return background;
    }

    private void enqueueBackgroundPixel(
        Pixmap pixmap,
        int cellX,
        int cellY,
        int localX,
        int localY,
        boolean[][] background,
        ArrayDeque<int[]> queue
    ) {
        if (!isEdgeBackground(pixmap.getPixel(cellX + localX, cellY + localY))) {
            return;
        }

        background[localY][localX] = true;
        queue.addLast(new int[] {localX, localY});
    }

    private Texture createFrameTexture(Pixmap source, int x, int y, int width, int height, boolean[][] background) {
        Pixmap frame = new Pixmap(width, height, Pixmap.Format.RGBA8888);
        frame.setBlending(Pixmap.Blending.None);

        for (int localY = 0; localY < height; localY++) {
            for (int localX = 0; localX < width; localX++) {
                if (background[localY][localX]) {
                    frame.drawPixel(localX, localY, 0x00000000);
                } else {
                    frame.drawPixel(localX, localY, source.getPixel(x + localX, y + localY) | 0x000000ff);
                }
            }
        }

        Texture texture = new Texture(frame);
        frame.dispose();
        return texture;
    }

    private boolean isEdgeBackground(int color) {
        int red = color >>> 24 & 0xff;
        int green = color >>> 16 & 0xff;
        int blue = color >>> 8 & 0xff;
        int max = Math.max(red, Math.max(green, blue));
        int min = Math.min(red, Math.min(green, blue));
        int brightness = (red + green + blue) / 3;

        if (brightness < 32) {
            return true;
        }

        return brightness > 205 && max - min < 34;
    }

    private enum SheetLayout {
        AUTO,
        INVINCIBLE,
        CONQUEST,
        THRAGG_IDLE
    }
}
