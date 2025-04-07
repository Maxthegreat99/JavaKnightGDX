package com.segfault.games.obj.comp;

public enum CollisionEvents {

    DISPOSE(0),
    BOUNCING(1),
    GROUND_CHECK(2),
    DASH_CANCEL(3),
    ORB_COLLIDE(4);

    public final int flag;

    CollisionEvents(int id) {
        this.flag = 1 << id;
    }
}