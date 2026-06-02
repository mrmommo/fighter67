package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import static com.almasb.fxgl.dsl.FXGL.*;

public class PlayerComponent extends Component {

    private PhysicsComponent physics;
    private int jumps = 2;

    private double currentSpeedX = 0;
    private double maxSpeed = 350;
    private double acceleration = 2500;
    private double friction = 2000;
    private int moveDirection = 0; // -1 left, 1 right, 0 stop

    private double fastFallAccel = 900;

    private LocalTimer leftTapTimer;
    private LocalTimer rightTapTimer;
    private LocalTimer dashCooldownTimer;
    private LocalTimer dropTimer;

    private double dashSpeed = 1200;

    public boolean isDropping = false;
    private boolean isDashing = false;

    @Override
    public void onAdded() {
        leftTapTimer = newLocalTimer();
        rightTapTimer = newLocalTimer();
        dashCooldownTimer = newLocalTimer();
        dropTimer = newLocalTimer();
        dashCooldownTimer.capture();
        leftTapTimer.capture();
        rightTapTimer.capture();
    }

    public void setMoveDirection(int dir) {
        this.moveDirection = dir;
        if (dir != 0) {
            getEntity().setScaleX(dir);
        }
    }

    public void leftPress() {
        if (dashCooldownTimer.elapsed(javafx.util.Duration.seconds(0.5))) {
            if (!leftTapTimer.elapsed(javafx.util.Duration.seconds(0.25))) {
                // Dash Left
                currentSpeedX = -dashSpeed;
                physics.setVelocityY(0);
                dashCooldownTimer.capture();
                isDashing = true;
            }
        }
        leftTapTimer.capture();
    }

    public void rightPress() {
        if (dashCooldownTimer.elapsed(javafx.util.Duration.seconds(0.5))) {
            if (!rightTapTimer.elapsed(javafx.util.Duration.seconds(0.25))) {
                // Dash Right
                currentSpeedX = dashSpeed;
                physics.setVelocityY(0);
                dashCooldownTimer.capture();
                isDashing = true;
            }
        }
        rightTapTimer.capture();
    }

    public void stop() {
        this.moveDirection = 0;
    }

    public void jump() {
        if (jumps == 0)
            return;
        physics.setVelocityY(-700); // Lực nhảy
        jumps--;
    }

    public void dropDown() {
        // Chỉ cho phép tụt xuống khi đang đứng trên mặt đất
        if (!isDropping && physics.isOnGround()) {
            isDropping = true;
            dropTimer.capture();
            // Đổi sang KINEMATIC để đi xuyên vật thể STATIC
            physics.setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.KINEMATIC);
            // Dịch chuyển nhẹ xuống dưới để vượt qua bề mặt platform
            getEntity().translateY(35);
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // Chỉ reset jumps nếu đang ở trên mặt đất VÀ không phải vừa mới nhảy lên
        if (physics.isOnGround() && physics.getVelocityY() > -100) {
            jumps = 2;
        } else {
            // Fast fall
            if (physics.getVelocityY() > 0 && !isDropping) {
                physics.setVelocityY(physics.getVelocityY() + fastFallAccel * tpf);
            }
        }

        if (isDropping) {
            // Ép vận tốc rơi cực nhanh để thoát khỏi platform dày
            physics.setVelocityY(800);

            // Tăng thời gian xuyên thấu lên 0.3s để đảm bảo rớt hẳn qua platform
            if (dropTimer.elapsed(javafx.util.Duration.seconds(0.3))) {
                isDropping = false;
                physics.setBodyType(com.almasb.fxgl.physics.box2d.dynamics.BodyType.DYNAMIC);
            }
        }

        if (isDashing && dashCooldownTimer.elapsed(javafx.util.Duration.seconds(0.2))) {
            isDashing = false;
        }

        // Acceleration / Inertia
        if (moveDirection != 0 && !isDashing) {
            currentSpeedX += moveDirection * acceleration * tpf;
            if (currentSpeedX > maxSpeed)
                currentSpeedX = maxSpeed;
            if (currentSpeedX < -maxSpeed)
                currentSpeedX = -maxSpeed;
        } else if (!isDashing) {
            if (currentSpeedX > 0) {
                currentSpeedX -= friction * tpf;
                if (currentSpeedX < 0)
                    currentSpeedX = 0;
            } else if (currentSpeedX < 0) {
                currentSpeedX += friction * tpf;
                if (currentSpeedX > 0)
                    currentSpeedX = 0;
            }
        }

        physics.setVelocityX(currentSpeedX);
    }
}