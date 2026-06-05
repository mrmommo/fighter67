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
import com.nhom67.platformfighter.core.CameraController;  // ✅ THÊM
import com.nhom67.platformfighter.entity.EntityFactory;
import com.nhom67.platformfighter.map.MapLoader;
import com.nhom67.platformfighter.map.MapRegistry;
import com.nhom67.platformfighter.scene.ui.GameHUD;
import com.nhom67.platformfighter.core.RoundManager;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.core.WinnerInfo;

public class FighterApp extends GameApplication {
    private Entity player;
    private Entity player2;
    Controllers controller = new Controllers();
    private GameHUD gameHUD;
    private RoundManager roundManager;
    private CameraController cameraController;  // ✅ THÊM

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("67-Fighter");
        settings.setVersion("1.0");
        settings.setAppIcon("ui/icon.png");
        // Bật Main Menu
        settings.setMainMenuEnabled(true);
        // Cài đặt Scene Factory tùy chỉnh của chúng ta
        settings.setSceneFactory(new AppSceneFactory());
    }

    @Override
    protected void initUI() {
        gameHUD = new GameHUD();
        PlayerComponent p1Comp = player.getComponent(PlayerComponent.class);
        PlayerComponent p2Comp = player2.getComponent(PlayerComponent.class);
        
        gameHUD.initHUD(p1Comp, p2Comp);
        roundManager = new RoundManager();
    }
    
    @Override
    public void onUpdate(double tpf) {
        if (gameHUD != null) {
            gameHUD.onUpdate(tpf);
        }
        if (roundManager != null && player != null && player2 != null) {
            PlayerComponent p1Comp = player.getComponent(PlayerComponent.class);
            PlayerComponent p2Comp = player2.getComponent(PlayerComponent.class);
            roundManager.checkWinCondition(p1Comp, p2Comp);
            roundManager.onUpdate(tpf);
        }
        
        // ✅ THÊM: Update camera smooth follow
        if (cameraController != null) {
            cameraController.update(tpf);
        }
    }

    // Map được chọn từ màn hình SelectScene
    public static MapRegistry selectedMap = MapRegistry.MAP_1;

    @Override
    protected void initGame() {
        // Đăng ký EntityFactory
        getGameWorld().addEntityFactory(new EntityFactory());
        
        // ✅ THAY: Load map và lấy dimensions
        MapLoader.MapDimensions mapDims = MapLoader.loadMap(selectedMap);
        
        //
        player = spawn("player", new SpawnData(700, 300).put("color", Color.GREEN));
        player2 = spawn("player", new SpawnData(500, 300).put("color", Color.BLUE));

        controller.setPlayers(player, player2);

        set("player", player);
        set("player2", player2);
        
        // ✅ THÊM: Khởi tạo camera controller với map dimensions thật
        cameraController = new CameraController(player, player2, mapDims.width, mapDims.height);
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
