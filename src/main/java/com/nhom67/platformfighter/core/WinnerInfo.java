package com.nhom67.platformfighter.core;

public enum WinnerInfo {
    PLAYER_1("PLAYER 1 WINS!"),
    PLAYER_2("PLAYER 2 WINS!");

    private final String message;

    WinnerInfo(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}
