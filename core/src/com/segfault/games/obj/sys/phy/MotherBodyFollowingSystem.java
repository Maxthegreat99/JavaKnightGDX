package com.segfault.games.obj.sys.phy;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.ChildBodyComponent;
import com.segfault.games.obj.comp.CollidesComponent;
import com.segfault.games.obj.ent.EntityManager;
import com.segfault.games.obj.ent.Mappers;
import com.segfault.games.obj.sys.SubSystem;

public class MotherBodyFollowingSystem implements SubSystem {

    private final Mappers mappers;
    private final EntityManager manager;
    public MotherBodyFollowingSystem(JavaKnight instance) {
        manager = instance.GetEntityManager();
        mappers = instance.GetEntityManager().GetMappers();
    }

    private final Vector2 vec = new Vector2();
    @Override
    public void processEntity(Entity entity, float interval, float accumulator) {
        ChildBodyComponent child = mappers.ChildBody.get(entity);
        Body motherBody = manager.GetMotherBodies().get(child.motherBodyKey);
        Body childBody = mappers.Collides.get(entity).physicBody;

        if (motherBody.getFixtureList().first().getShape().getType() == Shape.Type.Circle) {
            float radius = motherBody.getFixtureList().first().getShape().getRadius();
            childBody.setTransform(vec.set(motherBody.getPosition()).add(radius, radius), childBody.getAngle());

            return;
        }

        CollidesComponent col = (CollidesComponent) motherBody.getUserData();
        childBody.setTransform(vec.set(motherBody.getPosition()).add(col.width / 2, col.height / 2), childBody.getAngle());




    }
}
