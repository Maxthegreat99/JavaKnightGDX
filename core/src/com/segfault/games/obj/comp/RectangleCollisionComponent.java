package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;

public class RectangleCollisionComponent extends Component {
    public Rec targetRectangle = null;
    public float checkRange = 0f;
    @Override
    public void reset() {
        targetRectangle = null;
        checkRange = 0f;

    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        RectangleCollisionComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.checkRange = checkRange;
        comp.targetRectangle = targetRectangle;
        return comp;
    }
}
