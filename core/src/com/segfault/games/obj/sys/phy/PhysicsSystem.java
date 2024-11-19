package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.ashley.utils.ImmutableArray;
import com.badlogic.gdx.Gdx;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.PhysicComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.obj.sys.SubSystem;

public class PhysicsSystem extends EntitySystem {

    private ImmutableArray<Entity> entities;
    private final Mappers mappers;
    private final float interval;
    private float accumulator = 0f;
    private final EntityManager entityManager;
    public PhysicsSystem(JavaKnight instance, float interval, int priority) {
        super(priority);

        entities = getEngine().getEntitiesFor(Family.all(PhysicComponent.class).get());
        this.interval = interval;
        mappers = instance.GetEntityManager().GetMappers();
        entityManager = instance.GetEntityManager();
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
                    k.processEntity(entity, interval);

            }

            entityManager.GetPhysicWorld().step(interval, 6, 2);

            accumulator -= interval;
        }
    }
}
