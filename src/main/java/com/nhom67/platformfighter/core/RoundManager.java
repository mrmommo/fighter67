package com.nhom67.platformfighter.core;

import com.almasb.fxgl.scene.Scene;
import com.almasb.fxgl.scene.SubScene;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.scene.GameOverScene;
import static com.almasb.fxgl.dsl.FXGL.*;

public class RoundManager {
    
    private boolean isGameOver = false;

    public void checkWinCondition(PlayerComponent p1, PlayerComponent p2) {
        if (isGameOver) return;

        if (p1.isDead()) {
            isGameOver = true;
            showGameOver(WinnerInfo.PLAYER_2);
        } else if (p2.isDead()) {
            isGameOver = true;
            showGameOver(WinnerInfo.PLAYER_1);
        }
    }

    private void showGameOver(WinnerInfo winner) {
        SubScene gameOverScene = new GameOverScene(winner);
        getSceneService().pushSubScene(gameOverScene);
    }
}
