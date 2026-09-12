package com.pt.zyfooai.utils;

public class ClickDebouncer {

    private long lastClickTime;
    private final long intervalMs;

    public ClickDebouncer() {
        this(450);
    }

    public ClickDebouncer(long intervalMs) {
        this.intervalMs = intervalMs;
    }

    public boolean shouldIgnore() {
        long now = System.currentTimeMillis();
        if (now - lastClickTime < intervalMs) {
            return true;
        }
        lastClickTime = now;
        return false;
    }
}
