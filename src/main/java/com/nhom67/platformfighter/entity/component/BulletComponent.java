package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;

public class BulletComponent extends Component {
    
    private BulletData data;
    private Entity owner;
    private boolean facingRight;
    private double distanceTravelled = 0;

    public BulletComponent(BulletData data, Entity owner, boolean facingRight) {
        this.data = data;
        this.owner = owner;
        this.facingRight = facingRight;
    }

    @Override
    public void onUpdate(double tpf) {
        double moveDist = data.speed() * tpf;
        if (facingRight) {
            entity.translateX(moveDist);
        } else {
            entity.translateX(-moveDist);
        }
        
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
