package com.segfault.games.obj.comp;

/**
 * Collision types enum, defines the different types of collsions,
 * that JBump boxes can use.
 * a reminder to please use the Equal() method to see
 * if a specific one, == will compare by reference.
 */
public enum CollisionRelationship {
    PLAYER,
    OBSTACLE,
    OUT_OF_BOUNDS,
    NULL
}
