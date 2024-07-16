package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.ObjectMap;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.segfault.games.obj.comp.CollisionRelationship;

/**
 * Holds all types of collision filters for the game in a class for quick referencing
 */
public class CollisionFilters {
    public final ObjectMap<CollisionFiltersID, CollisionFilter> Filters = new ObjectMap<>();

    public CollisionFilters(Mappers mappers) {
        Filters.get(CollisionFiltersID.DEFAULT, CollisionFilter.defaultFilter);
        Filters.put(CollisionFiltersID.PLAYER, new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                if (mappers.Collides.get((Entity) other.userData).relationship.equals(CollisionRelationship.OBSTACLE))
                    return Response.slide;
                return null;
            }
        });
    }

}
