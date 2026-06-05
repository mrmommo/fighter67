package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;

public class CrateComponent extends Component {

    private LocalTimer lifeTimer;
    private boolean gravitySet = false;

    @Override
    public void onAdded() {
        lifeTimer = newLocalTimer();
        lifeTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // Giảm tốc độ rơi ngay khi body Box2D đã sẵn sàng
        if (!gravitySet) {
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            if (physics.getBody() != null) {
                physics.getBody().setGravityScale(0.3f);
                gravitySet = true;
            }
        }

        // Nếu đã tồn tại quá 15 giây mà không ai nhặt thì tự hủy
        if (lifeTimer.elapsed(Duration.seconds(15))) {
            entity.removeFromWorld();
        }
    }

    public static void spawnPickupEffect(double worldX, double worldY, String weaponName) {
        Text pickupText = new Text("⚔ " + weaponName + "!");
        pickupText.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD, 20));
        pickupText.setFill(Color.CYAN);
        pickupText.setStroke(Color.DARKBLUE);
        pickupText.setStrokeWidth(1.5);

        var viewport = com.almasb.fxgl.dsl.FXGL.getGameScene().getViewport();
        double zoom = viewport.getZoom();
        double screenX = (worldX - viewport.getX()) * zoom;
        double screenY = (worldY - viewport.getY()) * zoom;

        pickupText.setTranslateX(screenX - 30);
        pickupText.setTranslateY(screenY);

        com.almasb.fxgl.dsl.FXGL.getGameScene().addUINode(pickupText);

        TranslateTransition moveUp = new TranslateTransition(Duration.seconds(0.8), pickupText);
        moveUp.setByY(-55);

        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), pickupText);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> com.almasb.fxgl.dsl.FXGL.getGameScene().removeUINode(pickupText));

        moveUp.play();
        fadeOut.play();
    }
}
