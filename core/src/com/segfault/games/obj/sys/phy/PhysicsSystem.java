package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.PhysicComponent;
import com.segfault.games.obj.ent.Mappers;

public class PhysicsSystem extends IntervalIteratingSystem {

    private final Mappers mappers;
    public PhysicsSystem(JavaKnight instance, float interval, int priority) {
        super(Family.all(PhysicComponent.class).get(), interval, priority);

        mappers = instance.GetEntityManager().GetMappers();
    }

    @Override
    protected void processEntity(Entity entity) {
        if (entity.isScheduledForRemoval()) return;

        PhysicComponent comp = mappers.Physic.get(entity);

        comp.physicSystems.forEach(i ->  i.processEntity(entity, getInterval()));
    }
}
