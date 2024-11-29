package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;

/**
 * interface for systems embed in other systems
 */
public interface SubSystem {

    void processEntity(Entity entity, float v, float a);
}
