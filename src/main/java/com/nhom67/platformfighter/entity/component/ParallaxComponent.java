package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.app.scene.Viewport;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.component.Component;

public class ParallaxComponent extends Component {
    private double ratioX;
    private double ratioY;
    private double autoScrollSpeedX;
    private double bgWidth;
    
    private double startX;
    private double startY;
    private double autoScrollX = 0;

    public ParallaxComponent(double ratioX, double ratioY, double autoScrollSpeedX, double bgWidth) {
        this.ratioX = ratioX;
        this.ratioY = ratioY;
        this.autoScrollSpeedX = autoScrollSpeedX;
        this.bgWidth = bgWidth;
    }

    @Override
    public void onAdded() {
        this.startX = entity.getX();
        this.startY = entity.getY();
    }

    @Override
    public void onUpdate(double tpf) {
        autoScrollX += autoScrollSpeedX * tpf;
        
        Viewport viewport = FXGL.getGameScene().getViewport();
        double camX = viewport.getX();
        double camY = viewport.getY();
        
        // Vị trí ngang với lặp vô tận (đảm bảo ảnh gốc đủ che phủ)
        double absoluteX = startX + (camX * ratioX) + autoScrollX;
        double relX = (absoluteX - camX) % bgWidth;
        if (relX > 0) {
            relX -= bgWidth;
        }
        double newX = camX + relX;
        
        // Chiều dọc di chuyển nhẹ theo camera để tạo chiều sâu, không dùng scale để tránh bị tách khe hở
        double newY = startY + (camY * ratioX * 0.5);
        
        entity.setX(newX);
        entity.setY(newY);
    }
}
