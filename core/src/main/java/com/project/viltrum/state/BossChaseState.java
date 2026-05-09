package com.project.viltrum.state;

import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Player;

public class BossChaseState implements BossState {
    @Override
    public void update(Boss boss, Player player, float delta) {
        if (boss.getStats().hp <= boss.getStats().maxHp / 2) {
            boss.setState(new BossRageState());
            return;
        }

        float dx = player.getX() - boss.getHitbox().x;
        float dy = player.getY() - boss.getHitbox().y;
        float length = (float) Math.sqrt(dx * dx + dy * dy);

        if (length > 5) {
            boss.x += dx / length * boss.getStats().speed * delta;
            boss.y += dy / length * boss.getStats().speed * delta;
        }

        if (boss.getHitbox().overlaps(player.getHitbox())) {
            player.takeDamage(boss.getStats().damage * delta);
        }
    }
}
