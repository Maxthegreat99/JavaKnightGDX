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
        Filters.put(CollisionFiltersID.DEFAULT, CollisionFilter.defaultFilter);
        Filters.put(CollisionFiltersID.PLAYER, new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                CollisionRelationship rel = mappers.Collides.get((Entity) other.userData).relationship;
                if (rel.equals(CollisionRelationship.OBSTACLE) || rel.equals(CollisionRelationship.OUT_OF_BOUNDS))
                    return Response.slide;
                return null;
            }
        });
        Filters.put(CollisionFiltersID.BOUNCE, new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                return Response.bounce;
            }
        });

        Filters.put(CollisionFiltersID.BULLET, new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                CollisionRelationship rel = mappers.Collides.get((Entity) other.userData).relationship;

                if (rel.equals(CollisionRelationship.PLAYER) || rel.equals(CollisionRelationship.BULLET))
                    return Response.cross;

                return Response.slide;
            }
        });
    }

}
