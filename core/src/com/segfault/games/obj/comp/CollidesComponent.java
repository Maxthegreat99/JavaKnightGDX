package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;

/**
 * Component defining if an object has JBump based collisions,
 * also used for specific events on collision depending of
 * the components the entity has
 */

public class CollidesComponent extends Component {
    /**
     * JBump Physic item of the object, the userData refers to the
     * entity
     */
    public Item<Entity> physicItem = null;
    /**
     * collision relationship, defines what type of collision
     * this object is. is used by other collisions to define behaviour
     */
    public CollisionRelationship collisionRelationShip = null;
    /**
     * collision filter, set to define if custom interaction, (sliding,
     * crossing etc..) should happen, whether you want custom interaction
     * over the default filter depends usually a block collision
     * (ex. an obstacle) will use a default filter and other collisions
     * will handle in their own filters whether they should touch/cross/etc..
     * the coolision.
     *
     */
    public CollisionFilter filter = CollisionFilter.defaultFilter;
    /**
     * result from last movement, constains overlapping collisions and more
     * null if the movement logic has not executed yet
     */
    public Response.Result res = null;
    /**
     * width of the collision, modifying this value wont do anything,
     * if you want to modify the width, use PhysicWorld instance
     * but make sure to update this field as it is used for cloning the
     * instance
     */
    public float width = 0f;
    /**
     * height of the collision, modifying this value wont do anything,
     * if you want to modify the height, use PhysicWorld instance
     * but make sure to update this field as it is used for cloning the
     * instance
     */
    public float height = 0f;

    /**
     * x position of the collision, modifying this value wont do anything,
     * if you want to modify the position, use PhysicWorld instance
     * but make sure to update this field as it is used for cloning the
     * instance
     */
    public float x = 0f;
    /**
     * y position of the collision, modifying this value wont do anything,
     * if you want to modify the position, use PhysicWorld instance
     * but make sure to update this field as it is used for cloning the
     * instance
     */
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
        instance.GetEntityManager().GetPhysicWorld().remove(physicItem);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        CollidesComponent comp = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
        comp.physicItem = new Item<>(ent);
        instance.GetEntityManager().GetPhysicWorld().add(comp.physicItem, x, y, width, height);
        comp.collisionRelationShip = collisionRelationShip;
        comp.width = width;
        comp.height = height;
        comp.x = x;
        comp.y = y;
        comp.res = null;
        comp.filter = filter;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonData) {
        collisionRelationShip = CollisionRelationship.valueOf(jsonData.getString("collisionRelationShip"));

    }
}
