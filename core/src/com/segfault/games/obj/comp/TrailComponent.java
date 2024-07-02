package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.segfault.games.JavaKnight;

public class TrailComponent extends Component {
    public float trailCooldown = 0f;
    public float trailInitialCooldown = 0f;
    public float trailIninitalAlpha = 0f;
    public float trailAlphaDecrease = 0f;
    public float alphaComparator = 0f;
    @Override
    public void reset() {
        trailCooldown = 0f;
        trailInitialCooldown = 0f;
        trailIninitalAlpha = 0f;
        trailAlphaDecrease = 0f;
        alphaComparator = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        TrailComponent comp = instance.PooledECS.createComponent(TrailComponent.class);
        comp.trailCooldown = trailCooldown;
        comp.trailInitialCooldown = trailInitialCooldown;
        comp.trailIninitalAlpha = trailIninitalAlpha;
        comp.trailAlphaDecrease = trailAlphaDecrease;
        comp.alphaComparator = alphaComparator;
        return comp;
    }
}
