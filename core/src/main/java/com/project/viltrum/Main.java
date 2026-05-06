package com.project.viltrum;

import com.badlogic.gdx.Game;
import com.project.viltrum.screens.MenuScreen;

public class Main extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
