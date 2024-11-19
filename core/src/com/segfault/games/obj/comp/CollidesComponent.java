package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.CollisionFiltersID;

/**
 * Component defining if an object has JBump based collisions,
 * also used for specific events on collision depending of
 * the components the entity has
 */

public class CollidesComponent extends Component {
    /*
     * Box2D physics body, check https://libgdx.com/wiki/extensions/physics/box2d for more info
     * on Box2D
     */
    public Body physicBody = null;
    /*
     *  body definition used to create our entity's physic body
     */
    public BodyDef bodyDefinition = new BodyDef();
    /**
     * collision relationship, defines what type of collision
     * this object is. is used by other collisions to define behaviour
     */
    public CollisionRelationship relationship = null;
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
     * ID of the collision filter currently being used, used for serialization
     */
    public CollisionFiltersID filterID = null;
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

    /**
     * Whether or not the collision has a collisionEvent component
     */
    public boolean hasCollisionEvent = false;

    @Override
    public void reset() {
        filter = CollisionFilter.defaultFilter;
        relationship = null;
        filterID = null;
        res = null;
        width = 0f;
        height = 0f;
        x = 0f;
        y = 0f;
        hasCollisionEvent = false;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.GetEntityManager().GetPhysicWorld().remove(physicItem);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CollidesComponent comp = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
        comp.physicItem.userData = ent;
        comp.relationship = relationship;
        comp.width = (Float.isNaN(width)) ? pol.z : width;
        comp.height = (Float.isNaN(height)) ? pol.w : height;
        comp.x = (Float.isNaN(x)) ? pol.x : x;
        comp.y = (Float.isNaN(y)) ? pol.y : y;
        comp.res = null;
        comp.filter = filter;
        comp.filterID = filterID;
        comp.hasCollisionEvent = false;
        instance.GetEntityManager().GetPhysicWorld().add(comp.physicItem, comp.x, comp.y, comp.width, comp.height);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(relationship.toString(), "relationship");
        json.writeField(filterID.toString(), "filter");
        json.writeField(width, "width");
        json.writeField(height, "height");
        json.writeField(x, "x");
        json.writeField(y, "y");
        json.writeField(hasCollisionEvent, "hasCollisionEvent");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        relationship = CollisionRelationship.valueOf(jsonValue.getString("relationship"));
        width = jsonValue.getFloat("width", Float.NaN);
        height = jsonValue.getFloat("height", Float.NaN);
        x = jsonValue.getFloat("x", Float.NaN);
        y = jsonValue.getFloat("y", Float.NaN);
        filterID = CollisionFiltersID.valueOf(jsonValue.getString("filter"));
        filter = instance.GetEntityManager().GetCollisionFilters().Filters.get(filterID);
        hasCollisionEvent = jsonValue.getBoolean("hasCollisionEvent");
    }


}
