package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class DamageComponent extends Component {
    public int damage = 0;
    public LifeComponent target = null;

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
