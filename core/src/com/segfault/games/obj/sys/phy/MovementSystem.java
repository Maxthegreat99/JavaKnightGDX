package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
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

    private final Vector2 vec = new Vector2();
    private final Vector2 velToApply = new Vector2();
    public void processEntity(Entity entity, float interval, float accumulator) {
        if (entity.isScheduledForRemoval()) return;

        MovingComponent movement = manager.GetMappers().Moving.get(entity);
        DrawableComponent drawable = manager.GetMappers().Drawable.get(entity);

        CollidesComponent collisionInfo = manager.GetMappers().Collides.get(entity);
        boolean hasBox2DCol = collisionInfo != null;

        if (Float.compare(movement.dx, 0f) == 0 && Float.compare(movement.dy, 0f) == 0) return;

        float x = drawable.sprite.getX();
        float y = drawable.sprite.getY();

        // to ensure the movement per update is scaled to the movement per secs
        // we multiply velocity by the interval constant

        float dx = movement.dx * interval;
        float dy = movement.dy * interval;

        float targetX = x + dx;
        float targetY = y + dy;


        if (!hasBox2DCol)
            drawable.sprite.setPosition(targetX, targetY);

        // if box2d collision exists we let it handle the movement
        else {
            Vector2 vec = collisionInfo.physicBody.getWorldCenter();
            Vector2 vel = collisionInfo.physicBody.getLinearVelocity();

            velToApply.set(dx, dy);

            if (Math.abs(vel.x) > Math.abs(movement.dx) && Math.signum(vel.x) == Math.signum(movement.dx)) velToApply.x = 0;
            if (Math.abs(vel.y) > Math.abs(movement.dy) && Math.signum(vel.y) == Math.signum(movement.dy)) velToApply.y = 0;
            if (velToApply.len2() > 0)
                collisionInfo.physicBody.applyLinearImpulse(velToApply, vec, true);

        }


    }
}
