package com.project.viltrum.managers;

public class GameManager {
    private static GameManager instance;

    private int currentRoom = 1;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) {
            instance = new GameManager();
        }
        return instance;
    }

    public int getCurrentRoom() {
        return currentRoom;
    }

    public void nextRoom() {
        currentRoom++;
    }

    public void reset() {
        currentRoom = 1;
    }
}
