package com.segfault.games.obj.comp;


import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class CooldownComponent extends Component {
    public float cd = 0.0f;
    public float initialCd = 0.0f;
    public boolean automated = false;
    public boolean activate = false;
    @Override
    public void reset() {
        cd = 0.0f;
        initialCd = 0.0f;
        activate = false;
        automated = false;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        CooldownComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.cd = cd;
        comp.initialCd = initialCd;
        comp.automated = automated;
        comp.activate = activate;
        return comp;
    }
}
