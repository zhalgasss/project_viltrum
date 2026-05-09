package com.project.viltrum.entities;

public class CharacterStats {
    public float maxHp;
    public float hp;
    public float speed;
    public float damage;

    public CharacterStats(float maxHp, float speed, float damage) {
        this.maxHp = maxHp;
        this.hp = maxHp;
        this.speed = speed;
        this.damage = damage;
    }

    public void takeDamage(float amount) {
        hp -= amount;
        if (hp < 0) hp = 0;
    }

    public void heal(float amount) {
        hp += amount;
        if (hp > maxHp) hp = maxHp;
    }

    public boolean isDead() {
        return hp <= 0;
    }
}
