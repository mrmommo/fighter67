package com.nhom67.platformfighter.scene.ui;

import com.nhom67.platformfighter.entity.component.PlayerComponent;
import com.nhom67.platformfighter.entity.component.WeaponType;
import javafx.scene.effect.Blend;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.ColorInput;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.List;

/**
 * HUD widget cho mỗi player: thanh máu (accent color → đỏ khi < 30%),
 * icon mạng (heart.png tô màu accent, đen khi mất mạng),
 * vũ khí hiện tại (ảnh gun + tên).
 */
public class HealthBarWidget extends Pane {

    // ── Layout constants ──────────────────────────────────────────────
    private static final double PW = 500; // panel width
    private static final double PH = 120; // panel height
    private static final double PAD = 16; // padding
    private static final double BW = 356; // health bar width
    private static final double BH = 24; // health bar height
    private static final double R1Y = 18; // row-1 top Y
    private static final double R2Y = 68; // row-2 top Y
    private static final double HS = 32; // heart icon size
    private static final double HG = 5; // heart gap
    private static final double GH = 38; // gun icon height

    // ── Fields ───────────────────────────────────────────────────────
    private final PlayerComponent player;
    private final boolean left; // true = P1 (left-aligned), false = P2 (mirrored)
    private final Color accent;

    private Rectangle hfg; // health foreground bar
    private Text weaponTxt;
    private Text ammoTxt;
    private ImageView weaponIV;
    private final List<ImageView> heartIVs = new ArrayList<>();

    private double displayHp;
    private double bx; // barX position inside panel
    private WeaponType lastWT;

    // ─────────────────────────────────────────────────────────────────
    public HealthBarWidget(PlayerComponent player, boolean left,
            String label, String accentHex) {
        this.player = player;
        this.left = left;
        this.accent = Color.web(accentHex);
        this.displayHp = player.getCurrentHealth();
        build(label);
    }

    // ── Build UI ─────────────────────────────────────────────────────
    private void build(String label) {
        // Glass background panel
        Rectangle bg = new Rectangle(PW, PH);
        bg.setFill(Color.rgb(0, 0, 0, 0.10));
        bg.setStroke(accent.deriveColor(0, 1, 1, 0.65));
        bg.setStrokeWidth(1.5);
        bg.setArcWidth(12);
        bg.setArcHeight(12);

        // Thin accent glow strip at top
        Rectangle glow = new Rectangle(PW - 8, 3);
        glow.setX(4);
        glow.setY(0);
        glow.setFill(accent);
        glow.setArcWidth(12);
        glow.setArcHeight(12);
        glow.setOpacity(0.88);

        getChildren().addAll(bg, glow);
        buildHealthRow(label);
        buildHearts();
        buildWeapon();
    }

    /** Row 1 */
    private void buildHealthRow(String label) {
        if (left) {
            bx = PAD + 34;
        } else {
            bx = PAD + 50;
        }

        Rectangle hbg = new Rectangle(BW, BH);
        hbg.setX(bx);
        hbg.setY(R1Y);
        hbg.setFill(Color.rgb(0, 0, 0, 0.15));
        hbg.setArcWidth(6);
        hbg.setArcHeight(6);
        hbg.setStroke(Color.rgb(255, 255, 255, 0.08));
        hbg.setStrokeWidth(1);

        hfg = new Rectangle(BW, BH);
        hfg.setX(bx);
        hfg.setY(R1Y);
        hfg.setArcWidth(6);
        hfg.setArcHeight(6);
        hfg.setFill(accentGrad());

        Text lbl = new Text(label);
        lbl.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        lbl.setFill(accent);
        lbl.setX(left ? PAD : (bx + BW + 6));
        lbl.setY(R1Y + BH - 2);

        getChildren().addAll(hbg, hfg, lbl); // bỏ hpTxt
    }

    /** Row 2: hearts */
    private void buildHearts() {
        Image img = null;
        try {
            img = new Image(getClass().getResourceAsStream("/assets/textures/ui/heart.png"));
        } catch (Exception ignored) {
        }

        int max = player.getMaxLives();
        double block = max * (HS + HG) - HG;

        for (int i = 0; i < max; i++) {
            ImageView iv = new ImageView(img);
            iv.setFitWidth(HS);
            iv.setFitHeight(HS);
            iv.setPreserveRatio(true);
            iv.setSmooth(true);

            double ix;
            if (left) {
                // P1: hearts bắt đầu từ PAD (trái)
                ix = PAD + i * (HS + HG);
            } else {
                // P2: hearts anchor phải panel (đối xứng P1)
                ix = PW - PAD - block + i * (HS + HG);
            }

            iv.setLayoutX(ix);
            iv.setLayoutY(R2Y + 1);
            tintHeart(iv, true);
            heartIVs.add(iv);
            getChildren().add(iv);
        }
    }

