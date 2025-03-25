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
    public Sprite sprite = new Sprite();
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
     * the order in which the object should be rendered, the bigger the value
     * the more likely the object wil be the last to be rendered
     */
    public int order = 0;

    /**
     * the shader the drawable is currently using, used to indentify which shader
     * the drawable should apply and order each drawable correctly to save having to
     * over flush the buffer
     */
    public Shaders shader = Shaders.NORMAL;

    @Override
    public void reset() {
        spriteID = null;
        order = 0;
        alpha = 0.0f;
        shader = Shaders.NORMAL;
    }


    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        DrawableComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());
        comp.sprite.set(sprite);
        comp.sprite.setPosition(pol.x, pol.y);
        comp.order = order;
        comp.alpha = alpha;
        comp.spriteID = spriteID;
        comp.shader = shader;
        return comp;
    }

    @Override
    public void write(Json json) {

        json.writeField(alpha, "alpha");
        json.writeField(spriteID.toString(), "sprite");
        json.writeField(order, "order");
        json.writeField(shader.toString(), "shader");
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        alpha = jsonValue.getFloat("alpha");
        spriteID = indexT.valueOf(jsonValue.getString("sprite"));
        sprite = new Sprite(instance.GetAssetManager().GetTextures().get(spriteID));
        order = jsonValue.getInt("order");
        sprite.setAlpha(alpha);
        shader = Shaders.valueOf(jsonValue.getString("shader", "NORMAL"));
    }


}
