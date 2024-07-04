package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.segfault.games.JavaKnight;

/**
 * component for objects with that should be rendered,
 * this component is also used for manipulating with positions
 */

public class DrawableComponent extends Component {
    /**
     * sprite of the object, holds position info, alpha rotation etc...
     */
    public Sprite sprite = null;
    /**
     * alpha value of the component, changing this does nothing,
     * to set the alpha use the sprite field, when changing the
     * sprite field make sure to change this afterwards
     */
    public float alpha = 0.0f;
    /**
     * whether blending should be enabled or disabled when rendering
     * this objecct, if your object does not need alpha and has no
     * transparent pixels you should set this to false
     */
    public boolean blending = true;
    /**
     * the order in which the object should be rendered, the higer the value
     * the more likely the object wil be the last to be rendered
     */
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
