package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.OrbObjectComponent;
import com.segfault.games.obj.ent.Mappers;

public class OrbUpdateSystem extends IteratingSystem {
    private final Mappers mappers;

    public OrbUpdateSystem(JavaKnight instance, int priority) {
        super(Family.all(OrbObjectComponent.class).get(), priority);

        mappers = instance.GetEntityManager().GetMappers();
    }

    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        OrbObjectComponent orb = mappers.OrbObject.get(entity);

        if (!orb.hasBeenHit)
            return;

        if (orb.elapsedCooldown > orb.cooldown) {
            orb.hasBeenHit = false;
            orb.elapsedCooldown = 0;
            return;
        }

        orb.elapsedCooldown += deltaTime;


    }
}
