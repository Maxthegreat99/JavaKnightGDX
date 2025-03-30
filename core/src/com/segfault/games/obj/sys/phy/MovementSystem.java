package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.comp.AcceleratedBodyComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.sys.SubSystem;

/**
 * movement system, controling entities specifying velocity,
 * this system runs at a constant rate.
 */
public class MovementSystem implements SubSystem {
    private final EntityManager manager;

    public MovementSystem(JavaKnight ins) {
        manager = ins.GetEntityManager();

    }
    private final Vector2 snapedPos = new Vector2();
    private final Vector2 velToApply = new Vector2();
    public void processEntity(Entity entity, float interval, float accumulator) {
        if (entity.isScheduledForRemoval()) return;

        AcceleratedBodyComponent movement = manager.GetMappers().AcceleratedBody.get(entity);

        CollidesComponent collisionInfo = manager.GetMappers().Collides.get(entity);

        collisionInfo.lastX = collisionInfo.physicBody.getPosition().x;
        collisionInfo.lastY = collisionInfo.physicBody.getPosition().y;

        float fx = collisionInfo.physicBody.getMass() * movement.ax;
        float fy = collisionInfo.physicBody.getMass() * movement.ay;

        collisionInfo.physicBody.applyForceToCenter(fx, fy, true);


    }
}