    private void buildWeapon() {
        int max = player.getMaxLives();
        double block = max * (HS + HG) - HG; // tổng width của hearts row

        double gx, tx;
        if (left) {
            // P1: [hearts từ PAD] [gun] [text]
            gx = PAD + block + 10;
            tx = gx + GH + 6;
        } else {
            // P2 mirror: [text] [gun] [hearts anchor phải]
            // hearts bắt đầu tại: PW - PAD - block
            // gun icon nằm NGAY TRƯỚC hearts
            gx = PW - PAD - block - GH - 10;
            tx = gx - 70; // text nằm trước gun icon
        }

        weaponIV = new ImageView();
        weaponIV.setFitHeight(GH);
        weaponIV.setPreserveRatio(true);
        weaponIV.setSmooth(true);
        weaponIV.setLayoutX(gx);
        weaponIV.setLayoutY(R2Y - 1);

        weaponTxt = new Text("PISTOL");
        weaponTxt.setFont(Font.font("Arial", FontWeight.BOLD, 15));
        weaponTxt.setFill(accent);
        weaponTxt.setX(tx);
        weaponTxt.setY(R2Y + 18);

        ammoTxt = new Text("7/7");
        ammoTxt.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        ammoTxt.setFill(Color.rgb(210, 225, 235));
        ammoTxt.setX(tx);
        ammoTxt.setY(R2Y + 36);

        getChildren().addAll(weaponIV, weaponTxt, ammoTxt);
        refreshWeapon();
    }

    // ── Per-frame update ─────────────────────────────────────────────
    public void update(double tpf) {
        // Lerp health display
        double target = player.getCurrentHealth();
        displayHp += (target - displayHp) * 10 * tpf;
        if (Math.abs(target - displayHp) < 0.5)
            displayHp = target;

        double ratio = Math.max(0, displayHp / player.getMaxHealth());
        double w = BW * ratio;
        boolean crit = ratio < 0.30;

        // Health bar
        hfg.setFill(crit ? redGrad() : accentGrad());
        hfg.setWidth(w);
        hfg.setX(left ? bx : bx + BW - w); // P1 fills L→R, P2 fills R→L

        // Life icons: alive = accent tint, dead = dark
        int lives = player.getCurrentLives();
        for (int i = 0; i < heartIVs.size(); i++) {
            tintHeart(heartIVs.get(i), i < lives);
        }

        // Weapon icon + name (only refreshes when weapon changes)
        refreshWeapon();

        // Ammo count
        String a = player.isReloading()
                ? "RELOAD"
                : player.getCurrentAmmo() + "/" + player.getCurrentWeapon().maxAmmo();
        ammoTxt.setText(a);
        ammoTxt.setFill(player.isReloading()
                ? Color.web("#ff7675")
                : Color.rgb(210, 225, 235));
    }

    // ── Helpers ──────────────────────────────────────────────────────

    /** Tô màu heart icon: accent (alive) hoặc đen mờ (mất mạng). */
    private void tintHeart(ImageView iv, boolean alive) {
        Color c = alive ? accent : Color.rgb(20, 20, 20);
        ColorInput ci = new ColorInput(0, 0, HS + 4, HS + 4, c);
        Blend b = new Blend(BlendMode.SRC_ATOP);
        b.setTopInput(ci);
        iv.setEffect(b);
        iv.setOpacity(alive ? 1.0 : 0.38);
    }

    /** Cập nhật ảnh vũ khí khi weapon type thay đổi. */
    private void refreshWeapon() {
        WeaponType wt = player.getCurrentWeapon().type();
        if (wt == lastWT)
            return;
        lastWT = wt;
        try {
            weaponIV.setImage(new Image(
                    getClass().getResourceAsStream("/assets/guns/" + gunFile(wt))));
        } catch (Exception ignored) {
        }
        weaponTxt.setText(wt.name());
    }

    private String gunFile(WeaponType t) {
        return switch (t) {
            case PISTOL -> "piston.png";
            case SHOTGUN -> "shotgun.png";
            case RIFLE -> "rifle.png";
            case UZI -> "uzi.png";
            case AK -> "AK-47.png";
            default -> "piston.png";
        };
    }

    /** Gradient theo màu accent của map. */
    private LinearGradient accentGrad() {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, accent.brighter()),
                new Stop(1, accent));
    }

    /** Gradient đỏ cảnh báo (HP < 30%). */
    private LinearGradient redGrad() {
        return new LinearGradient(0, 0, 1, 0, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#ff6b6b")),
                new Stop(1, Color.web("#c0392b")));
    }
}
