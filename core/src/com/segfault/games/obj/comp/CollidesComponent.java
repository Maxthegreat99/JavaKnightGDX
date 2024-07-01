package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Component;
import com.badlogic.gdx.utils.Pool;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;

public class CollidesComponent implements Component, Pool.Poolable {
    public Item<CollidesComponent> physicItem = null;
    public CollisionRelationship collisionRelationShip = null;
    public CollisionFilter filter = CollisionFilter.defaultFilter;
    public Response.Result res = null;
    @Override
    public void reset() {
        filter = CollisionFilter.defaultFilter;
        collisionRelationShip = null;
        physicItem = null;
        res = null;
    }
}
