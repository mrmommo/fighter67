package com.nhom67.platformfighter.app;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.nhom67.platformfighter.scene.MenuScene;

public class AppSceneFactory extends SceneFactory {
    
    @Override
    public FXGLMenu newMainMenu() {
        // Trả về custom menu của chúng ta
        return new MenuScene();
    }
}
