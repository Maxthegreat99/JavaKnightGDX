package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.ent.EntityManager;

/**
 * Component defining if an object has JBump based collisions,
 * also used for specific events on collision depending of
 * the components the entity has
 */

public class CollidesComponent extends Component {
    /**
     * Box2D physics body, check <a href="https://libgdx.com/wiki/extensions/physics/box2d">the libGDX box2d docs</a> for more info
     * on Box2D
     */
    public Body physicBody = null;

    /**
     * Box2d fixture, holds the body's shape and its physical properties
     */
    public Fixture fixture = null;


    /**
     * collision relationship, defines what type of collision
     * this object is. is used by other collisions to define behaviour
     */
    public CollisionRelationship relationship = null;

    /**
     * This value is recommended to be modified when changing
     * this object's object space properties but not nessesary
     * it is mainly used to help with cloning entities but systems
     * may still used its value
     */
    public float width = 0f;

    /**
     * This value is recommended to be modified when changing
     * this object's object space properties but not nessesary
     * it is mainly used to help with cloning entities but systems
     * may still used it
     */
    public float height = 0f;

    /**
     * This value is recommended to be modified when changing
     * this object's object space properties but not nessesary
     * it is mainly used to help with cloning entities but systems
     * may still used it, make sure this value is updated before
     * conducting any cloning activities, otherwise use the value
     * using the body's field
     */
    public float x = 0f;

    /**
     * This value is recommended to be modified when changing
     * this object's object space properties but not nessesary
     * it is mainly used to help with cloning entities but systems
     * may still used it, make sure this value is updated before
     * conducting any cloning activities, otherwise use the value
     * using the body's field
     */
    public float y = 0f;

    /**
     * the object's elasticity / bounciness, this represents the initial value
     * and may not be representative of the real value gotten from the body itself
     * make sure this value is updated before conducting any cloning activities,
     * value ranges between 0-1f
     */
    public float restitution = 0f;

    /**
     * density in kg/m^2, this determines how the object reacts to forces
     * eg. make a small sized ball feel more like a denser metal ball rather
     * than an air balloon, value is representative of the initial
     * value and may not be updated to the body's real value,
     * make sure to update this value before cloning,
     * value ranges between 0-1f
     */
    public float density = 0f;


    /**
     * determines how slippery the object is, this value is representative
     * of the initial value and may not be updated to the body's real value,
     * make sure to update this value before cloning, value ranges between 0-1f
     */
    public float friction = 0f;

    /**
     * determines the type of body this object is within the physics engine
     */
    public BodyDef.BodyType bodyType = BodyDef.BodyType.StaticBody;


    /**
     * Whether or not the collision has a collisionEvent component,
     * used by the engine to save time looking up components
     */
    public boolean hasCollisionEvent = false;

    /**
     * the type of shape this physics object has
     */

    public EntityManager.Shapes shape = EntityManager.Shapes.RECTANGLE;

    /**
     * The entity the component is referred to, used by the engine to
     * easily look up components from collisions' userData
     */
    public Entity entity = null;

    /**
     * whether or not the rotation of the body is fixed
     */
    public boolean rotationFixed = true;

    /**
     * air resistance, how fast it slows down to while moving
     */
    public float linearDamping = 0f;

    /**
     * defines which collisions the fixture is allowed to collide with
     */
    public short collisionFilter = 0;

    /**
     * string array copy of the collisionFilters for easier writing to json
     */
    public Array<String> collisionFilterStrings = new Array<>();

    /**
     * amount of collisions the body is colliding with
     */
    public int collisionCount = 0;

    /**
     * x position last update
     */
    public float lastX = 0f;

    /**
     * y position last update
     */
    public float lastY = 0f;

    /**
     *
     */
    public boolean isSensor = false;

