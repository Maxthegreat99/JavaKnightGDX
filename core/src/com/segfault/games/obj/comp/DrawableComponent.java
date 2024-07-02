package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.segfault.games.JavaKnight;

public class DrawableComponent extends Component {
    public Sprite sprite = null;  // Image of the object
    public float alpha = 0.0f;
    public boolean blending = true;
    public int order = 0;
    @Override
    public void reset() {
        sprite = null;
        alpha = 0.0f;
        blending = true;
        order = 0;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent) {
        DrawableComponent comp = instance.PooledECS.createComponent(this.getClass());
        comp.sprite = new Sprite(sprite);
        comp.order = order;
        comp.alpha = alpha;
        comp.blending = blending;
        return comp;
    }
}
