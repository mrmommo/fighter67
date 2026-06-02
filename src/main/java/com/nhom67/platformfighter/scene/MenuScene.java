package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import static com.almasb.fxgl.dsl.FXGL.*;

public class MenuScene extends FXGLMenu {

    public MenuScene(MenuType type) {
        super(type);

        // Nút Play
        Button btnPlay = getUIFactoryService().newButton("Play");
        
        btnPlay.setOnAction(e -> {
            // Khi nhấn Play, đẩy màn hình Select lên trên cùng
            getSceneService().pushSubScene(new SelectScene());
        });

        // Canh giữa màn hình
        VBox box = new VBox(btnPlay);
        box.setTranslateX(1920 / 2.0 - 50);
        box.setTranslateY(1080 / 2.0);

        getContentRoot().getChildren().add(box);
    }
}
