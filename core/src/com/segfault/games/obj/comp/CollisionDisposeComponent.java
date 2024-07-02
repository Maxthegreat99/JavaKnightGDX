package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.ParticlesCreator;

public class CollisionDisposeComponent extends Component {
    public ParticlesCreator particles = null;
    public CollisionRelationship relationship = null;
    public Rec rectangle = null;
    public float checkRange = 0f;
    @Override
    public void reset() {
        particles = null;
        rectangle = null;
        relationship = null;
        checkRange = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.Rectangles.removeValue(rectangle, true);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        CollisionDisposeComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.particles = particles;
        comp.rectangle = rectangle;
        comp.checkRange = checkRange;
        comp.relationship = relationship;
        return comp;
    }
}
