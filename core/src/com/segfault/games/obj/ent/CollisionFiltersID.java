package com.segfault.games.obj.ent;

/**
 * Types of collision filters, collision filters define the basic bahaviour between different collision
 * collision filters should not be used for logic like disposing, creating particles or anything that
 * isnt ralated to return the response value
 */
public enum CollisionFiltersID {
    DEFAULT,
    PLAYER,
    BOUNCE
}
