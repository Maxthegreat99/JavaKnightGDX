package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class DecreasingAlplaComponent extends Component {
    public float alphaDecrease = 0.0f;
    public float comparator = 0.0f;
    @Override
    public void reset() {
        alphaDecrease = 0.0f;
        comparator = 0.0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        DecreasingAlplaComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.alphaDecrease = alphaDecrease;
        comp.comparator = comparator;
        return comp;
    }
}
