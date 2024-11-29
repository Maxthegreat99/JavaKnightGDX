package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Engine;
import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.utils.ImmutableArray;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.PhysicComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.obj.sys.SubSystem;

public class PhysicsSystem extends EntitySystem {

    private float accumulator = 0f;
    private ImmutableArray<Entity> entities;
    private final Mappers mappers;
    private final float interval;
    private final EntityManager entityManager;

    public PhysicsSystem(JavaKnight instance, float interval, int priority) {
        super(priority);

        this.interval = interval;
        mappers = instance.GetEntityManager().GetMappers();
        entityManager = instance.GetEntityManager();
    }

    @Override
    public void addedToEngine (Engine engine) {
        entities = engine.getEntitiesFor(Family.all(PhysicComponent.class).get());
    }

    @Override
    public void update(float deltaTime) {

        // avoid the accumulator being constantly too high on slower devices
        float frameTime = Math.min(deltaTime, 0.25f);
        accumulator += frameTime;
        while (accumulator >= interval) {

            for (int i = 0; i < entities.size(); i++) {
                Entity entity = entities.get(i);

                if (entity.isScheduledForRemoval()) continue;

                PhysicComponent comp = mappers.Physic.get(entity);

                for (SubSystem k : comp.physicSystems)
                    k.processEntity(entity, interval, accumulator);

            }

            entityManager.GetPhysicWorld().step(interval, 6, 2);

            accumulator -= interval;
        }
    }
}
