package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;

public class MovementInputSystem extends IteratingSystem {
    private final JavaKnight instance;
    private final Vector2 dir = new Vector2();
    public MovementInputSystem(JavaKnight ins, int priority) {
        super(Family.all(MovementInputComponent.class,MovingComponent.class)
                    .exclude(PrototypeComp.class).get());
        instance = ins;
        this.priority = priority;
    }
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = instance.EntityManager.Mm.get(entity);
        MovementInputComponent movementInput = instance.EntityManager.Mim.get(entity);

        dir.set(0,0);
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dir.x -= 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dir.x += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dir.y += 1f;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dir.y -= 1f;

        dir.setLength2(movementInput.speed * movementInput.speed);

        movement.dx = dir.x;
        movement.dy = dir.y;

    }
}
