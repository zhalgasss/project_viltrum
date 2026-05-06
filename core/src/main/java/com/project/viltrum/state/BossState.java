package com.project.viltrum.state;

import com.project.viltrum.entities.Boss;
import com.project.viltrum.entities.Player;

public interface BossState {
    void update(Boss boss, Player player, float delta);
}
