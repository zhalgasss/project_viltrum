package com.projectviltrum.entities;

public class Player extends Entity {

    public Player(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        System.out.println("Player updating...");
    }
}