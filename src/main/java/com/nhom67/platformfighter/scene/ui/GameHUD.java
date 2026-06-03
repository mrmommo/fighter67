package com.nhom67.platformfighter.scene.ui;

import com.almasb.fxgl.app.GameApplication;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import static com.almasb.fxgl.dsl.FXGL.*;

public class GameHUD {
    
    private HealthBarWidget p1HealthBar;
    private HealthBarWidget p2HealthBar;

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
    }

    public void onUpdate(double tpf) {
        if (p1HealthBar != null) p1HealthBar.update(tpf);
        if (p2HealthBar != null) p2HealthBar.update(tpf);
    }
}
