package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;

/**
 * component defining that the entity's rectangle may
 * collide with a target rectangle for specific events
 */
public class RectangleCollisionComponent extends Component {
    /**
     * the target rectangle trigering the event
     */
    public Rec targetRectangle = null;
    /**
     * range squared that the target rectangle should be in before checking for collision
     */
    public float checkRange2 = 0f;
    @Override
    public void reset() {
        targetRectangle = null;
        checkRange2 = 0f;

    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        RectangleCollisionComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.checkRange2 = checkRange2;
        comp.targetRectangle = targetRectangle;
        return comp;
    }
}
