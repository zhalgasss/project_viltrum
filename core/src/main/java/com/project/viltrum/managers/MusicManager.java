package com.project.viltrum.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();

    private final Map<String, Sound> sounds = new HashMap<>();
    private Music currentMusic;
    private String currentMusicPath;

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public void playMenuMusic() {
        playMusic("audio/music/menu.ogg", 0.45f);
    }

    public void playGameMusic(boolean bossFight) {
        playMusic(bossFight ? "audio/music/boss.ogg" : "audio/music/combat.ogg", bossFight ? 0.5f : 0.38f);
    }

    public void playSound(String id) {
        Sound sound = sounds.get(id);

        if (sound == null && !sounds.containsKey(id)) {
            sound = loadSound(soundPath(id));
            sounds.put(id, sound);
        }

        if (sound != null) {
            sound.play(0.75f);
        }
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }

        currentMusicPath = null;
    }

    public void dispose() {
        if (currentMusic != null) {
            currentMusic.dispose();
            currentMusic = null;
        }

        currentMusicPath = null;

        for (Sound sound : sounds.values()) {
            if (sound != null) {
                sound.dispose();
            }
        }

        sounds.clear();
    }

    private void playMusic(String path, float volume) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            return;
        }

        if (path.equals(currentMusicPath) && currentMusic != null && currentMusic.isPlaying()) {
            return;
        }

        if (currentMusic != null) {
            currentMusic.stop();
            currentMusic.dispose();
        }

        currentMusicPath = path;
        currentMusic = Gdx.audio.newMusic(file);
        currentMusic.setLooping(true);
        currentMusic.setVolume(volume);
        currentMusic.play();
    }

    private Sound loadSound(String path) {
        FileHandle file = Gdx.files.internal(path);
        return file.exists() ? Gdx.audio.newSound(file) : null;
    }

    private String soundPath(String id) {
        if ("hit".equals(id)) return "audio/sfx/hit.wav";
        if ("dash".equals(id)) return "audio/sfx/dash.wav";
        if ("enemy_death".equals(id)) return "audio/sfx/enemy_death.wav";
        if ("boss_roar".equals(id)) return "audio/sfx/boss_roar.wav";
        if ("menu_select".equals(id)) return "audio/sfx/menu_select.wav";
        return "audio/sfx/" + id + ".wav";
    }
}
