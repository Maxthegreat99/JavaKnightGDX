package com.segfault.games.obj.comp;

public enum CollisionEvents {

    DISPOSE(0),
    BOUNCING(1);

    public final int flag;

    CollisionEvents(int id) {
        this.flag = 1 << id;
    }
}