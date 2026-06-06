package com.nhom67.platformfighter.scene;

import javafx.fxml.FXML;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.animation.ScaleTransition;
import javafx.util.Duration;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import com.nhom67.platformfighter.core.WinnerInfo;
import com.nhom67.platformfighter.util.SoundManager;
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameOverController {

    @FXML private Text winnerText;
    @FXML private StackPane btnReturn;

    private final WinnerInfo winner;

    public GameOverController(WinnerInfo winner) {
        this.winner = winner;
    }

    @FXML
    public void initialize() {
        winnerText.setText("🏆 " + winner.getMessage());

        // Animations and Event Handlers remain in Java for tight control
        DropShadow shadow = new DropShadow(15, Color.web("#a8e6cf", 0.8));
        ScaleTransition hoverScale = new ScaleTransition(Duration.seconds(0.15), btnReturn);
        
        btnReturn.setOnMouseEntered(e -> {
            btnReturn.setEffect(shadow);
            hoverScale.setToX(1.05);
            hoverScale.setToY(1.05);
            hoverScale.play();
        });

        btnReturn.setOnMouseExited(e -> {
            btnReturn.setEffect(null);
            hoverScale.setToX(1.0);
            hoverScale.setToY(1.0);
            hoverScale.play();
        });
        
        btnReturn.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            getSceneService().popSubScene();
            getGameController().gotoMainMenu();
            getSceneService().pushSubScene(new SelectScene());
        });
    }
}
