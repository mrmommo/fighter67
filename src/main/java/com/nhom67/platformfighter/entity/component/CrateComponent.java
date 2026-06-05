package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;

public class CrateComponent extends Component {
    
    private LocalTimer lifeTimer;
    private boolean gravitySet = false;

    @Override
    public void onAdded() {
        lifeTimer = newLocalTimer();
        lifeTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // Giảm tốc độ rơi ngay khi body Box2D đã sẵn sàng
        if (!gravitySet) {
            PhysicsComponent physics = entity.getComponent(PhysicsComponent.class);
            if (physics.getBody() != null) {
                physics.getBody().setGravityScale(0.3f);
                gravitySet = true;
            }
        }

        // Nếu đã tồn tại quá 15 giây mà không ai nhặt thì tự hủy
        if (lifeTimer.elapsed(Duration.seconds(15))) {
            entity.removeFromWorld();
        }
    }
}
