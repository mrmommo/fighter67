package com.nhom67.platformfighter.core;

public enum GameMode {
    TWO_PLAYER("2 Người chơi"),
    VS_BOT("VS BOT");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
