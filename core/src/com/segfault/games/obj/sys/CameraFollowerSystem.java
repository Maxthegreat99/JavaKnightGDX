package com.segfault.games.obj.sys;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.Family;
import com.badlogic.ashley.systems.SortedIteratingSystem;
import com.badlogic.gdx.math.Vector2;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CameraFollowerComponent;
import com.segfault.games.obj.comp.DrawableComponent;
import com.segfault.games.obj.ent.Mappers;

import java.util.Comparator;

public class CameraFollowerSystem extends SortedIteratingSystem {

    private final Mappers mappers;
    private final JavaKnight instance;
    private final Vector2 current = new Vector2();
    private final Vector2 target = new Vector2();

    public CameraFollowerSystem(JavaKnight instance, int priority) {
        super(Family.all(CameraFollowerComponent.class).get(), new Comparator<Entity>() {
            @Override
            public int compare(Entity o1, Entity o2) {
                return instance.GetEntityManager().GetMappers().CameraFollower.get(o1).priority -
                       instance.GetEntityManager().GetMappers().CameraFollower.get(o2).priority;
            }


        });

        this.instance = instance;
        mappers = instance.GetEntityManager().GetMappers();
        this.priority = priority;
    }

    @Override
    protected void processEntity(Entity entity, float v) {
        CameraFollowerComponent cameraFollower = mappers.CameraFollower.get(entity);
        DrawableComponent drawable = mappers.Drawable.get(entity);

        cameraFollower.targetX = drawable.sprite.getX();
        cameraFollower.targetY = drawable.sprite.getY();

        if(cameraFollower.fresh) {
            cameraFollower.currentX = cameraFollower.targetX;
            cameraFollower.currentY = cameraFollower.targetY;

            instance.GetRenderer().CameraPos.set(cameraFollower.targetX, cameraFollower.targetY, 0);
            instance.GetRenderer().UpdateCamera = true;

            cameraFollower.fresh = false;
            return;
        }

        if (Float.compare(Vector2.dst2(cameraFollower.currentX, cameraFollower.currentY, cameraFollower.targetX, cameraFollower.targetY), 0f) == 0)
            return;

        current.set(cameraFollower.currentX, cameraFollower.currentY);
        target.set(cameraFollower.targetX, cameraFollower.targetY);

        instance.GetRenderer().CameraPos.set(current.lerp(target, cameraFollower.alpha), 0);

        cameraFollower.currentX = current.x;
        cameraFollower.currentY = current.y;
        instance.GetRenderer().UpdateCamera = true;
    }
}
