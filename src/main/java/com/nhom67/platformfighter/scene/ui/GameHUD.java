package com.nhom67.platformfighter.scene.ui;

import com.almasb.fxgl.app.GameApplication;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class GameHUD {
    private HealthBarWidget p1HealthBar;
    private HealthBarWidget p2HealthBar;

    private PlayerComponent p1;
    private PlayerComponent p2;

    private StackPane p1AmmoHUD;
    private Text p1AmmoText;
    private StackPane p2AmmoHUD;
    private Text p2AmmoText;

    public void initHUD(PlayerComponent p1, PlayerComponent p2) {
        // Player 1 HUD (Top Left)
        p1HealthBar = new HealthBarWidget(p1, true, "P1");
        p1HealthBar.setTranslateX(20);
        p1HealthBar.setTranslateY(20);
        
        // Player 2 HUD (Top Right)
        p2HealthBar = new HealthBarWidget(p2, false, "P2");
        p2HealthBar.setTranslateX(getAppWidth() - 220); // 200 width + 20 margin
        p2HealthBar.setTranslateY(20);

        addUINode(p1HealthBar);
        addUINode(p2HealthBar);

        this.p1 = p1;
        this.p2 = p2;

        // Player 1 Ammo HUD (World-space)
        p1AmmoText = new Text();
        p1AmmoText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        p1AmmoText.setFill(Color.BLACK);
        Rectangle bg1 = new Rectangle(30, 15, Color.WHITE);
        bg1.setStroke(Color.BLACK);
        bg1.setStrokeWidth(1);
        p1AmmoHUD = new StackPane(bg1, p1AmmoText);
        p1AmmoHUD.setTranslateX(10);
        p1AmmoHUD.setTranslateY(-20);
        p1.getEntity().getViewComponent().addChild(p1AmmoHUD);

        // Player 2 Ammo HUD (World-space)
        p2AmmoText = new Text();
        p2AmmoText.setFont(Font.font("Arial", FontWeight.BOLD, 12));
        p2AmmoText.setFill(Color.BLACK);
        Rectangle bg2 = new Rectangle(30, 15, Color.WHITE);
        bg2.setStroke(Color.BLACK);
        bg2.setStrokeWidth(1);
        p2AmmoHUD = new StackPane(bg2, p2AmmoText);
        p2AmmoHUD.setTranslateX(10);
        p2AmmoHUD.setTranslateY(-20);
        p2.getEntity().getViewComponent().addChild(p2AmmoHUD);
    }

    public void onUpdate(double tpf) {
        if (p1HealthBar != null) p1HealthBar.update(tpf);
        if (p2HealthBar != null) p2HealthBar.update(tpf);

        if (p1 != null && p1AmmoHUD != null) {
            p1AmmoText.setText(p1.isReloading() ? "0" : String.valueOf(p1.getCurrentAmmo()));
            p1AmmoHUD.setScaleX(p1.getFacingDirection() == -1 ? -1 : 1);
        }

        if (p2 != null && p2AmmoHUD != null) {
            p2AmmoText.setText(p2.isReloading() ? "0" : String.valueOf(p2.getCurrentAmmo()));
            p2AmmoHUD.setScaleX(p2.getFacingDirection() == -1 ? -1 : 1);
        }
    }
}
