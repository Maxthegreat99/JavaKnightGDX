package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;

public class CollidesComponent extends Component {
    public Item<Entity> physicItem = null;
    public CollisionRelationship collisionRelationShip = null;
    public CollisionFilter filter = CollisionFilter.defaultFilter;
    public Response.Result res = null;
    public float width = 0f;
    public float height = 0f;
    public float x = 0f;
    public float y = 0f;

    @Override
    public void reset() {
        filter = CollisionFilter.defaultFilter;
        collisionRelationShip = null;
        physicItem = null;
        res = null;
        width = 0f;
        height = 0f;
        x = 0f;
        y = 0f;

    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.PhysicWorld.remove(physicItem);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        CollidesComponent comp = instance.PooledECS.createComponent(CollidesComponent.class);
        comp.physicItem = new Item<>(ent);
        instance.PhysicWorld.add(comp.physicItem, x, y, width, height);
        comp.collisionRelationShip = collisionRelationShip;
        comp.width = width;
        comp.height = height;
        comp.x = x;
        comp.y = y;
        comp.res = null;
        comp.filter = filter;
        return comp;
    }
}
