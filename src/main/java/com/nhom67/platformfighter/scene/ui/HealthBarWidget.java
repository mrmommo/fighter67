package com.nhom67.platformfighter.scene.ui;

import com.nhom67.platformfighter.entity.component.PlayerComponent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class HealthBarWidget extends Pane {

    private PlayerComponent player;
    private boolean isLeftAligned;
    
    private double barWidth = 200;
    private double barHeight = 20;
    
    private Rectangle healthBarBg;
    private Rectangle healthBarFg;
    private Text nameText;
    private Text livesText;
    
    private double displayHealth;

    public HealthBarWidget(PlayerComponent player, boolean isLeftAligned, String playerName) {
        this.player = player;
        this.isLeftAligned = isLeftAligned;
        this.displayHealth = player.getCurrentHealth();

        // Background
        healthBarBg = new Rectangle(barWidth, barHeight, Color.DARKGRAY);
        healthBarBg.setStroke(Color.BLACK);
        healthBarBg.setStrokeWidth(2);

        // Foreground (Health)
        healthBarFg = new Rectangle(barWidth, barHeight, Color.web("#E74C3C"));

        // Name text
        nameText = new Text(playerName);
        nameText.setFont(Font.font("Arial", FontWeight.BOLD, 18));
        nameText.setFill(Color.WHITE);

        // Lives text
        livesText = new Text("Lives: " + player.getCurrentLives());
        livesText.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        livesText.setFill(Color.WHITE);

        // Layout alignment
        if (isLeftAligned) {
            nameText.setX(0);
            nameText.setY(-5);
            
            healthBarBg.setX(0);
            healthBarBg.setY(0);
            
            healthBarFg.setX(0);
            healthBarFg.setY(0);
            
            livesText.setX(0);
            livesText.setY(barHeight + 15);
        } else {
            // Right aligned
            nameText.setX(barWidth - nameText.getLayoutBounds().getWidth());
            nameText.setY(-5);
            
            healthBarBg.setX(0);
            healthBarBg.setY(0);
            
            healthBarFg.setX(0); // Will adjust width from right to left in update
            healthBarFg.setY(0);
            
            livesText.setX(barWidth - livesText.getLayoutBounds().getWidth());
            livesText.setY(barHeight + 15);
        }

        getChildren().addAll(healthBarBg, healthBarFg, nameText, livesText);
    }

    public void update(double tpf) {
        // Lerp display health
        double targetHealth = player.getCurrentHealth();
        displayHealth += (targetHealth - displayHealth) * 10 * tpf; // lerp with speed factor

        if (Math.abs(targetHealth - displayHealth) < 0.5) {
            displayHealth = targetHealth;
        }

        double widthRatio = Math.max(0, displayHealth / player.getMaxHealth());
        double currentWidth = barWidth * widthRatio;

        if (isLeftAligned) {
            healthBarFg.setWidth(currentWidth);
        } else {
            healthBarFg.setWidth(currentWidth);
            healthBarFg.setX(barWidth - currentWidth);
        }

        livesText.setText("Lives: " + player.getCurrentLives());
        if (!isLeftAligned) {
            livesText.setX(barWidth - livesText.getLayoutBounds().getWidth());
        }
    }
}
