package com.nhom67.platformfighter.ai;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import com.nhom67.platformfighter.entity.EntityType;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import javafx.util.Duration;

import static com.almasb.fxgl.dsl.FXGL.*;

public class BotComponent extends Component {

    // --- State Machine ---
    private enum BotState { APPROACH, HOLD, RETREAT, GRAB_CRATE }
    private BotState state = BotState.HOLD;

    // --- References ---
    private Entity target;
    private PlayerComponent playerComp;
    private PhysicsComponent physics;

    // --- Timers ---
    private LocalTimer jumpCooldownTimer;

    // --- Constants (khoảng cách theo trục X) ---
    private static final double MIN_COMBAT_DIST = 35;  // px – dưới mức này thì lui
    private static final double IDEAL_MAX_DIST  = 220;  // px – trên mức này thì tiến lại
    private static final double SHOOT_RANGE     = 10080;  // px – tầm tối đa để tiếp cận
    private static final double SHOOT_ALIGN_Y   = 70;   // px – chênh lệch Y tối đa (ngang hàng)
    private static final double CRATE_PREFER    = 380;  // px – chỉ nhặt crate khi đủ xa đối thủ
    private static final double JUMP_DY_THRESH  = -60;  // py – nhảy khi target cao hơn 60px
    private static final double DROP_DY_THRESH  = 80;   // py – tụt sàn khi target thấp hơn 80px
    private static final double JUMP_COOLDOWN       = 0.65; // s – nghỉ giữa các lần nhảy
    private static final double DOUBLE_JUMP_DY_THRESH = -25; // py – vẫn cần lên khi nhảy lần 2
    private static final double DOUBLE_JUMP_VY_MAX    = 80;  // vy – nhảy đôi gần đỉnh / đang rơi nhẹ

    // -----------------------------------------------------------------

    /** Gọi từ FighterApp sau khi spawn để gán target (player1) */
    public void setTarget(Entity target) {
        this.target = target;
    }

    @Override
    public void onAdded() {
        playerComp = entity.getComponent(PlayerComponent.class);
        physics     = entity.getComponent(PhysicsComponent.class);
        jumpCooldownTimer = newLocalTimer();
        jumpCooldownTimer.capture();
    }

    @Override
    public void onUpdate(double tpf) {
        // --- Guard: bot đã chết hoặc target không hợp lệ ---
        if (playerComp == null || playerComp.isDead()) return;
        if (playerComp.isHitStunned()) {
            playerComp.stop();
            return;
        }
        if (target == null || !target.isActive()) return;

        PlayerComponent targetComp = target.getComponent(PlayerComponent.class);
        if (targetComp != null && targetComp.isDead()) {
            playerComp.stop();
            return;
        }

        // --- Tính vector tới target ---
        double dx      = target.getX() - entity.getX();
        double dy      = target.getY() - entity.getY();
        double distX   = Math.abs(dx);
        int    dirToTarget = dx > 0 ? 1 : -1;

        // --- Tìm crate gần nhất ---
        Entity nearestCrate  = findNearestCrate();
        double crateDist     = nearestCrate != null
                ? Math.abs(nearestCrate.getX() - entity.getX())
                : Double.MAX_VALUE;

        // --- Chọn state (ưu tiên giữ khoảng cách bắn) ---
        if (distX < MIN_COMBAT_DIST) {
            state = BotState.RETREAT;
        } else if (distX > SHOOT_RANGE) {
            state = BotState.APPROACH;
        } else if (nearestCrate != null
                && crateDist < distX
                && crateDist < CRATE_PREFER
                && distX >= MIN_COMBAT_DIST) {
            state = BotState.GRAB_CRATE;
        } else if (distX > IDEAL_MAX_DIST) {
            state = BotState.APPROACH;
        } else {
            state = BotState.HOLD;
        }

        // --- Thực thi state ---
        switch (state) {

            case APPROACH -> {
                playerComp.setMoveDirection(dirToTarget);
                tryJump(dy);
                tryDropDown(dy);
                tryShoot(distX, dy, dirToTarget);
            }

            case HOLD -> {
                // Đứng giữ tầm, quay mặt đối thủ và bắn
                playerComp.stop();
                tryJump(dy);
                tryShoot(distX, dy, dirToTarget);
            }

            case RETREAT -> {
                playerComp.setMoveDirection(-dirToTarget);
                tryShoot(distX, dy, dirToTarget);
            }

            case GRAB_CRATE -> {
                double crateDx  = nearestCrate.getX() - entity.getX();
                int    crateDir = crateDx > 0 ? 1 : -1;
                playerComp.setMoveDirection(crateDir);
                tryJump(nearestCrate.getY() - entity.getY());
            }
        }
    }

    // ---- Helper methods ----

    /** Chỉ bắn khi đủ gần (X) và ngang hàng với đối thủ (Y). */
    private void tryShoot(double distX, double dy, int dirToTarget) {
        if (!canShoot(distX, dy)) {
            return;
        }
        playerComp.face(dirToTarget);
        playerComp.shoot();
    }

    private boolean canShoot(double distX, double dy) {
        boolean closeEnough = distX <= IDEAL_MAX_DIST;
        boolean sameLevel = Math.abs(dy) <= SHOOT_ALIGN_Y;
        return closeEnough && sameLevel;
    }

    private void tryJump(double dy) {
        if (physics == null || !playerComp.canJump()) {
            return;
        }
        if (!jumpCooldownTimer.elapsed(Duration.seconds(JUMP_COOLDOWN))) {
            return;
        }

        boolean onGround = physics.isOnGround();

        // Nhảy đầu: đối thủ / mục tiêu cao hơn, đang đứng sàn
        if (onGround && dy < JUMP_DY_THRESH) {
            playerComp.jump();
            jumpCooldownTimer.capture();
            return;
        }

        // Nhảy đôi: trên không, còn 1 lần nhảy, vẫn chưa ngang tầm đối thủ
        if (!onGround
                && playerComp.getRemainingJumps() == 1
                && dy < DOUBLE_JUMP_DY_THRESH
                && physics.getVelocityY() > -DOUBLE_JUMP_VY_MAX) {
            playerComp.jump();
            jumpCooldownTimer.capture();
        }
    }

    private void tryDropDown(double dy) {
        boolean onGround = (physics != null) && physics.isOnGround();
        if (dy > DROP_DY_THRESH && onGround) {
            playerComp.dropDown();
        }
    }

    private Entity findNearestCrate() {
        var crates = getGameWorld().getEntitiesByType(EntityType.CRATE);
        Entity nearest = null;
        double minDist  = Double.MAX_VALUE;
        for (Entity crate : crates) {
            double d = Math.abs(crate.getX() - entity.getX());
            if (d < minDist) {
                minDist = d;
                nearest = crate;
            }
        }
        return nearest;
    }
}
