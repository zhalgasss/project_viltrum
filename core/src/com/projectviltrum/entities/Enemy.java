package com.projectviltrum.entities;

public class Enemy extends Entity {

    public Enemy(float x, float y) {
        super(x, y);
    }

    @Override
    public void update() {
        System.out.println("Enemy updating...");
    }
}