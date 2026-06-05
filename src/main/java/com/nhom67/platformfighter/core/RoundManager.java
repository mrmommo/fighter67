package com.nhom67.platformfighter.core;

import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import com.nhom67.platformfighter.app.FighterApp;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.scene.GameOverScene;
import static com.almasb.fxgl.dsl.FXGL.*;
import java.util.Random;

public class RoundManager {
    
    private boolean isGameOver = false;
    private double crateSpawnTimer = 0;
    private Random random = new Random();

    public void checkWinCondition(PlayerComponent p1, PlayerComponent p2) {
        if (isGameOver) return;

        if (p1.isDead()) {
            isGameOver = true;
            WinnerInfo winner = FighterApp.gameMode == GameMode.VS_BOT
                    ? WinnerInfo.BOT
                    : WinnerInfo.PLAYER_2;
            showGameOver(winner);
        } else if (p2.isDead()) {
            isGameOver = true;
            showGameOver(WinnerInfo.PLAYER_1);
        }
    }

    public void onUpdate(double tpf) {
        if (isGameOver) return;

        crateSpawnTimer += tpf;
        if (crateSpawnTimer > 10.0) { // Spawn crate every 10 seconds
            crateSpawnTimer = 0;
            spawnCrate();
        }
    }

    private void spawnCrate() {
        double spawnX = 100 + random.nextDouble() * (getAppWidth() - 200);
        spawn("crate", new com.almasb.fxgl.entity.SpawnData(spawnX, -50)); // Rơi từ trên trời
    }

    private void showGameOver(WinnerInfo winner) {
        SubScene gameOverScene = new GameOverScene(winner);
        getSceneService().pushSubScene(gameOverScene);
    }
}
