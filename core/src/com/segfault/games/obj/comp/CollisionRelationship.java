package com.segfault.games.obj.comp;

/**
 * Collision types enum, defines the different types of collsions,
 * that box2d boxes can use.
 * a reminder to please use the Equal() method to see
 * if a specific one, == will compare by reference.
 */
public enum CollisionRelationship {
    PLAYER(0),
    OBSTACLE(1),
    OUT_OF_BOUNDS(2),
    BULLET(3 ),
    OBJECT(4);


    public final int flag;

    CollisionRelationship(int id) {
        this.flag = 1 << id;
    }
}
