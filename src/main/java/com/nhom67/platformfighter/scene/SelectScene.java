package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.input.UserAction;
import com.almasb.fxgl.scene.SubScene;
import javafx.scene.input.KeyCode;

import static com.almasb.fxgl.dsl.FXGL.*;

public class SelectScene extends SubScene {

    public static SelectScene instance;
    private SelectController controller;

    public SelectScene() {
        instance = this;

        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/assets/ui/select_scene.fxml"));
            controller = new SelectController(this);
            loader.setController(controller);
            javafx.scene.Parent root = loader.load();
            getContentRoot().getChildren().add(root);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lắng nghe phím ESC để quay lại Menu
        getInput().addAction(new UserAction("Back") {
            @Override
            protected void onActionBegin() {
                instance = null;
                getSceneService().popSubScene();
            }
        }, KeyCode.ESCAPE);
    }

    public void syncWithMenu(int index) {
        if (controller != null) {
            controller.syncWithMenu(index);
        }
    }
}
