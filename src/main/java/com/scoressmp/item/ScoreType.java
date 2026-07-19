/*
 * Decompiled with CFR 0.152.
 */
package com.scoressmp.item;

public enum ScoreType {
    FIRE,
    WATER,
    MINE,
    DRAGON,
    PVP,
    WARDEN,
    HONOR;


    public String getDisplayName() {
        return com.scoressmp.config.ConfigManager.getDisplayName(this);
    }
}

