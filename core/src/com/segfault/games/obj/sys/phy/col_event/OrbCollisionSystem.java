package com.segfault.games.obj.sys.phy.col_event;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.physics.box2d.Contact;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.OrbObjectComponent;
import com.segfault.games.obj.comp.PlayerDashComponent;
import com.segfault.games.obj.ent.Mappers;

public class OrbCollisionSystem implements CollisionEventSystem {

    private final Mappers mappers;

    public OrbCollisionSystem(JavaKnight instance) {
        mappers = instance.GetEntityManager().GetMappers();
    }

    @Override
    public void HandleCollision(Entity entity, Entity target, Contact collision) {
        Entity ply = target;
        Entity orb = entity;

        PlayerDashComponent plyDash = mappers.PlayerDash.get(ply);

        if (plyDash.canDash)
            return;
        OrbObjectComponent orbComp = mappers.OrbObject.get(orb);

        if (orbComp.hasBeenHit)
            return;

        plyDash.canDash = true;
        plyDash.cooldownElapsedTime = plyDash.dashCooldown + 0.1f;

        orbComp.elapsedCooldown = 0f;
        orbComp.hasBeenHit = true;

    }

    @Override
    public void HandleEndCollision(Entity entity, Entity target, Contact collision) {

    }
}
