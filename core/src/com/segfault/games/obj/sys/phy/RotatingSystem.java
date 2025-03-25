package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.MathUtils;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.comp.RotatingComponent;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.obj.sys.SubSystem;

public class RotatingSystem implements SubSystem {


    public final Mappers mappers;
    public RotatingSystem (JavaKnight ins) {
        mappers = ins.GetEntityManager().GetMappers();

    }


    @Override
    public void processEntity(Entity entity, float interval, float accumulator) {
        CollidesComponent col = mappers.Collides.get(entity);
        RotatingComponent rot = mappers.Rotating.get(entity);
        DrawableComponent dra = mappers.Drawable.get(entity);

        col.physicBody.setAngularVelocity(rot.rotatingSpeed);
        dra.sprite.setRotation(col.physicBody.getAngle() * MathUtils.radiansToDegrees);
    }
}
