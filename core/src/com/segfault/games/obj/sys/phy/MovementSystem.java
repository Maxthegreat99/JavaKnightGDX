package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.MovingComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.ent.indexEntitySystems;
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

    public void processEntity(Entity entity, float interval, float accumulator) {
        if (entity.isScheduledForRemoval()) return;


        MovingComponent movement = manager.GetMappers().Moving.get(entity);
        DrawableComponent drawable = manager.GetMappers().Drawable.get(entity);

        CollidesComponent collisionInfo = manager.GetMappers().Collides.get(entity);
        boolean hasBox2DCol = collisionInfo != null;

        Vector2 pos;
        if (hasBox2DCol) {

            pos = collisionInfo.physicBody.getPosition();

            float interpolation = accumulator / interval;

            vec.set(collisionInfo.x, collisionInfo.y);
            vec.lerp(pos, interpolation);


            collisionInfo.x = pos.x;
            collisionInfo.y = pos.y;
            drawable.sprite.setPosition(vec.x * Renderer.PIXEL_TO_METERS - collisionInfo.width * Renderer.PIXEL_TO_METERS / 2, vec.y * Renderer.PIXEL_TO_METERS - collisionInfo.height * Renderer.PIXEL_TO_METERS / 2);
        }
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
            collisionInfo.physicBody.applyLinearImpulse(dx, dy, vec.x, vec.y, true);
            Vector2 vel = collisionInfo.physicBody.getLinearVelocity();

            if (vel.len2() > movement.maxVel * movement.maxVel)
                collisionInfo.physicBody.setLinearVelocity(vel.nor().scl(movement.maxVel));
        }


    }
}
