package com.projectviltrum.screens;

import com.projectviltrum.entities.Player;

public class GameScreen {

    private Player player;

    public GameScreen() {
        player = new Player(0, 0);
    }

    public void update() {
        player.update();
    }
}