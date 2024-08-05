package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.dongbat.jbump.Collision;

public interface CollisionEventSystem {
    void HandleCollision(Entity entity, Entity target, Collision collision);
}
