package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;

public interface CollisionEventSystem {
    void HandleCollision(Entity entity, Entity target, Contact collision);

    void HandleEndCollision(Entity entity, Entity target, Contact collision);
}
