package com.nhom67.platformfighter.app;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.SpawnData;
import com.nhom67.platformfighter.util.SoundManager;
import javafx.scene.paint.Color;

import static com.almasb.fxgl.dsl.FXGL.*;

import com.nhom67.platformfighter.controllers.Controllers;
import com.nhom67.platformfighter.ai.BotComponent;
import com.nhom67.platformfighter.core.CameraController;
import com.nhom67.platformfighter.core.GameMode;
import com.nhom67.platformfighter.entity.EntityFactory;
import com.nhom67.platformfighter.map.MapLoader;
import com.nhom67.platformfighter.map.MapRegistry;
import com.nhom67.platformfighter.scene.ui.GameHUD;
import com.nhom67.platformfighter.core.RoundManager;
import com.nhom67.platformfighter.entity.component.PlayerComponent;

public class FighterApp extends GameApplication {
    private Entity player;
    private Entity player2;
    Controllers controller = new Controllers();
    private GameHUD gameHUD;
    private RoundManager roundManager;
    private CameraController cameraController;

    private boolean wasAudioStopped = false;

    @Override
    protected void initSettings(GameSettings settings) {
        settings.setWidth(1920);
        settings.setHeight(1080);
        settings.setTitle("67-Fighter");
        settings.setVersion("1.0");
        settings.setAppIcon("ui/icon.png");
        settings.setMainMenuEnabled(true);
        settings.setSceneFactory(new AppSceneFactory());
    }

    @Override
    protected void initUI() {
        gameHUD = new GameHUD();
        PlayerComponent p1Comp = player.getComponent(PlayerComponent.class);
        PlayerComponent p2Comp = player2.getComponent(PlayerComponent.class);

        String p2Label = gameMode == GameMode.VS_BOT ? "BOT" : "P2";
        gameHUD.initHUD(p1Comp, p2Comp, p2Label);
        roundManager = new RoundManager();

        // ⭐ Thêm xử lý window focus events
        setupWindowFocusListener();
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

        if (cameraController != null) {
            cameraController.update(tpf);
        }
    }

    // Map và chế độ chơi được chọn từ SelectScene
    public static MapRegistry selectedMap = MapRegistry.MAP_3;
    public static GameMode gameMode = GameMode.TWO_PLAYER;
    public static boolean isIngame = false;

    @Override
    protected void initGame() {
        isIngame = true;
        // Dừng các nhạc cũ (nhạc menu)
        SoundManager.stopMusic();

        // Đăng ký EntityFactory
        getGameWorld().addEntityFactory(new EntityFactory());

        // Load map và lấy dimensions
        MapLoader.MapDimensions mapDims = MapLoader.loadMap(selectedMap);

        // Phát nhạc cho Map
        if (selectedMap.getMusicTrack() != null && !selectedMap.getMusicTrack().isEmpty()) {
            SoundManager.playMusic(selectedMap.getMusicTrack());
        }

        player = spawn("player", new SpawnData(400, -100).put("color", Color.GREEN));
        player2 = spawn("player", new SpawnData(1520, -100).put("color", Color.BLUE));

        boolean vsBot = gameMode == GameMode.VS_BOT;
        if (vsBot) {
            BotComponent bot = new BotComponent();
            player2.addComponent(bot);
            bot.setTarget(player);
        }

        controller.setPlayers(player, player2, vsBot);

        set("player", player);
        set("player2", player2);

        // Khởi tạo camera controller với map dimensions thật
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

    // ⭐ Xử lý window focus/unfocus để tránh trồng nhạc
    private void setupWindowFocusListener() {
        try {
            javafx.stage.Window window = getGameScene().getRoot().getScene().getWindow();
            if (window == null)
                return;

            // Khi window mất focus (minimize, tab out)
            window.focusedProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal) {
                    // Window mất focus → pause nhạc
                    wasAudioStopped = true;
                    SoundManager.pauseMusic();
                    System.out.println("[FighterApp] Window lost focus - music paused");
                } else {
                    // Window được focus lại
                    if (wasAudioStopped && isIngame) {
                        // Chỉ resume nếu đang trong game
                        SoundManager.resumeMusic();
                        System.out.println("[FighterApp] Window gained focus - music resumed");
                        wasAudioStopped = false;
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("[FighterApp] Failed to setup window focus listener: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}