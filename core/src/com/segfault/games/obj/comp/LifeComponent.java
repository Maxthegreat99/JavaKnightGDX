package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class LifeComponent extends Component {
    public int life = 0;
    public boolean tookHit = false;
    @Override
    public void reset() {
        life = 0;
        tookHit = false;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        LifeComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.tookHit = tookHit;
        comp.life = life;
        return comp;
    }
}
