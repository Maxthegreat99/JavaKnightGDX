package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class BounceComponent extends Component {
    public int maxBounces = 0;
    public CollisionRelationship relationship = null;
    public int bounces = 0;
    @Override
    public void reset() {
        relationship = null;
        bounces = 0;
        maxBounces = 0;
    }

    @Override
    public void dispose(JavaKnight instance) {
        return;
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        BounceComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.maxBounces = maxBounces;
        comp.bounces = bounces;
        comp.relationship = relationship;
        return comp;
    }
}
