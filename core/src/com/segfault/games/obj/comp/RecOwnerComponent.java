package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;

public class RecOwnerComponent extends Component {
    public Rec rectangle = null;
    @Override
    public void reset() {
        rectangle = null;
    }

    @Override
    public void dispose(JavaKnight instance) {
        instance.Rectangles.removeValue(rectangle, true);
    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        RecOwnerComponent comp = instance.PooledECS.createComponent(RecOwnerComponent.class);
        comp.rectangle = new Rec(rectangle.X, rectangle.Y, rectangle.Width, rectangle.Height);
        if (rectangle.angle != 0) comp.rectangle.Rotate(rectangle.angle, rectangle.X, rectangle.Y);
        instance.Rectangles.add(comp.rectangle);
        return comp;
    }
}
