package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.MovementInputComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.EntityManager;

/**
 * System that takes userInput and controls specified entities with them
 */
public class MovementInputSystem {
    private final EntityManager manager;
    private final Vector2 dir = new Vector2();
    public MovementInputSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();
    }

    public void processEntity(Entity entity) {
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
