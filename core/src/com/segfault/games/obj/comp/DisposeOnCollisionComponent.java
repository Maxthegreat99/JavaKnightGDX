package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.ent.ParticlesCreator;

/**
 * component disposing the object whenever they hit
 * the specified collision
 */

public class DisposeOnCollisionComponent extends Component {
    /**
     * TODO : IMPLEMENT PARTICLES
     * particle creator
     */
    public ParticlesCreator particles = null;
    /**
     * CollisionRelationship needed to be collided with to dispose
     * the object, kept null if using a rectangle
     */
    public CollisionRelationship relationship = null;
    /**
     * Rectangle to collide with to dispose the object,
     * kept null if using JBump collision
     */
    public Rec rectangle = null;
    /**
     * set this value if using a rectangle,
     * the range to the square to check
     * if the specified rectangle is colliding.
     */
    public float checkRange2 = 0f;
    @Override
    public void reset() {
        particles = null;
        rectangle = null;
        relationship = null;
        checkRange2 = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        DisposeOnCollisionComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.particles = particles;
        comp.rectangle = rectangle;
        comp.checkRange2 = checkRange2;
        comp.relationship = relationship;
        return comp;
    }

    @Override
    public void write(Json json) {
        json.writeFields(this);
    }

    @Override
    public void read(Json json, JsonValue jsonValue) {
        checkRange2 = jsonValue.getFloat("checkRange2");
    }
}
