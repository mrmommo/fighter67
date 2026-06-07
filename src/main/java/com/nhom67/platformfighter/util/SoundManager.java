package com.nhom67.platformfighter.util;

import com.almasb.fxgl.audio.Sound;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import static com.almasb.fxgl.dsl.FXGL.*;

public class SoundManager {

    private static volatile double sfxVolume = 0.5;
    private static volatile double musicVolume = 0.5;
    private static MediaPlayer currentMusicPlayer; // ⭐ Dùng MediaPlayer thay Music
    private static String currentMusicPath = "";

    public static void init() {
        FXGL.getSettings().setGlobalMusicVolume(musicVolume);
        FXGL.getSettings().setGlobalSoundVolume(sfxVolume);
    }

    public static void playClickSound() {
        playSound("click.wav");
    }

    public static void playSound(String fileName) {
        try {
            Sound sound = getAssetLoader().loadSound(fileName);
            getAudioPlayer().playSound(sound);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy âm thanh: " + fileName);
        }
    }

    // ⭐ Phát nhạc bằng MediaPlayer (tránh được bug FXGL)
    public static void playMusic(String fileName) {
        if (currentMusicPath.equals(fileName) && currentMusicPlayer != null
                && currentMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
            return;
        }

        stopMusic();

        try {
            String resourcePath = SoundManager.class.getResource("/assets/music/" + fileName).toExternalForm();
            Media media = new Media(resourcePath);
            currentMusicPlayer = new MediaPlayer(media);
            currentMusicPlayer.setCycleCount(MediaPlayer.INDEFINITE);
            currentMusicPlayer.setVolume(musicVolume);
            currentMusicPlayer.play();
            currentMusicPath = fileName;
            System.out.println("[SoundManager] Playing music: " + fileName);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy nhạc nền: " + fileName);
            e.printStackTrace();
        }
    }

    public static void playMusicOnce(String fileName) {
        stopMusic();

        try {
            String resourcePath = SoundManager.class.getResource("/assets/music/" + fileName).toExternalForm();
            Media media = new Media(resourcePath);
            currentMusicPlayer = new MediaPlayer(media);
            currentMusicPlayer.setCycleCount(1);
            currentMusicPlayer.setVolume(musicVolume);
            currentMusicPlayer.play();
            currentMusicPath = fileName;
            System.out.println("[SoundManager] Playing music once: " + fileName);
        } catch (Exception e) {
            System.err.println("SoundManager: Không tìm thấy nhạc: " + fileName);
            e.printStackTrace();
        }
    }

    // ⭐ Stop hoàn toàn và xóa reference
    public static void stopMusic() {
        try {
            if (currentMusicPlayer != null) {
                currentMusicPlayer.stop();
                currentMusicPlayer.dispose(); // ⭐ Giải phóng tài nguyên
                currentMusicPlayer = null;
            }
            currentMusicPath = "";
            System.out.println("[SoundManager] Music stopped");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⭐ Pause nhạc
    public static void pauseMusic() {
        try {
            if (currentMusicPlayer != null && currentMusicPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                currentMusicPlayer.pause();
                System.out.println("[SoundManager] Music paused");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ⭐ Resume nhạc
    public static void resumeMusic() {
        try {
            if (currentMusicPlayer != null && currentMusicPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                currentMusicPlayer.play();
                System.out.println("[SoundManager] Music resumed");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setSfxVolume(double volume) {
        sfxVolume = volume;
        FXGL.getSettings().setGlobalSoundVolume(volume);
    }

    public static void setMusicVolume(double volume) {
        musicVolume = volume;
        FXGL.getSettings().setGlobalMusicVolume(volume);
        if (currentMusicPlayer != null) {
            currentMusicPlayer.setVolume(volume);
        }
    }

    public static double getMusicVolume() {
        return musicVolume;
    }

    public static double getSfxVolume() {
        return sfxVolume;
    }
}