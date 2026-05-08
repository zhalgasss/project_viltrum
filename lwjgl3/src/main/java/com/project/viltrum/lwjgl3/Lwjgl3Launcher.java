package com.project.viltrum.lwjgl3;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;
import com.project.viltrum.Main;

public class Lwjgl3Launcher {

    public static void main(String[] args) {
        createApplication();
    }

    private static Lwjgl3Application createApplication() {
        return new Lwjgl3Application(new Main(), getDefaultConfiguration());
    }

    private static Lwjgl3ApplicationConfiguration getDefaultConfiguration() {
        Lwjgl3ApplicationConfiguration config =
                new Lwjgl3ApplicationConfiguration();

        config.setTitle("Invincible: Fall of Earth");

        // НОРМАЛЬНЫЙ РАЗМЕР ОКНА
        config.setWindowedMode(1600, 900);

        // FPS
        config.setForegroundFPS(60);

        // VSYNC
        config.useVsync(true);

        return config;
    }
}