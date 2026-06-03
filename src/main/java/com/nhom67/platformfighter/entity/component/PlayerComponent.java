package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.component.Component;
import com.almasb.fxgl.physics.PhysicsComponent;
import com.almasb.fxgl.time.LocalTimer;
import static com.almasb.fxgl.dsl.FXGL.*;

// IMPORT CÁC LỚP VẬT LÝ BOX2D
import com.almasb.fxgl.physics.box2d.dynamics.Fixture;
import com.almasb.fxgl.physics.box2d.dynamics.Filter;
import com.nhom67.platformfighter.entity.EntityFactory;
import com.nhom67.platformfighter.entity.EntityType;
import com.nhom67.platformfighter.entity.component.WeaponData;
import javafx.util.Duration;

public class PlayerComponent extends Component {

    private PhysicsComponent physics;
    private int jumps = 2;

    private double currentSpeedX = 0;
    private double maxSpeed = 350;
    private double acceleration = 2500;
    private double friction = 2000;
    private int moveDirection = 0; // -1 left, 1 right, 0 stop
    private int facingDirection = 1; // 1 right, -1 left

    private double fastFallAccel = 900;

    private LocalTimer leftTapTimer;
    private LocalTimer rightTapTimer;
    private LocalTimer dashCooldownTimer;
    private LocalTimer dropTimer;

    private double dashSpeed = 800;

    public boolean isDropping = false;
    private boolean isDashing = false;

    // --- HEALTH & LIVES ---
    private int maxHealth = 100;
    private int currentHealth = 100;
    private int maxLives = 3;
    private int currentLives = 3;
    private boolean isDead = false;
    
    private double lastBottomY = 0; // TRACK PREVIOUS Y TO PREVENT FAST-FALL GLITCH

    // --- RESPAWN ---
    private double spawnX;
    private double spawnY;

    // --- WEAPON ---
    private WeaponData currentWeapon = WeaponData.pistol();
    private int currentAmmo = currentWeapon.maxAmmo();
    private boolean isReloading = false;
    private LocalTimer shootTimer;
    private LocalTimer reloadTimer;

    @Override
    public void onAdded() {
        physics = entity.getComponent(PhysicsComponent.class); // FIX #2

        leftTapTimer = newLocalTimer();
        rightTapTimer = newLocalTimer();
        dashCooldownTimer = newLocalTimer();
        dropTimer = newLocalTimer();
        shootTimer = newLocalTimer();
        reloadTimer = newLocalTimer();
        
        dashCooldownTimer.capture();
        leftTapTimer.capture();
        rightTapTimer.capture();
        shootTimer.capture();
        reloadTimer.capture();

        spawnX = entity.getX();
        spawnY = entity.getY();
    }

    public void setMoveDirection(int dir) {
        this.moveDirection = dir;
        if (dir != 0) {
            this.facingDirection = dir;
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
            physics.setVelocityY(50);  // FIX #5: ADD: kick player downward immediately
        }
    }

