package com.nhom67.platformfighter.entity.component;

import com.almasb.fxgl.entity.component.Component;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class GunViewComponent extends Component {
    private PlayerComponent player;
    private WeaponType currentWeaponType;
    private ImageView gunImageView;

    @Override
    public void onAdded() {
        player = entity.getComponent(PlayerComponent.class);
        gunImageView = new ImageView();

        entity.getViewComponent().addChild(gunImageView);
        updateGunView();
    }

    @Override
    public void onUpdate(double tpf) {
        if (player.getCurrentWeapon().type() != currentWeaponType) {
            updateGunView();
        }

        // Cập nhật hướng súng theo hướng nhân vật
        int dir = player.getFacingDirection();
        gunImageView.setScaleX(dir);

        if (dir == 1) {
            // Quay phải: Xương tay nằm ở khoảng X = 25
            switch (currentWeaponType) {
                case PISTOL:
                    gunImageView.setTranslateX(30);
                    break;
                case SHOTGUN:
                    gunImageView.setTranslateX(5);
                    break;
                case RIFLE:
                    gunImageView.setTranslateX(5);
                    break;
                case AK:
                    gunImageView.setTranslateX(0);
                    break;
                default:
                    gunImageView.setTranslateX(20);
                    break;
            }
        } else {
            // Quay trái: Súng bị lật, hãy tự chỉnh các số âm hoặc dương nhỏ dưới đây
            // sao cho báng súng khớp vào tay (không lòi ra ngoài).
            switch (currentWeaponType) {
                case PISTOL:
                    gunImageView.setTranslateX(13);
                    break;
                case SHOTGUN:
                    gunImageView.setTranslateX(0);
                    break;
                case RIFLE:
                    gunImageView.setTranslateX(-20);
                    break;
                case AK:
                    gunImageView.setTranslateX(-2);
                    break;
                default:
                    gunImageView.setTranslateX(0);
                    break;
            }
        }

        // Ensure the gun is rendered on top of the player animation
        gunImageView.toFront();
    }

    private void updateGunView() {
        currentWeaponType = player.getCurrentWeapon().type();
        String imagePath = getWeaponImagePath(currentWeaponType);

        if (imagePath != null) {
            try {
                Image img = new Image(getClass().getResource(imagePath).toExternalForm());
                gunImageView.setImage(img);
                gunImageView.setPreserveRatio(true);

                // Thiết lập kích thước (FitWidth) và chiều cao (TranslateY) cho từng loại súng.
                // TranslateY càng nhỏ thì súng càng cao lên.
                switch (currentWeaponType) {
                    case PISTOL:
                        gunImageView.setFitWidth(20);
                        gunImageView.setTranslateY(27);
                        break;
                    case SHOTGUN:
                        gunImageView.setFitWidth(60);
                        gunImageView.setTranslateY(3); // Cao hơn (nhỏ hơn 10)
                        break;
                    case RIFLE:
                        gunImageView.setFitWidth(80);
                        gunImageView.setTranslateY(-10); // Cao hơn (nhỏ hơn 10)
                        break;
                    case AK:
                        gunImageView.setFitWidth(65);
                        gunImageView.setTranslateY(2); // Cao hơn (nhỏ hơn 10)
                        break;
                    default:
                        gunImageView.setFitWidth(40);
                        gunImageView.setTranslateY(20);
                        break;
                }

            } catch (Exception e) {
                System.out.println("Could not load gun image: " + imagePath);
            }
        }
    }

    private String getWeaponImagePath(WeaponType type) {
        switch (type) {
            case PISTOL:
                return "/assets/guns/piston.png"; // sửa thành piston.png
            case SHOTGUN:
                return "/assets/guns/shotgun.png";
            case RIFLE:
                return "/assets/guns/rifle.png";
            case UZI:
                return "/assets/guns/uzi.png";
            case AK:
                return "/assets/guns/AK-47.png";
            default:
                return "/assets/guns/piston.png";
        }
    }
}
