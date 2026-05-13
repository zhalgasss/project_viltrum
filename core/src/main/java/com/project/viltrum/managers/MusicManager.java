package com.project.viltrum.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.files.FileHandle;

import java.util.HashMap;
import java.util.Map;

public class MusicManager {
    private static final MusicManager INSTANCE = new MusicManager();
    private static final float MUSIC_FADE_IN_DURATION = 0.65f;

    private final Map<String, Sound> sounds = new HashMap<>();
    private Music currentMusic;
    private String currentMusicPath;
    private float currentMusicVolume = 0;
    private float fadeStartVolume = 0;
    private float fadeTargetVolume = 0;
    private float fadeTimer = 0;
    private float fadeDuration = 0;
    private boolean stopAfterFade = false;
    private boolean hasPendingMusic = false;
    private String pendingMusicPath;
    private float pendingMusicVolume;
    private boolean pendingMusicLooping;
    private float pendingMusicStartPosition;

    private MusicManager() {
    }

    public static MusicManager getInstance() {
        return INSTANCE;
    }

    public void playMenuMusic() {
        if (Gdx.files.internal("audio/music/menu.ogg").exists()) {
            playMusic("audio/music/menu.ogg", 0.45f);
        } else {
            playMusic("audio/sfx/menu_select.wav.mp3", 0.45f);
        }
    }

    public void playGameMusic(boolean bossFight) {
        playMusic(bossFight ? "audio/music/boss.ogg" : "audio/music/combat.ogg", bossFight ? 0.5f : 0.38f);
    }

    public void playGameOverMusic() {
        playMusic("audio/sfx/game_over.wav.mp3", 0.65f, false, 10f);
    }

    public void playVictoryMusic() {
        playMusic("audio/sfx/victory_chorus.mp3", 0.7f, false);
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

    public void update(float delta) {
        if (currentMusic == null || fadeDuration <= 0) {
            return;
        }

        fadeTimer += delta;
        float progress = Math.min(1f, fadeTimer / fadeDuration);
        currentMusicVolume = fadeStartVolume + (fadeTargetVolume - fadeStartVolume) * progress;
        currentMusic.setVolume(currentMusicVolume);

        if (progress < 1f) {
            return;
        }

        fadeDuration = 0;
        currentMusicVolume = fadeTargetVolume;
        currentMusic.setVolume(currentMusicVolume);

        if (stopAfterFade) {
            currentMusic.stop();
            currentMusic.dispose();
            currentMusic = null;
            currentMusicPath = null;
            currentMusicVolume = 0;
            stopAfterFade = false;

            if (hasPendingMusic) {
                String path = pendingMusicPath;
                float volume = pendingMusicVolume;
                boolean looping = pendingMusicLooping;
                float startPosition = pendingMusicStartPosition;
                clearPendingMusic();
                playMusic(path, volume, looping, startPosition);
            }
        }
    }

    public void fadeOut(float duration) {
        clearPendingMusic();
        fadeOut(duration, false);
    }

    private void fadeOut(float duration, boolean keepPendingMusic) {
        if (currentMusic == null) {
            return;
        }

        if (!keepPendingMusic) {
            clearPendingMusic();
        }

        fadeStartVolume = currentMusicVolume;
        fadeTargetVolume = 0;
        fadeTimer = 0;
        fadeDuration = Math.max(0.01f, duration);
        stopAfterFade = true;
    }

    public void stopMusic() {
        if (currentMusic != null) {
            currentMusic.stop();
        }

        currentMusicPath = null;
        currentMusicVolume = 0;
        fadeDuration = 0;
        stopAfterFade = false;
        clearPendingMusic();
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
        playMusic(path, volume, true);
    }

    private void playMusic(String path, float volume, boolean looping) {
        playMusic(path, volume, looping, 0);
    }

    private void playMusic(String path, float volume, boolean looping, float startPosition) {
        FileHandle file = Gdx.files.internal(path);

        if (!file.exists()) {
            return;
        }

        if (path.equals(currentMusicPath) && currentMusic != null && currentMusic.isPlaying()) {
            fadeTo(volume, MUSIC_FADE_IN_DURATION);
            return;
        }

        if (currentMusic != null) {
            pendingMusicPath = path;
            pendingMusicVolume = volume;
            pendingMusicLooping = looping;
            pendingMusicStartPosition = startPosition;
            hasPendingMusic = true;
            fadeOut(MUSIC_FADE_IN_DURATION, true);
            return;
        }

        currentMusicPath = path;
        currentMusic = Gdx.audio.newMusic(file);
        currentMusic.setLooping(looping);
        currentMusic.setVolume(0);
        currentMusic.play();

        if (startPosition > 0) {
            currentMusic.setPosition(startPosition);
        }

        currentMusicVolume = 0;
        fadeTo(volume, MUSIC_FADE_IN_DURATION);
    }

    private void fadeTo(float volume, float duration) {
        fadeStartVolume = currentMusicVolume;
        fadeTargetVolume = volume;
        fadeTimer = 0;
        fadeDuration = Math.max(0.01f, duration);
        stopAfterFade = false;
    }

    private void clearPendingMusic() {
        hasPendingMusic = false;
        pendingMusicPath = null;
        pendingMusicVolume = 0;
        pendingMusicLooping = false;
        pendingMusicStartPosition = 0;
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
