package com.project.viltrum.events;

public class GameEvent {
    public final GameEventType type;
    public final float x;
    public final float y;
    public final float width;
    public final float height;
    public final float value;
    public final String message;

    public GameEvent(GameEventType type, float x, float y, float width, float height, float value, String message) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.value = value;
        this.message = message;
    }

    public static GameEvent point(GameEventType type, float x, float y) {
        return new GameEvent(type, x, y, 0, 0, 0, null);
    }

    public static GameEvent damage(GameEventType type, float x, float y, float value) {
        return new GameEvent(type, x, y, 0, 0, value, null);
    }

    public static GameEvent area(GameEventType type, float x, float y, float width, float height) {
        return new GameEvent(type, x, y, width, height, 0, null);
    }
}
