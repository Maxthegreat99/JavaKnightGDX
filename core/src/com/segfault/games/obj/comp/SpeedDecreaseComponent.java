package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class SpeedDecreaseComponent extends Component {
    public float decelerationValue = 0.0f;
    public float comparator = 0f;
    @Override
    public void reset() {
        decelerationValue = 0.0f;
        comparator = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        SpeedDecreaseComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.comparator = comparator;
        comp.decelerationValue = decelerationValue;
        return comp;
    }
}
