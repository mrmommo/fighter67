package com.nhom67.platformfighter.core;

import com.almasb.fxgl.entity.Entity;
import static com.almasb.fxgl.dsl.FXGL.*;

/**
 * Smooth camera controller - Gun Mayhem style with DYNAMIC ZOOM
 * Follows both players with smooth easing and zooms based on distance
 */
public class CameraController {
    private Entity player1;
    private Entity player2;

    // Camera position (Center of the screen)
    private double currentCameraX;
    private double currentCameraY;

    // Camera zoom
    private double currentZoom;

    // Smooth follow parameters (Tốc độ mượt, số càng lớn càng nhanh)
    private final double posSmoothSpeed = 8.0; 
    private final double zoomOutSpeed = 9.0; // Phóng to góc nhìn (zoom out) nhanh để không mất nhân vật khỏi màn hình
    private final double zoomInSpeed = 3.0;   // Thu hẹp góc nhìn (zoom in) chậm rãi để tránh chóng mặt

    // Tăng giới hạn zoom để cho phép phóng to khi gần và thu nhỏ khi xa
    private final double zoomMin = 1.0; // Zoom out khi xa nhau
    private final double zoomMax = 1.3; // Zoom in khi lại gần

    // Padding around players when calculating zoom
    private final double cameraPadX = 350;
    private final double cameraPadY = 250;

    // Map dimensions
    private double mapW;
    private double mapH;

    // Screen size
    private final double screenWidth = 1920;
    private final double screenHeight = 1080;

    public CameraController(Entity p1, Entity p2, double mapW, double mapH) {
        this.player1 = p1;
        this.player2 = p2;
        this.mapW = mapW;
        this.mapH = mapH;
        this.currentZoom = 1.0; // Initial zoom

        // Initialize camera position to center of players
        this.currentCameraX = (player1.getX() + player2.getX()) / 2;
        this.currentCameraY = (player1.getY() + player2.getY()) / 2;

        clampCamera();
        applyCamera();
    }

    public void update(double tpf) {
        if (player1 == null || player2 == null)
            return;
        if (!player1.isActive() || !player2.isActive())
            return; // Tránh lỗi khi player chết

        // 1. Calculate midpoint between 2 players
        double p1CenterX = player1.getX() + player1.getWidth() / 2;
        double p1CenterY = player1.getY() + player1.getHeight() / 2;

        double p2CenterX = player2.getX() + player2.getWidth() / 2;
        double p2CenterY = player2.getY() + player2.getHeight() / 2;

        double midX = (p1CenterX + p2CenterX) / 2;
        double midY = (p1CenterY + p2CenterY) / 2;

        // Thêm một chút trọng số dời camera xuống dưới để nhìn thấy mặt đất rõ hơn khi nhảy cao
        midY += 50; 

        // 2. Calculate span (distance between players + padding)
        double spanX = Math.abs(p1CenterX - p2CenterX) + cameraPadX * 2;
        double spanY = Math.abs(p1CenterY - p2CenterY) + cameraPadY * 2;

        // 3. Calculate target zoom to fit both players
        double targetZoom = Math.max(zoomMin, Math.min(zoomMax,
                Math.min(screenWidth / spanX, screenHeight / spanY)));

        // 4. Smooth camera position movement (Framerate Independent Lerp)
        double posLerp = 1.0 - Math.exp(-posSmoothSpeed * tpf);
        this.currentCameraX += (midX - currentCameraX) * posLerp;
        this.currentCameraY += (midY - currentCameraY) * posLerp;

        // 5. Smooth zoom animation (Asymmetric Lerp: Fast Out, Slow In)
        // Lưu ý: targetZoom nhỏ hơn currentZoom nghĩa là góc nhìn rộng ra (Zoom Out)
        double zoomSpeed = (targetZoom < currentZoom) ? zoomOutSpeed : zoomInSpeed;
        double zoomLerp = 1.0 - Math.exp(-zoomSpeed * tpf);
        this.currentZoom += (targetZoom - currentZoom) * zoomLerp;

        // 6. Clamp camera to stay within map bounds
        clampCamera();

        // 7. Apply camera and zoom to viewport
        applyCamera();
    }

    private void clampCamera() {
        // Calculate half-screen size in world coordinates with current zoom
        double halfWidth = (screenWidth / 2.0) / currentZoom;
        double halfHeight = (screenHeight / 2.0) / currentZoom;

        // Clamp X: camera center must be between halfWidth and (mapW - halfWidth)
        if (halfWidth * 2 >= mapW) {
            currentCameraX = mapW / 2.0; // Màn hình to hơn map -> căn giữa
        } else {
            currentCameraX = Math.max(halfWidth, Math.min(mapW - halfWidth, currentCameraX));
        }

        // Clamp Y: camera center must be between halfHeight and (mapH - halfHeight)
        if (halfHeight * 2 >= mapH) {
            currentCameraY = mapH / 2.0; // Màn hình to hơn map -> căn giữa
        } else {
            currentCameraY = Math.max(halfHeight, Math.min(mapH - halfHeight, currentCameraY));
        }
    }

    private void applyCamera() {
        // Apply zoom first
        getGameScene().getViewport().setZoom(currentZoom);

        // Then apply position. The Viewport X, Y represents the top-left corner in
        // world space.
        // We subtract the scaled half-width/height from our center to get the top-left.
        getGameScene().getViewport().setX(currentCameraX - (screenWidth / 2.0) / currentZoom);
        getGameScene().getViewport().setY(currentCameraY - (screenHeight / 2.0) / currentZoom);
    }

    // Getters for rendering or debugging
    public double getCameraX() {
        return currentCameraX;
    }

    public double getCameraY() {
        return currentCameraY;
    }

    public double getZoom() {
        return currentZoom;
    }
}