package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IntervalIteratingSystem;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.comp.RecOwnerComponent;
import com.segfault.games.obj.ent.Mappers;

public class PhysicsSystem extends IntervalIteratingSystem {

    private final BouncingSystem bouncing;
    private final DamageCollisionSystem damageCollision;
    private final DisposeCollisionSystem disposeCollison;
    private final MovementInputSystem movementInput;
    private final MovementSystem movement;
    private final SpeedDecreaseSystem speedDecrease;
    private final Mappers mappers;
    public PhysicsSystem(JavaKnight instance, float interval, int priority) {
        super(Family.one(MovingComponent.class, RecOwnerComponent.class, CollidesComponent.class).get(), interval, priority);
        bouncing = new BouncingSystem(instance);
        damageCollision = new DamageCollisionSystem(instance);
        disposeCollison = new DisposeCollisionSystem(instance);
        movementInput = new MovementInputSystem(instance);
        movement = new MovementSystem(instance);
        speedDecrease = new SpeedDecreaseSystem(instance);
        mappers = instance.GetEntityManager().GetMappers();
    }

    @Override
    protected void processEntity(Entity entity) {
        if (entity.isScheduledForRemoval()) return;

        if (mappers.SpeedDecrease.get(entity) != null)
            speedDecrease.processEntity(entity);

        if (mappers.MovementInput.get(entity) != null)
            movementInput.processEntity(entity);

        if (mappers.Moving.get(entity) != null)
            movement.processEntity(entity, getInterval());

        if (mappers.Damage.get(entity) != null && (mappers.Collides.get(entity) != null || mappers.RecCollision.get(entity) != null))
            damageCollision.processEntity(entity);

        if (mappers.Bounce.get(entity) != null)
            bouncing.processEntity(entity);

        if (mappers.DisposeOnCollision.get(entity) != null)
            disposeCollison.processEntity(entity);
    }
}
