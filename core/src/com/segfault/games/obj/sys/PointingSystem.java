package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PointingComponent;
import com.segfault.games.util.Math;

public class PointingSystem extends IteratingSystem {
    private final JavaKnight ins;

    public PointingSystem(JavaKnight instance, int priority) {
        super(Family.all(PointingComponent.class, DrawableComponent.class).get(), priority);
        ins = instance;

    }

    /*
     * target is the target to be pointing to, base is from where we are pointing to the target
     */
    private final Vector3 targetPoint = new Vector3();
    private final Vector2 basePoint = new Vector2();
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        PointingComponent pointing = ins.GetEntityManager().GetMappers().Pointing.get(entity);
        DrawableComponent drawable = ins.GetEntityManager().GetMappers().Drawable.get(entity);

        if (pointing.cursor) {
            targetPoint.set(Gdx.input.getX(), Gdx.input.getY(), 0);
            targetPoint.set(ins.GetRenderer().GetWorldCamera().unproject(targetPoint));
        }
        else targetPoint.set(pointing.target.sprite.getX() + pointing.target.sprite.getWidth() / 2,
                             pointing.target.sprite.getY() + pointing.target.sprite.getHeight() / 2, 0);

        basePoint.set(pointing.base.sprite.getX() + pointing.base.sprite.getWidth() / 2,
                      pointing.base.sprite.getY() + pointing.base.sprite.getHeight() / 2);

        float angle = Math.AngleBetween(basePoint.x, basePoint.y, targetPoint.x, targetPoint.y);
        pointing.angle = angle;

        /*
         * position where the object must be located
         */
        float x = (MathUtils.cosDeg(angle) * pointing.rotatingRadius) + basePoint.x;
        float y = (MathUtils.sinDeg(angle) * pointing.rotatingRadius) + basePoint.y;

        boolean isAtRightSideFrombase = (angle > 90 || angle < -90);
        boolean flip = (isAtRightSideFrombase && !drawable.sprite.isFlipY()) || (!isAtRightSideFrombase && drawable.sprite.isFlipY());


        if (flip) {
            drawable.sprite.flip(false, true);
            pointing.flip = !pointing.flip;
        }

        drawable.sprite.setOriginCenter();
        drawable.sprite.setRotation(angle);

        drawable.sprite.setPosition(x - drawable.sprite.getWidth() / 2, y - drawable.sprite.getHeight() / 2);
    }
}
