package com.projectviltrum.entities;

public abstract class Entity {

    protected float x;
    protected float y;
    protected int health;

    public Entity(float x, float y) {
        this.x = x;
        this.y = y;
        this.health = 100;
    }

    public abstract void update();
}