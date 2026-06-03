package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.time.LocalTimer;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;

public class CrateComponent extends Component {
    
    private LocalTimer lifeTimer;

    @Override
    public void onAdded() {
        lifeTimer = newLocalTimer();
        lifeTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // Nếu đã tồn tại quá 15 giây mà không ai nhặt thì tự hủy
        if (lifeTimer.elapsed(Duration.seconds(15))) {
            entity.removeFromWorld();
        }
    }
}
