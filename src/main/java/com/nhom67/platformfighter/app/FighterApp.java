package com.nhom67.platformfighter.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.nhom67.platformfighter.scene.GameOverScene;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

import java.lang.ModuleLayer.Controller;

import com.nhom67.platformfighter.controllers.Controllers;
import com.nhom67.platformfighter.entity.EntityFactory;
import com.nhom67.platformfighter.map.MapLoader;
import com.nhom67.platformfighter.map.MapRegistry;

public class FighterApp extends GameApplication {
    private Entity player;
    private Entity player2;
    Controllers controller = new Controllers();

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("67-Fighter");
        settings.setVersion("1.0");

        // Bật Main Menu
        settings.setMainMenuEnabled(true);
        // Cài đặt Scene Factory tùy chỉnh của chúng ta
        settings.setSceneFactory(new AppSceneFactory());
    }

    @Override
    protected void initUI() {
        // Tạo một nút "Game End" hiển thị trên màn hình chơi (HUD)
        Button btnGameEnd = getUIFactoryService().newButton("Game End");
        btnGameEnd.setFocusTraversable(false);
        btnGameEnd.setTranslateX(1920 - 200); // Góc trên bên phải
        btnGameEnd.setTranslateY(50);

        // Khi nhấn nút, hiển thị màn hình GameOver
        btnGameEnd.setOnAction(e -> {
            getSceneService().pushSubScene(new GameOverScene());
        });
        // Thêm nút vào giao diện HUD của GameScene
        addUINode(btnGameEnd);
    }

    // Map được chọn từ màn hình SelectScene
    public static MapRegistry selectedMap = MapRegistry.MAP_1;

    @Override
    protected void initGame() {
        // Đăng ký EntityFactory
        getGameWorld().addEntityFactory(new EntityFactory());
        // Load map từ MapRegistry
        MapLoader.loadMap(selectedMap);
        //
        player = spawn("player", new SpawnData(700, 300).put("color", Color.GREEN));
        player2 = spawn("player", new SpawnData(500, 300).put("color", Color.BLUE));

        controller.setPlayers(player, player2);

        set("player", player);
        set("player2", player2);

    }

    @Override
    protected void initInput() {
        controller.initInput();
    }

    @Override
    protected void initPhysics() {
        controller.initPhysics();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
