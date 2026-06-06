package com.nhom67.platformfighter.util;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import static com.almasb.fxgl.dsl.FXGL.*;

public class SoundManager {

    private static volatile double sfxVolume = 0.5;
    private static volatile double musicVolume = 0.5;
    private static volatile Music currentMusic;

    public static void init() {
        FXGL.getSettings().setGlobalMusicVolume(musicVolume);
        FXGL.getSettings().setGlobalSoundVolume(sfxVolume);
    }

    // Phát âm thanh click cơ bản
    public static void playClickSound() {
        playSound("click.wav"); // Tên file âm thanh có sẵn trong thư mục sfx
    }

    // Phát một hiệu ứng âm thanh (SFX)
    public static void playSound(String fileName) {
        try {
            Sound sound = getAssetLoader().loadSound(fileName); // FXGL tự động tìm trong assets/sounds/
            getAudioPlayer().playSound(sound);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy âm thanh: " + fileName);
        }
    }

    private static volatile String currentMusicName = "";

    // Phát nhạc nền (lặp lại)
    public static void playMusic(String fileName) {
        if (currentMusic != null && currentMusicName.equals(fileName)) {
            return; // Nếu đang phát bài này thì bỏ qua (không khởi động lại)
        }
        stopMusic();
        try {
            currentMusic = getAssetLoader().loadMusic(fileName);
            currentMusicName = fileName;
            getAudioPlayer().loopMusic(currentMusic);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy nhạc nền: " + fileName);
        }
    }

    // Phát nhạc một lần duy nhất (không lặp)
    public static void playMusicOnce(String fileName) {
        stopMusic();
        try {
            currentMusic = getAssetLoader().loadMusic(fileName);
            currentMusicName = fileName;
            getAudioPlayer().playMusic(currentMusic);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy nhạc: " + fileName);
        }
    }

    public static void stopMusic() {
        if (currentMusic != null) {
            try {
                getAudioPlayer().stopMusic(currentMusic);
                currentMusic = null;
                currentMusicName = "";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
        FXGL.getSettings().setGlobalSoundVolume(volume);
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        FXGL.getSettings().setGlobalMusicVolume(volume);
    }

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }
}
