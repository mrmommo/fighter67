package com.nhom67.platformfighter.scene;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.scene.image.Image;
import javafx.scene.shape.Circle;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import com.nhom67.platformfighter.util.SoundManager;

import static com.almasb.fxgl.dsl.FXGL.*;

public class MenuScene extends FXGLMenu {

    public static MenuScene instance;
    public static boolean isStartingGame = false;

    private String[] bgPaths;

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
    private MenuController controller;

    public MenuScene() {
        super(MenuType.MAIN_MENU);
        instance = this;

        com.nhom67.platformfighter.map.MapRegistry[] maps = com.nhom67.platformfighter.map.MapRegistry.values();
        bgPaths = new String[maps.length];
        for (int i = 0; i < maps.length; i++) {
            bgPaths[i] = maps[i].getPreviewImagePath();
        }

        // 1. Tạo phần nền (Backgrounds)
        bgPane = new Pane();
        currentBgView = createBgView(bgPaths[0]);
        nextBgView = createBgView(bgPaths[bgPaths.length > 1 ? 1 : 0]); // Pre-load ảnh tiếp theo

        // Đặt nextBg ở ngoài màn hình
        nextBgView.setTranslateX(WIDTH);
        nextBgView.setOpacity(0);

        bgPane.getChildren().addAll(currentBgView, nextBgView);

        // 2. Tạo lớp Đom đóm (Fireflies)
        fireflyLayer = new Pane();
        fireflyLayer.setPrefSize(WIDTH, HEIGHT);
        fireflyLayer.setMouseTransparent(true);
        startFireflyEffect();

        // 3. Tự động trượt nền (Auto Slide)
        autoSlide = new Timeline(new KeyFrame(Duration.seconds(7), e -> {
            if (!isAnimating) {
                slideBackground(1);
            }
        }));
        autoSlide.setCycleCount(Timeline.INDEFINITE);
        autoSlide.play();

        // Load CSS stylesheet
        getContentRoot().getStylesheets().add(getClass().getResource("/assets/ui/styles.css").toExternalForm());

        // Thêm các layer Java vào trước
        getContentRoot().getChildren().addAll(bgPane, fireflyLayer);

        // 4. Load giao diện UI bằng FXML
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/assets/ui/menu_scene.fxml"));
            controller = new MenuController(this);
            loader.setController(controller);
            javafx.scene.Parent uiRoot = loader.load();
            getContentRoot().getChildren().add(uiRoot);

            // Áp dụng theme của map đầu tiên ngay khi UI sẵn sàng
            controller.applyMapTheme(maps[currentBgIndex].getAccentColor());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 5. Bắt sự kiện phím toàn cục cho nền
        getContentRoot().setOnKeyPressed(this::handleGlobalKeyPress);
        getContentRoot().setFocusTraversable(true);
        getContentRoot().requestFocus();
        getContentRoot().setOnMouseClicked(e -> getContentRoot().requestFocus());

        // Phát nhạc nền lần đầu
        SoundManager.playMusic("menu_track_01.mp3");
    }

    @Override
    public void onEnteredFrom(com.almasb.fxgl.scene.Scene prevState) {
        super.onEnteredFrom(prevState);
        if (isStartingGame) {
            isStartingGame = false;
            return;
        }
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

    public void handleGlobalKeyPress(javafx.scene.input.KeyEvent e) {
        if (controller != null && controller.isOverlayVisible())
            return;
        if (isAnimating)
            return;

        if (e.getCode() == KeyCode.D || e.getCode() == KeyCode.RIGHT) {
            if (autoSlide != null)
                autoSlide.playFromStart();
            slideBackground(1); // Sang phải
        } else if (e.getCode() == KeyCode.A || e.getCode() == KeyCode.LEFT) {
            if (autoSlide != null)
                autoSlide.playFromStart();
            slideBackground(-1); // Sang trái
        }
    }

    private void startFireflyEffect() {
        fireflySpawner = new Timeline(new KeyFrame(Duration.seconds(0.3), e -> spawnFirefly()));
        fireflySpawner.setCycleCount(Timeline.INDEFINITE);
        fireflySpawner.play();
    }

    private void spawnFirefly() {
        double radius = com.almasb.fxgl.core.math.FXGLMath.random(1.5, 3.5);
        Circle firefly = new Circle(radius, Color.web("#a8e6cf"));
        firefly.setTranslateX(com.almasb.fxgl.core.math.FXGLMath.random(0, WIDTH));
        firefly.setTranslateY(HEIGHT + 10);
        fireflyLayer.getChildren().add(firefly);

        double targetX = firefly.getTranslateX() + com.almasb.fxgl.core.math.FXGLMath.random(-200, 200);
        double targetY = com.almasb.fxgl.core.math.FXGLMath.random(-50, HEIGHT * 0.7);
        double durationSecs = com.almasb.fxgl.core.math.FXGLMath.random(6.0, 12.0);

        TranslateTransition move = new TranslateTransition(Duration.seconds(durationSecs), firefly);
        move.setToX(targetX);
        move.setToY(targetY);
        move.setInterpolator(Interpolator.EASE_OUT);

        FadeTransition fade = new FadeTransition(Duration.seconds(durationSecs / 2), firefly);
        fade.setFromValue(0.1);
        fade.setToValue(0.8);
        fade.setAutoReverse(true);
        fade.setCycleCount(2);

        move.setOnFinished(e -> fireflyLayer.getChildren().remove(firefly));
        move.play();
        fade.play();
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

        // Đồng bộ SelectScene ngay lập tức để cả hai cùng chuyển đổi hiệu ứng
        if (SelectScene.instance != null) {
            SelectScene.instance.syncWithMenu(nextIndex);
        }

        fadeIn.setOnFinished(e -> {
            ImageView temp = currentBgView;
            currentBgView = nextBgView;
            nextBgView = temp;

            currentBgIndex = nextIndex;
            isAnimating = false;

            // Đổi màu button theo accent color của map mới
            if (controller != null) {
                com.nhom67.platformfighter.map.MapRegistry[] maps =
                        com.nhom67.platformfighter.map.MapRegistry.values();
                controller.applyMapTheme(maps[currentBgIndex].getAccentColor());
            }
        });

        fadeOut.play();
        fadeIn.play();
    }

    // Cho phép SelectScene kích hoạt hiệu ứng slide chung
    public void slideFromSelectScene(int direction) {
        if (isAnimating)
            return;
        if (autoSlide != null)
            autoSlide.playFromStart();
        slideBackground(direction);
    }

    public void forceBackground(int index) {
        if (currentBgIndex == index)
            return;
        currentBgIndex = index;
        try {
            currentBgView.setImage(image(bgPaths[currentBgIndex]));
            // Đảm bảo nextBgView cũng được reset để không bị lỗi slide lần sau
            nextBgView.setOpacity(0);
        } catch (Exception e) {
        }

        if (autoSlide != null) {
            autoSlide.playFromStart();
        }
    }
}
