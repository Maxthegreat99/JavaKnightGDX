package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.util.indexT;

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
     * enum id of the sprite in the texture hashmap
     */
    public indexT spriteID = null;
    /**
     * alpha value of the component, changing this does nothing,
     * to set the alpha use the sprite field, when changing the
     * sprite field make sure to change this afterwards
     */
    public float alpha = 0.0f;
    /**
     * whether blending should be enabled or disabled when rendering
     * this object, if your object does not need alpha and has no
     * transparent pixels you should set this to false
     */
    public boolean blending = true;
    /**
     * the order in which the object should be rendered, the bigger the value
     * the more likely the object wil be the last to be rendered
     */
    public int order = 0;
    @Override
    public void reset() {
        sprite = null;
        spriteID = null;
        alpha = 0.0f;
        blending = true;
        order = 0;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component Clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        DrawableComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.sprite = new Sprite(sprite);
        comp.sprite.setPosition(pol.x, pol.y);
        comp.order = order;
        comp.alpha = alpha;
        comp.spriteID = spriteID;
        comp.blending = blending;
        return comp;
    }

    @Override
    public void write(Json json) {

        json.writeField(alpha, "alpha");
        json.writeField(blending, "blending");
        json.writeField(order, "order");
        json.writeField(spriteID.toString(), "sprite");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance) {
        alpha = jsonValue.getFloat("alpha");
        blending = jsonValue.getBoolean("blending");
        order = jsonValue.getInt("order");
        spriteID = indexT.valueOf(jsonValue.getString("sprite"));
        sprite = new Sprite(instance.GetAssetManager().GetTextures().get(spriteID));
        sprite.setAlpha(alpha);
    }


}
