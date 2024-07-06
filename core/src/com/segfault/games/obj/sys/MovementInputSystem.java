package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System that takes userInput and controls specified entities with them
 */
public class MovementInputSystem extends IteratingSystem {
    private final EntityManager manager;
    private final Vector2 dir = new Vector2();
    public MovementInputSystem(JavaKnight ins, int priority) {
        super(Family.all(MovementInputComponent.class,MovingComponent.class).get());
        manager = ins.GetEntityManager();
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = manager.GetMappers().Moving.get(entity);
        MovementInputComponent movementInput = manager.GetMappers().MovementInput.get(entity);

        dir.set(0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dir.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dir.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dir.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dir.y -= 1f;

        dir.setLength2(movementInput.speed2);

        movement.dx = dir.x;
        movement.dy = dir.y;

    }
}
