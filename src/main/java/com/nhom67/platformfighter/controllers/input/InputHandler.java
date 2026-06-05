package com.nhom67.platformfighter.controllers.input;

import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.input.UserAction;
import com.nhom67.platformfighter.entity.component.PlayerComponent;
import static com.almasb.fxgl.dsl.FXGL.*;

import javafx.scene.input.KeyCode;

public class InputHandler {
    private Entity player1;
    private Entity player2;
    private boolean player2BotControlled;

    public void setPlayers(Entity p1, Entity p2) {
        this.player1 = p1;
        this.player2 = p2;
    }

    public void setPlayer2BotControlled(boolean botControlled) {
        this.player2BotControlled = botControlled;
    }

    private boolean canControlPlayer2() {
        return player2 != null && !player2BotControlled;
    }

    public void initInput() {
        getInput().addAction(new UserAction("Move Left") {
            @Override
            protected void onActionBegin() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).leftPress();
            }
            @Override
            protected void onAction() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).setMoveDirection(-1);
            }
            @Override
            protected void onActionEnd() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.A);

        getInput().addAction(new UserAction("Move Right") {
            @Override
            protected void onActionBegin() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).rightPress();
            }
            @Override
            protected void onAction() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).setMoveDirection(1);
            }
            @Override
            protected void onActionEnd() {
                if (player1 != null) player1.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.D);

        getInput().addAction(new UserAction("Jump") {
            @Override
            protected void onActionBegin() {
                if (player1 != null) {
                    player1.getComponent(PlayerComponent.class).jump();
                }
            }
        }, KeyCode.W);
        
        getInput().addAction(new UserAction("Drop Down") {
            @Override
            protected void onActionBegin() {
                if (player1 != null) {
                    player1.getComponent(PlayerComponent.class).dropDown();
                }
            }
        }, KeyCode.S);

        getInput().addAction(new UserAction("Shoot") {
            @Override
            protected void onActionBegin() {
                if (player1 != null) {
                    player1.getComponent(PlayerComponent.class).shoot();
                }
            }
        }, KeyCode.F);

        getInput().addAction(new UserAction("Move Left 2") {
            @Override
            protected void onActionBegin() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).leftPress();
            }
            @Override
            protected void onAction() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).setMoveDirection(-1);
            }
            @Override
            protected void onActionEnd() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.LEFT);

        getInput().addAction(new UserAction("Move Right 2") {
            @Override
            protected void onActionBegin() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).rightPress();
            }
            @Override
            protected void onAction() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).setMoveDirection(1);
            }
            @Override
            protected void onActionEnd() {
                if (canControlPlayer2()) player2.getComponent(PlayerComponent.class).stop();
            }
        }, KeyCode.RIGHT);

        getInput().addAction(new UserAction("Jump 2") {
            @Override
            protected void onActionBegin() {
                if (canControlPlayer2()) {
                    player2.getComponent(PlayerComponent.class).jump();
                }
            }
        }, KeyCode.UP);
        
        getInput().addAction(new UserAction("Drop Down 2") {
            @Override
            protected void onActionBegin() {
                if (canControlPlayer2()) {
                    player2.getComponent(PlayerComponent.class).dropDown();
                }
            }
        }, KeyCode.DOWN);

        getInput().addAction(new UserAction("Shoot 2") {
            @Override
            protected void onActionBegin() {
                if (canControlPlayer2()) {
                    player2.getComponent(PlayerComponent.class).shoot();
                }
            }
        }, KeyCode.L);

    }
}
