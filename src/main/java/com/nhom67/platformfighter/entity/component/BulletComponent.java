package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import javafx.geometry.Point2D;

public class BulletComponent extends Component {
    
    private BulletData data;
    private Entity owner;
    private boolean facingRight;
    private double distanceTravelled = 0;
    private double timeAlive = 0;
    private Point2D direction;

    public BulletComponent(BulletData data, Entity owner, boolean facingRight, Point2D direction) {
        this.data = data;
        this.owner = owner;
        this.facingRight = facingRight;
        this.direction = direction;
    }

    @Override
    public void onUpdate(double tpf) {
        timeAlive += tpf;
        if (timeAlive >= data.lifespan()) {
            entity.removeFromWorld();
            return;
        }

        double moveDist = data.speed() * tpf;
        entity.translate(
                direction.getX() * moveDist,
                direction.getY() * moveDist
        );
        
        distanceTravelled += moveDist;
        // Xóa đạn nếu bay quá xa (tránh lag)
        if (distanceTravelled > 2000) {
            entity.removeFromWorld();
        }
    }

    public Entity getOwner() {
        return owner;
    }

    public BulletData getData() {
        return data;
    }

    public boolean isFacingRight() {
        return facingRight;
    }
}
