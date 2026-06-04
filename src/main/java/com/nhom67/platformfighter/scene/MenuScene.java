package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.paint.CycleMethod;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.animation.ScaleTransition;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.animation.ScaleTransition;
import javafx.scene.text.Text;
import javafx.util.Duration;

import com.nhom67.platformfighter.util.SoundManager;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MenuScene extends FXGLMenu {

    public static MenuScene instance;
    public static boolean isStartingGame = false;

    private final String[] bgPaths = {
            "maps/map1background.png",
            "maps/map2background.png"
    };

    private int currentBgIndex = 0;
    private Pane bgPane;
    private ImageView currentBgView;
    private ImageView nextBgView;
    private boolean isAnimating = false;

    // Overlay dùng cho các bảng phụ (Settings, Credits,...)
    private StackPane overlayContainer;
    
    // UI Boxes
    private VBox btnBox;
    private VBox settingsBox;
    private VBox howToPlayBox;
    private Timeline autoSlide;

    // Layer chứa các hạt đom đóm bay lượn
    private Pane fireflyLayer;
    private Timeline fireflySpawner;

    // Kích thước màn hình cố định
    private final int WIDTH = 1920;
    private final int HEIGHT = 1080;

    public MenuScene() {
        super(MenuType.MAIN_MENU);
        instance = this;

        // 1. Tạo phần nền (Backgrounds)
        bgPane = new Pane();
        currentBgView = createBgView(bgPaths[0]);
        nextBgView = createBgView(bgPaths[1]); // Pre-load ảnh tiếp theo

        // Đặt nextBg ở ngoài màn hình
        nextBgView.setTranslateX(WIDTH);

        bgPane.getChildren().addAll(currentBgView, nextBgView);

        // 2. Tạo giao diện UI (Title & Buttons)
        VBox uiBox = createUI();

        // 3. Khởi tạo Overlay Container (Mặc định ẩn)
        overlayContainer = new StackPane();
        overlayContainer.setVisible(false);
        overlayContainer.setPrefSize(WIDTH, HEIGHT);

        // 4. Khởi tạo Firefly Layer (Hiệu ứng đom đóm)
        fireflyLayer = new Pane();
        fireflyLayer.setPrefSize(WIDTH, HEIGHT);
        fireflyLayer.setMouseTransparent(true); // Không cản trở click chuột
        startFireflyEffect();

        // 5. Xử lý Input trượt hình nền
        registerInput();

        // 6. Tự động chuyển hình nền mỗi 7 giây (Ngay cả khi mở Settings)
        autoSlide = new Timeline(new KeyFrame(Duration.seconds(7), e -> {
            if (!isAnimating) {
                slideBackground(1);
            }
        }));
        autoSlide.setCycleCount(Timeline.INDEFINITE);
        autoSlide.play();

        // Thêm tất cả vào root (layer theo thứ tự: Nền -> Fireflies -> UI)
        getContentRoot().getChildren().addAll(bgPane, fireflyLayer, uiBox, overlayContainer);

        // Phát nhạc nền lần đầu
        SoundManager.playMusic("menu_track_01.mp3");
    }

    @Override
    public void onEnteredFrom(com.almasb.fxgl.scene.Scene prevState) {
        super.onEnteredFrom(prevState);
        if (isStartingGame) {
            isStartingGame = false; // Reset cờ
            return; // Đang vào Game, không phát lại nhạc
        }
        // Phát lại nhạc nền mỗi khi quay trở về Menu từ màn chơi khác
        SoundManager.playMusic("menu_track_01.mp3");
    }

    private ImageView createBgView(String path) {
        ImageView view = new ImageView();
        try {
            Image img = image(path);
            view.setImage(img);
        } catch (Exception e) {
            System.err.println("Cannot load background image: " + path);
        }
        view.setFitWidth(WIDTH);
        view.setFitHeight(HEIGHT);
        return view;
    }

    private VBox createUI() {
        // Overlay LinearGradient từ Đen mờ sang Trong suốt để làm nổi chữ bên trái
        Stop[] stops = new Stop[] {
            new Stop(0, Color.color(0, 0, 0, 0.8)),
            new Stop(0.5, Color.color(0, 0, 0, 0.4)),
            new Stop(1, Color.TRANSPARENT)
        };
        LinearGradient gradient = new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE, stops);
        Rectangle overlay = new Rectangle(WIDTH, HEIGHT, gradient);

        Text title = new Text("FIGHTER\n67"); // Xuống dòng cho hoành tráng
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 90));
        title.setFill(Color.web("#a8e6cf")); // Màu xanh lá cây sáng
        title.setTextAlignment(javafx.scene.text.TextAlignment.CENTER); // Căn giữa 2 dòng chữ
        DropShadow titleShadow = new DropShadow(20, Color.BLACK);
        title.setEffect(titleShadow);

        // Hiệu ứng "nhịp thở" cho tiêu đề
        TranslateTransition tt = new TranslateTransition(Duration.seconds(2), title);
        tt.setByY(-15);
        tt.setAutoReverse(true);
        tt.setCycleCount(TranslateTransition.INDEFINITE);
        tt.setInterpolator(Interpolator.EASE_BOTH);
        tt.play();

        btnBox = new VBox(22,
                createNatureButton("▶ PLAY GAME", () -> getSceneService().pushSubScene(new SelectScene())),
                createNatureButton("⚙ SETTINGS", () -> showSettingsOverlay()),
                createNatureButton("📖 HOW TO PLAY", () -> showHowToPlayOverlay()),
                createNatureButton("🌿 CREDITS", () -> System.out.println("Credits opened")), // TODO
                createNatureButton("✕ QUIT", () -> getGameController().exit()));
        btnBox.setAlignment(Pos.CENTER_LEFT); // Canh lề trái

        // Khởi tạo Settings Box
        initSettingsBox();
        initHowToPlayBox();

        // Chứa cả btnBox, settingsBox, howToPlayBox để swap in-place
        StackPane menuContainer = new StackPane(btnBox, settingsBox, howToPlayBox);
        menuContainer.setAlignment(Pos.CENTER_LEFT);

        VBox uiBox = new VBox(50, title, menuContainer);
        uiBox.setAlignment(Pos.CENTER_LEFT);
        uiBox.setPadding(new Insets(0, 0, 0, 150)); // Thụt từ lề trái 150px
        uiBox.setPrefSize(WIDTH, HEIGHT);

        // Tạo layer UI chứa overlay và nội dung
        StackPane uiLayer = new StackPane(overlay, uiBox);
        return new VBox(uiLayer); // Wrap để tương thích
    }

    private Group createNatureButton(String text, Runnable action) {
        Group btn = new Group();

        // Nền nút hình bình hành vát chéo
        Polygon bg = new Polygon(
            20.0, 0.0,
            320.0, 0.0,
            300.0, 60.0,
            0.0, 60.0
        );
        bg.setFill(Color.web("#1e3315", 0.75)); 
        bg.setStroke(Color.web("#a8e6cf", 0.6)); 
        bg.setStrokeWidth(2);

        // Thanh nhấn vát chéo
        Polygon accent = new Polygon(
            20.0, 0.0,
            26.0, 0.0,
            6.0, 60.0,
            0.0, 60.0
        );
        accent.setFill(Color.web("#a8e6cf"));

        Text textNode = new Text(text);
        textNode.setFont(Font.font("Arial", FontWeight.BOLD, 22)); // Arial hiện đại hơn
        textNode.setFill(Color.web("#e8f5d0"));
        // Đặt text ở vị trí tuyệt đối trong Group
        textNode.setX(60);
        textNode.setY(38); // Baseline Y

        btn.getChildren().addAll(bg, accent, textNode);
        
        DropShadow shadow = new DropShadow(15, Color.web("#a8e6cf", 0.8));
        
        // Animation trượt mượt mà
        TranslateTransition hoverMove = new TranslateTransition(Duration.seconds(0.15), btn);
        
        btn.setOnMouseEntered(e -> {
            bg.setFill(Color.web("#2d4a22", 0.95));
            bg.setStroke(Color.web("#a8e6cf", 1.0));
            textNode.setFill(Color.WHITE);
            btn.setEffect(shadow);
            
            hoverMove.setToX(20); // Dịch nhẹ sang phải
            hoverMove.play();
        });

        btn.setOnMouseExited(e -> {
            bg.setFill(Color.web("#1e3315", 0.75));
            bg.setStroke(Color.web("#a8e6cf", 0.6));
            textNode.setFill(Color.web("#e8f5d0"));
            btn.setEffect(null);
            
            hoverMove.setToX(0);
            hoverMove.play();
        });

        btn.setOnMousePressed(e -> bg.setFill(Color.web("#0a1207")));
        
        btn.setOnMouseClicked(e -> {
            SoundManager.playClickSound();
            action.run();
        });

        return btn;
    }

    // --- HIỆU ỨNG THIÊN NHIÊN (FIREFLIES) ---

    private void startFireflyEffect() {
        fireflySpawner = new Timeline(new KeyFrame(Duration.seconds(0.3), e -> spawnFirefly()));
        fireflySpawner.setCycleCount(Timeline.INDEFINITE);
        fireflySpawner.play();
    }

    private void spawnFirefly() {
        // Kích thước ngẫu nhiên từ 1.5 đến 3.5
        double radius = com.almasb.fxgl.core.math.FXGLMath.random(1.5, 3.5);
        Circle firefly = new Circle(radius, Color.web("#a8e6cf")); // Xanh lá sáng

        // Vị trí xuất phát ở dưới cùng màn hình, X ngẫu nhiên
        firefly.setTranslateX(com.almasb.fxgl.core.math.FXGLMath.random(0, WIDTH));
        firefly.setTranslateY(HEIGHT + 10);

        // Phát sáng
        DropShadow glow = new DropShadow(15, Color.web("#a8e6cf"));
        firefly.setEffect(glow);

        fireflyLayer.getChildren().add(firefly);

        // Mục tiêu bay đến (bay lên trên và hơi lệch trái/phải)
        double targetX = firefly.getTranslateX() + com.almasb.fxgl.core.math.FXGLMath.random(-200, 200);
        double targetY = com.almasb.fxgl.core.math.FXGLMath.random(-50, HEIGHT * 0.7);
        double durationSecs = com.almasb.fxgl.core.math.FXGLMath.random(6.0, 12.0);

        TranslateTransition move = new TranslateTransition(Duration.seconds(durationSecs), firefly);
        move.setToX(targetX);
        move.setToY(targetY);
        move.setInterpolator(Interpolator.EASE_OUT);

        // Hiệu ứng chớp nháy (Fade)
        FadeTransition fade = new FadeTransition(Duration.seconds(durationSecs / 2), firefly);
        fade.setFromValue(0.1);
        fade.setToValue(0.8);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        move.setOnFinished(e -> fireflyLayer.getChildren().remove(firefly));

        move.play();
        fade.play();
    }

    // --- CÁC OVERLAY ---

    private void initSettingsBox() {
        settingsBox = new VBox(30);
        settingsBox.setAlignment(Pos.CENTER_LEFT);
        settingsBox.setVisible(false);
        settingsBox.setOpacity(0);

        Text title = new Text("SETTINGS");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        title.setFill(Color.web("#a8e6cf"));

        Label musicLabel = new Label("Music Volume");
        musicLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        musicLabel.setTextFill(Color.WHITE);

        Slider musicSlider = new Slider(0, 1, SoundManager.getMusicVolume());
        musicSlider.setMaxWidth(300);
        musicSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setMusicVolume(newVal.doubleValue());
        });

        Label sfxLabel = new Label("SFX Volume");
        sfxLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        sfxLabel.setTextFill(Color.WHITE);

        Slider sfxSlider = new Slider(0, 1, SoundManager.getSfxVolume());
        sfxSlider.setMaxWidth(300);
        sfxSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
            SoundManager.setSfxVolume(newVal.doubleValue());
        });

        Group closeBtn = createNatureButton("◄ BACK", () -> hideSettingsOverlay());

        settingsBox.getChildren().addAll(title, musicLabel, musicSlider, sfxLabel, sfxSlider, closeBtn);
    }

    private void showSettingsOverlay() {
        FadeTransition fadeOutBtn = new FadeTransition(Duration.seconds(0.2), btnBox);
        fadeOutBtn.setToValue(0);
        fadeOutBtn.setOnFinished(e -> btnBox.setVisible(false));
        fadeOutBtn.play();

        settingsBox.setVisible(true);
        FadeTransition fadeInSettings = new FadeTransition(Duration.seconds(0.2), settingsBox);
        fadeInSettings.setToValue(1);
        fadeInSettings.play();
    }

    private void hideSettingsOverlay() {
        FadeTransition fadeOutSettings = new FadeTransition(Duration.seconds(0.2), settingsBox);
        fadeOutSettings.setToValue(0);
        fadeOutSettings.setOnFinished(e -> settingsBox.setVisible(false));
        fadeOutSettings.play();

        btnBox.setVisible(true);
        FadeTransition fadeInBtn = new FadeTransition(Duration.seconds(0.2), btnBox);
        fadeInBtn.setToValue(1);
        fadeInBtn.play();
    }

    private void initHowToPlayBox() {
        howToPlayBox = new VBox(20);
        howToPlayBox.setAlignment(Pos.CENTER_LEFT);
        howToPlayBox.setVisible(false);
        howToPlayBox.setOpacity(0);

        Text title = new Text("HOW TO PLAY");
        title.setFont(Font.font("Georgia", FontWeight.BOLD, 40));
        title.setFill(Color.web("#a8e6cf"));

        howToPlayBox.getChildren().addAll(
            title,
            createKeyRow("W A S D", "Di chuyển & Nhảy"),
            createKeyRow("J", "Đánh thường (Light Attack)"),
            createKeyRow("K", "Đánh mạnh (Heavy Attack)"),
            createKeyRow("L", "Lướt / Kỹ năng (Dash)"),
            createNatureButton("◄ BACK", () -> hideHowToPlayOverlay())
        );
    }

    private javafx.scene.layout.HBox createKeyRow(String keys, String desc) {
        Text keyText = new Text(keys);
        keyText.setFont(Font.font("Courier New", FontWeight.BOLD, 26));
        keyText.setFill(Color.web("#feca57"));
        
        Text descText = new Text(" - " + desc);
        descText.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        descText.setFill(Color.WHITE);
        
        javafx.scene.layout.HBox box = new javafx.scene.layout.HBox(10, keyText, descText);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private void showHowToPlayOverlay() {
        FadeTransition fadeOutBtn = new FadeTransition(Duration.seconds(0.2), btnBox);
        fadeOutBtn.setToValue(0);
        fadeOutBtn.setOnFinished(e -> btnBox.setVisible(false));
        fadeOutBtn.play();

        howToPlayBox.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), howToPlayBox);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    private void hideHowToPlayOverlay() {
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.2), howToPlayBox);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> howToPlayBox.setVisible(false));
        fadeOut.play();

        btnBox.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.2), btnBox);
        fadeIn.setToValue(1);
        fadeIn.play();
    }

    // -------------------

    private void registerInput() {
        getContentRoot().setOnKeyPressed(e -> {
            // Khóa trượt hình nền nếu overlay đang hiển thị
            if (overlayContainer.isVisible())
                return;
            if (isAnimating)
                return;

            if (e.getCode() == KeyCode.D) {
                if (autoSlide != null) autoSlide.playFromStart();
                slideBackground(1); // Sang phải
            } else if (e.getCode() == KeyCode.A) {
                if (autoSlide != null) autoSlide.playFromStart();
                slideBackground(-1); // Sang trái
            }
        });

        // Giữ focus để nhận phím
        getContentRoot().setFocusTraversable(true);
        getContentRoot().requestFocus();
        getContentRoot().setOnMouseClicked(e -> getContentRoot().requestFocus());
    }

    private void slideBackground(int direction) {
        isAnimating = true;

        int tempNextIndex = currentBgIndex + direction;
        if (tempNextIndex < 0)
            tempNextIndex = bgPaths.length - 1;
        if (tempNextIndex >= bgPaths.length)
            tempNextIndex = 0;
        final int nextIndex = tempNextIndex;

        // Cập nhật ảnh cho nextBgView
        try {
            nextBgView.setImage(image(bgPaths[nextIndex]));
        } catch (Exception e) {
            // ignore
        }

        // Đặt nextBgView đè lên cùng vị trí với currentBgView
        nextBgView.setTranslateX(0);
        nextBgView.setOpacity(0);

        // Tạo animation làm mờ (Fade)
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(0.8), currentBgView);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);

        FadeTransition fadeIn = new FadeTransition(Duration.seconds(0.8), nextBgView);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);

        fadeIn.setOnFinished(e -> {
            ImageView temp = currentBgView;
            currentBgView = nextBgView;
            nextBgView = temp;

            currentBgIndex = nextIndex;
            isAnimating = false;

            if (SelectScene.instance != null) {
                SelectScene.instance.syncWithMenu(currentBgIndex);
            }
        });

        fadeOut.play();
        fadeIn.play();
    }

    public void forceBackground(int index) {
        if (currentBgIndex == index) return;
        currentBgIndex = index;
        try {
            currentBgView.setImage(image(bgPaths[currentBgIndex]));
            // Đảm bảo nextBgView cũng được reset để không bị lỗi slide lần sau
            nextBgView.setOpacity(0);
        } catch (Exception e) {}

        if (autoSlide != null) {
            autoSlide.playFromStart();
        }
    }
}
