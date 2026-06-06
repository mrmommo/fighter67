package com.nhom67.platformfighter.app;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.SceneFactory;
import com.almasb.fxgl.app.scene.StartupScene;
import com.nhom67.platformfighter.scene.CustomStartupScene;
import com.nhom67.platformfighter.scene.MenuScene;

import static com.almasb.fxgl.dsl.FXGL.getAppHeight;
import static com.almasb.fxgl.dsl.FXGL.getAppWidth;

public class AppSceneFactory extends SceneFactory {

    @Override
    public StartupScene newStartup(int width, int height) {
        // Hiển thị màn hình Startup với icon.png trước khi vào menu chính
        return new CustomStartupScene(width, height);
    }

    @Override
    public FXGLMenu newMainMenu() {
        // Trả về custom menu của chúng ta
        return new MenuScene();
    }
}
