package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.texture.AnimatedTexture;
import com.almasb.fxgl.texture.AnimationChannel;
import javafx.util.Duration;
import static com.almasb.fxgl.dsl.FXGL.*;
import javafx.scene.image.Image;

public class AnimationComponent extends Component {

    private AnimatedTexture texture;
    private AnimationChannel animIdle;
    private AnimationChannel animRun;
    private AnimationChannel animJump;

    private String prefix;
    private PlayerComponent player;
    private PhysicsComponent physics;

    public AnimationComponent(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public void onAdded() {
        // Load sprite sheets
        Image imgIdle = image(prefix + "_idle.png");
        Image imgRun = image(prefix + "_run.png");
        Image imgJump = image(prefix + "_jump.png");

        // Set up animation channels
        animIdle = new AnimationChannel(imgIdle, 6, 64, 64, Duration.seconds(0.6), 0, 5);
        animRun = new AnimationChannel(imgRun, 6, 64, 64, Duration.seconds(0.6), 0, 5);
        animJump = new AnimationChannel(imgJump, 2, 64, 64, Duration.seconds(0.3), 0, 1);

        // Initialize AnimatedTexture
        texture = new AnimatedTexture(animIdle);
        texture.loop();

        // Add to entity view
        entity.getViewComponent().addChild(texture);
    }

    @Override
    public void onUpdate(double tpf) {
        if (player == null) {
            player = entity.getComponent(PlayerComponent.class);
        }
        if (physics == null) {
            physics = entity.getComponent(PhysicsComponent.class);
        }

        AnimationChannel nextChannel;

        boolean onGround = physics.isOnGround();
        double vx = physics.getVelocityX();

        if (!onGround) {
            nextChannel = animJump;
        } else if (Math.abs(vx) > 10) {
            nextChannel = animRun;
        } else {
            nextChannel = animIdle;
        }

        if (texture.getAnimationChannel() != nextChannel) {
            texture.loopNoOverride(nextChannel);
        }

        if (player != null) {
            texture.setScaleX(player.getFacingDirection());
        }

        texture.onUpdate(tpf);
    }
}
