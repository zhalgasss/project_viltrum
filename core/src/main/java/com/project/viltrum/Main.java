package com.project.viltrum;

import com.badlogic.gdx.Game;
import com.project.viltrum.managers.MusicManager;
import com.project.viltrum.screens.MenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }

    @Override
    public void dispose() {
        super.dispose();
        MusicManager.getInstance().dispose();
    }
}