    @Override
    public void reset() {

        physicBody = null;
        fixture = null;
        restitution = 0f;
        density = 0f;
        friction = 0f;
        bodyType = BodyDef.BodyType.StaticBody;
        entity = null;
        linearDamping = 0f;
        rotationFixed = true;
        collisionFilter = 0;
        collisionFilterStrings.clear();
        collisionCount = 0;
        lastX = 0f;
        lastY = 0f;

        isSensor = false;
        relationship = null;
        width = 0f;
        height = 0f;
        x = 0f;
        y = 0f;
        hasCollisionEvent = false;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.GetEntityManager().GetPhysicWorld().destroyBody(physicBody);
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        CollidesComponent comp = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
        comp.relationship = relationship;
        comp.width = (Float.isNaN(width)) ? pol.z / Renderer.PIXEL_TO_METERS: width;
        comp.height = (Float.isNaN(height)) ? pol.w / Renderer.PIXEL_TO_METERS: height;
        comp.x = (Float.isNaN(x)) ? pol.x / Renderer.PIXEL_TO_METERS: x;
        comp.y = (Float.isNaN(y)) ? pol.y / Renderer.PIXEL_TO_METERS: y;
        comp.hasCollisionEvent = hasCollisionEvent;
        comp.shape = shape;
        comp.restitution = restitution;
        comp.friction = friction;
        comp.density = density;
        comp.bodyType = bodyType;
        comp.rotationFixed = rotationFixed;
        comp.linearDamping = linearDamping;
        comp.collisionFilter = collisionFilter;
        comp.collisionFilterStrings.addAll(collisionFilterStrings);

        BodyDef bdyDef = instance.GetEntityManager().GetBodyDef();
        FixtureDef fixDef = instance.GetEntityManager().GetFixtureDef();

        bdyDef.position.set(comp.x + comp.width / 2, comp.y + comp.height / 2);
        bdyDef.type = bodyType;
        bdyDef.linearDamping = linearDamping;
        bdyDef.fixedRotation = rotationFixed;

        fixDef.friction = friction;
        fixDef.density = density;
        fixDef.restitution = restitution;
        fixDef.isSensor = isSensor;

        fixDef.shape = instance.GetEntityManager().GetShape(shape.ordinal());

        if (shape.equals(EntityManager.Shapes.CIRCLE))
            fixDef.shape.setRadius(comp.width / 2);
        else
            ((PolygonShape)fixDef.shape).setAsBox(comp.width / 2, comp.height / 2);

        fixDef.filter.categoryBits = (short) relationship.flag;
        fixDef.filter.maskBits = collisionFilter;

        comp.physicBody = instance.GetEntityManager().GetPhysicWorld().createBody(bdyDef);
        comp.fixture = comp.physicBody.createFixture(fixDef);

        instance.GetEntityManager().ResetDefinitions();

        comp.physicBody.setUserData(comp);
        comp.fixture.setUserData(comp);
        comp.entity = ent;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(relationship.toString(), "relationship");
        json.writeField(width, "width");
        json.writeField(height, "height");
        json.writeField(x, "x");
        json.writeField(y, "y");
        json.writeField(hasCollisionEvent, "hasCollisionEvent");

        json.writeField(shape.toString(), "shape");

        json.writeField(restitution, "restitution");
        json.writeField(density, "density");
        json.writeField(friction, "friction");
        json.writeField(isSensor, "isSensor");

        json.writeField(bodyType.toString(), "bodyType");
        json.writeField(rotationFixed, "rotationFixed");
        json.writeField(linearDamping, "linearDamping");

        json.writeArrayStart("filter");

        for (String s : collisionFilterStrings) json.writeValue(s);

        json.writeArrayEnd();

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        relationship = CollisionRelationship.valueOf(jsonValue.getString("relationship"));
        String shape = jsonValue.getString("shape");
        this.shape = EntityManager.Shapes.valueOf(shape);
        width = jsonValue.getFloat("width", Float.NaN) / Renderer.PIXEL_TO_METERS;
        height = jsonValue.getFloat("height", Float.NaN) / Renderer.PIXEL_TO_METERS;
        x = jsonValue.getFloat("x", Float.NaN) / Renderer.PIXEL_TO_METERS;
        y = jsonValue.getFloat("y", Float.NaN) / Renderer.PIXEL_TO_METERS;

        restitution = jsonValue.getFloat("restitution");
        friction = jsonValue.getFloat("friction");
        density = jsonValue.getFloat("density");
        isSensor = jsonValue.getBoolean("isSensor", false);


        bodyType = BodyDef.BodyType.valueOf(jsonValue.getString("bodyType"));
        hasCollisionEvent = jsonValue.getBoolean("hasCollisionEvent");
        rotationFixed = jsonValue.getBoolean("rotationFixed", true);
        linearDamping = jsonValue.getFloat("linearDamping", 5f);

        String[] filters = jsonValue.get("filter").asStringArray();

        for (String f : filters) {
            collisionFilter |= (short) CollisionRelationship.valueOf(f).flag;
            collisionFilterStrings.add(f);
        }
    }


}
