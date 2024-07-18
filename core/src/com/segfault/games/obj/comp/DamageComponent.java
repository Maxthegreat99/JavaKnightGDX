package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.ent.TargettingMethodID;

/**
 * Component used for dealing damage to a target life component,
 * this component can use either a collides or rectangle to check for collisions
 */
public class DamageComponent extends Component {
    /**
     * the value of the damage to be dealt
     */
    public int damage = 0;
    /**
     * this value should be set if the target or collision
     * in question does not have a life component, this value
     * should always be set if using a rectangle
     */
    public LifeComponent target = null;
    /**
     * the way to get the LifeComponent target
     */
    public TargettingMethodID targetMethod = null;
    /**
     * the collision relationship needed for the life component
     * to be damaged, leave this value null if using a rectangle
     */
    public CollisionRelationship relationship = null;
    @Override
    public void reset() {
        damage = 0;
        target = null;
        relationship = null;
        targetMethod = null;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        DamageComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.relationship = relationship;
        comp.damage = damage;
        comp.targetMethod = targetMethod;
        comp.target = (LifeComponent) instance.GetEntityManager().GetTargetGetter().GetTarget(targetMethod, LifeComponent.class);
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeField(damage, "damage");
        if (relationship != null)
           json.writeField(relationship, "relationship");
        json.writeField(targetMethod.toString(), "targetMethod");

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        damage = jsonValue.getInt("damage");
        if (jsonValue.has("relationship"))
            relationship = CollisionRelationship.valueOf(jsonValue.getString("relationship"));
        targetMethod = TargettingMethodID.valueOf(jsonValue.getString("targetMethod"));
    }

}
