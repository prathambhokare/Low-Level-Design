package com.lld.model;

public enum GameState {

    START(1),
    IN_PROGRESS(2),
    HOLD(3),
    CANCELLED(4),
    FINISHED(5);

    private final int code;

    GameState(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
