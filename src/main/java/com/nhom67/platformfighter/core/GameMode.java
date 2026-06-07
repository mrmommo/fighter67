package com.nhom67.platformfighter.core;

public enum GameMode {
    TWO_PLAYER("Casual PvP"),
    VS_BOT("Hard Bot Mode");

    private final String displayName;

    GameMode(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
