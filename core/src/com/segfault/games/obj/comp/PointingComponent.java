package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class PointingComponent extends Component {
    public boolean cursor = false;
    public DrawableComponent target = null;

    @Override
    public void reset() {
        cursor = false;
        target = null;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        PointingComponent comp = instance.PooledECS.createComponent(PointingComponent.class);
        comp.cursor = cursor;
        comp.target = target;
        return comp;
    }
}
