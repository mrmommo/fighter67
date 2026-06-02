package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import static com.almasb.fxgl.dsl.FXGL.*;

// IMPORT CÁC LỚP VẬT LÝ BOX2D
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.dynamics.Filter;
import com.nhom67.platformfighter.entity.EntityFactory;
import javafx.util.Duration;

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
        physics.setVelocityY(-600); // Lực nhảy
        jumps--;
    }

    public void dropDown() {
        if (!isDropping && physics.isOnGround()) {
            isDropping = true;
            dropTimer.capture();
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // 1. Quản lý trạng thái tụt xuống qua sàn mềm
        if (isDropping) {
            physics.setVelocityY(800);

            if (dropTimer.elapsed(Duration.seconds(0.25))) {
                isDropping = false;
            }
        }

        // 2. --- LOGIC QUYẾT ĐỊNH XUYÊN ĐỊA HÌNH (GUN MAYHEM STYLE) ---
        boolean isMovingUp = physics.getVelocityY() < -10;
        short newMaskBits;

        if (isMovingUp || isDropping) {
            newMaskBits = EntityFactory.CATEGORY_GROUND;
        } else {
            newMaskBits = (short) (EntityFactory.CATEGORY_GROUND | EntityFactory.CATEGORY_ONE_WAY);
        }

        // SỬA LỖI TẠI ĐÂY: Sử dụng vòng lặp for-each thay vì while-loop và dùng trực tiếp thuộc tính .maskBits
        if (physics.getBody() != null) {
            for (Fixture f : physics.getBody().getFixtures()) {
                Filter filter = f.getFilterData();
                if (filter.maskBits != newMaskBits) {
                    filter.maskBits = newMaskBits;
                    f.setFilterData(filter);
                }
            }
        }
        // --- KẾT THÚC LOGIC XUYÊN ĐỊA HÌNH ---

        // 3. Logic Jumps & Fast Fall gốc của bạn
        if (physics.isOnGround() && physics.getVelocityY() > -100) {
            jumps = 2;
        } else {
            if (physics.getVelocityY() > 0 && !isDropping) {
                physics.setVelocityY(physics.getVelocityY() + fastFallAccel * tpf);
            }
        }

        // 4. Logic Dash & Quán tính di chuyển gốc của bạn
        if (isDashing && dashCooldownTimer.elapsed(Duration.seconds(0.2))) {
            isDashing = false;
        }

        if (moveDirection != 0 && !isDashing) {
            currentSpeedX += moveDirection * acceleration * tpf;
            if (currentSpeedX > maxSpeed) currentSpeedX = maxSpeed;
            if (currentSpeedX < -maxSpeed) currentSpeedX = -maxSpeed;
        } else if (!isDashing) {
            if (currentSpeedX > 0) {
                currentSpeedX -= friction * tpf;
                if (currentSpeedX < 0) currentSpeedX = 0;
            } else if (currentSpeedX < 0) {
                currentSpeedX += friction * tpf;
                if (currentSpeedX > 0) currentSpeedX = 0;
            }
        }

        physics.setVelocityX(currentSpeedX);
    }
}