    @Override
    public void onUpdate(double tpf) {
        // 1. Quản lý trạng thái tụt xuống qua sàn mềm
        if (isDropping) {

            if (dropTimer.elapsed(Duration.seconds(0.3))) { // FIX #5: CHANGE: 0.25 -> 0.3
                isDropping = false; // FIX #5
            }
        }

        // --- WEAPON RELOAD LOGIC ---
        if (isReloading && currentWeapon.isDefault()) {
            if (reloadTimer.elapsed(Duration.seconds(2.0))) {
                currentAmmo = currentWeapon.maxAmmo();
                isReloading = false;
            }
        }

        // 2. --- LOGIC QUYẾT ĐỊNH XUYÊN ĐỊA HÌNH NÂNG CAO (ONE-WAY PLATFORM) ---
        // QUAN TRỌNG: Phải xét isMovingUp và isDropping bên ngoài vòng lặp va chạm!
        // Nếu không, Box2D sẽ tính toán va chạm trước khi game kịp đổi mask xuyên qua.
        boolean isMovingUp = physics.getVelocityY() < -50;
        boolean isInsidePlatform = false;

        if (getGameWorld() != null) {
            for (Entity platform : getGameWorld().getEntitiesByType(EntityType.ONE_WAY_PLATFORM)) {
                if (getEntity().isColliding(platform)) {
                    double playerBottomY = getEntity().getBottomY();
                    double platformTop    = platform.getY();

                    // Nếu chân đang kẹt bên trong sàn (thấp hơn mặt trên sàn một chút)
                    if (playerBottomY > platformTop + 5.0) {
                        // Để ngăn lỗi lọt hố do rơi quá nhanh, ta kiểm tra thêm lastBottomY
                        // Nếu khung hình trước đang ở trên sàn, mà khung hình này lọt xuống dưới -> KHÔNG cho xuyên qua
                        if (lastBottomY <= platformTop + 5.0) {
                            // Giữ nguyên isInsidePlatform = false để Box2D đẩy nhân vật ngược lên mặt sàn
                        } else {
                            isInsidePlatform = true;
                            break;
                        }
                    }
                }
            }
        }

        short newMaskBits;

        // Bất cứ khi nào ĐANG BAY LÊN, ĐANG TỤT XUỐNG, hoặc ĐANG KẸT TRONG SÀN -> Xuyên qua
        if (isMovingUp || isDropping || isInsidePlatform) {
            newMaskBits = (short) (EntityFactory.CATEGORY_GROUND | EntityFactory.CATEGORY_CRATE);
        } else {
            // Còn lại -> Va chạm bình thường (Đứng được trên sàn)
            newMaskBits = (short) (EntityFactory.CATEGORY_GROUND | EntityFactory.CATEGORY_ONE_WAY | EntityFactory.CATEGORY_CRATE);
        }

        lastBottomY = getEntity().getBottomY(); // LƯU LẠI VỊ TRÍ CHÂN CHO KHUNG HÌNH SAU

        // Cập nhật MaskBits vào các Fixture vật lý
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

    // --- WEAPON & SHOOTING METHODS ---

    public void shoot() {
        if (isDead || isReloading) return;
        
        if (shootTimer.elapsed(Duration.seconds(currentWeapon.fireRate()))) {
            if (currentAmmo > 0) {
                currentAmmo--;
                shootTimer.capture();
                
                spawn("bullet", new com.almasb.fxgl.entity.SpawnData(entity.getX() + (facingDirection == 1 ? 50 : -20), entity.getY() + 15)
                        .put("bulletData", currentWeapon.bulletData())
                        .put("facingRight", facingDirection == 1)
                        .put("owner", entity));
                
                if (currentAmmo <= 0) {
                    handleEmptyAmmo();
                }
            } else {
                handleEmptyAmmo();
            }
        }
    }

    private void handleEmptyAmmo() {
        if (currentWeapon.isDefault()) {
            isReloading = true;
            reloadTimer.capture();
        } else {
            equipWeapon(WeaponData.pistol());
        }
    }

    public void equipWeapon(WeaponData weapon) {
        this.currentWeapon = weapon;
        this.currentAmmo = weapon.maxAmmo();
        this.isReloading = false;
    }

    public void applyKnockback(double forceX) {
        this.currentSpeedX += forceX;
    }

    // --- HEALTH & LIVES METHODS ---

    public void takeDamage(int amount) {
        if (isDead) return;
        
        currentHealth -= amount;
        if (currentHealth <= 0) {
            currentHealth = 0;
            loseLife();
        }
    }

    public void loseLife() {
        if (isDead) return;

        currentLives--;
        currentHealth = maxHealth;
        
        if (currentLives <= 0) {
            isDead = true;
        } else {
            respawn();
        }
    }

    public void respawn() {
        physics.overwritePosition(new javafx.geometry.Point2D(spawnX, spawnY));
        physics.setVelocityX(0);
        physics.setVelocityY(0);
        currentSpeedX = 0;
        isDropping = false;
        isDashing = false;
        moveDirection = 0;
    }

    public void reset() {
        currentHealth = maxHealth;
        currentLives = maxLives;
        isDead = false;
        equipWeapon(WeaponData.pistol());
        respawn();
    }

    // --- GETTERS & SETTERS ---
    
    public int getMaxHealth() { return maxHealth; }
    public void setMaxHealth(int maxHealth) { this.maxHealth = maxHealth; }
    
    public int getCurrentHealth() { return currentHealth; }
    public void setCurrentHealth(int currentHealth) { this.currentHealth = currentHealth; }
    
    public int getMaxLives() { return maxLives; }
    public void setMaxLives(int maxLives) { this.maxLives = maxLives; }
    
    public int getCurrentLives() { return currentLives; }
    public void setCurrentLives(int currentLives) { this.currentLives = currentLives; }
    
    public boolean isDead() { return isDead; }
    public void setDead(boolean dead) { isDead = dead; }
}