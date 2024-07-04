package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

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
     * the collision relationship needed for the life component
     * to be damaged, leave this value null if using a rectangle
     */

    public CollisionRelationship relationship = null;
    @Override
    public void reset() {
        damage = 0;
        target = null;
        relationship = null;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        DamageComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.relationship = relationship;
        comp.target = target;
        comp.damage = damage;
        return comp;
    }
}
