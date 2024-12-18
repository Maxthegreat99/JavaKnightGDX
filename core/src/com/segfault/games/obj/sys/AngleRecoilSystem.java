package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.IteratingSystem;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.AngleRecoilComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.PointingComponent;
import com.segfault.games.obj.ent.Mappers;

public class AngleRecoilSystem extends IteratingSystem {
    private final Mappers mappers;
    public AngleRecoilSystem(JavaKnight instance, int priority) {
        super(Family.all(AngleRecoilComponent.class, DrawableComponent.class).get(), priority);
        mappers = instance.GetEntityManager().GetMappers();
    }

    /**
     * vector to translate the entity's sprite
     */
    private final Vector2 displace = new Vector2();
    @Override
    protected void processEntity(Entity entity, float deltaTime) {
        if (entity.isScheduledForRemoval()) return;

        DrawableComponent drawable = mappers.Drawable.get(entity);
        AngleRecoilComponent recoil = mappers.AngleRecoil.get(entity);
        PointingComponent pointing = mappers.Pointing.get(entity);



        if (recoil.trigger) {
            recoil.angle += recoil.angleSpeed * deltaTime;
            recoil.trigger = false;
        }
        else if (recoil.angle > 0) {

            recoil.angleSpeed -= recoil.angleDecceleration;
            recoil.angle += recoil.angleSpeed * deltaTime;
            if (recoil.angle <= 0) {
                recoil.angleSpeed = recoil.intialAngleSpeed;
                recoil.angle = 0;
            }
            else if (recoil.angle > recoil.maxAngle) {
                recoil.angle = recoil.maxAngle;
            }

            drawable.sprite.rotate((pointing.flip ? -1 : 1) * recoil.angle);

        }
        else return;

        /* distance to translate the sprite */
        float dis = (drawable.sprite.getWidth() / 2) * MathUtils.sinDeg(recoil.angle);

        displace.set(dis, 0);
        displace.setAngleDeg(pointing.angle);
        displace.rotateRad(MathUtils.PI / 2);

        if (pointing.flip)
            drawable.sprite.translate(-displace.x, -displace.y);
        else drawable.sprite.translate(displace.x, displace.y);

    }

}
