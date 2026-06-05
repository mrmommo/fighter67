package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.scene.SubScene;
import com.nhom67.platformfighter.core.WinnerInfo;

public class GameOverScene extends SubScene {

    public GameOverScene(WinnerInfo winner) {
        // Phát nhạc kết thúc tương ứng (không lặp lại)
        if (winner == WinnerInfo.BOT) {
            com.nhom67.platformfighter.util.SoundManager.playMusicOnce("defeat.mp3");
        } else {
            com.nhom67.platformfighter.util.SoundManager.playMusicOnce("victory.mp3");
        }

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/assets/ui/game_over.fxml"));
            loader.setController(new GameOverController(winner));
            javafx.scene.Parent root = loader.load();
            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
