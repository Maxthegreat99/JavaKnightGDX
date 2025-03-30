package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;

/**
 * component for player movement based on acceleration built around taking gravity into account
 */

public class PlayerAcceleratedComponent extends Component {

    /**
     * cap velocity X where acceleration wont be applied
     */

    public float velcapX = 0f;


    /**
     * maximum acceleration applied from horizontal movement
     */

    public float maxAcceX = 0f;

    /**
     * initial acceleration for jumping movement
     */

    public float initAcceY = 0f;

    /**
     * whether the body has hit the ceiling
     */
    public boolean hitCeil = false;

    /**
     * whether the body is touching the ground
     */

    public boolean onGround = false;

    /**
     * horizontal acceleration alpha
     */
    public float acceAlphaX = 0f;

    /**
     * time taken to bring velocity to 0 on the ground
     */
    public float frictionTime = 0f;

    /**
     * how much in fraction horizontal control be in air compared to grounded
     */

    public float airControl = 0f;

    /**
     * time during which player can still accelerate in the air
     */
    public float acceTimeY = 0f;

    /**
     * elapsed time during which player is accelerating on the y axis
     */
    public float acceTimeYElapsed = 0f;


    /**
     * Whether the player is still applying vertical velocity
     */
    public boolean isJumping = false;

    /**
     * acceleration applied constantly after the initial jump
     */
    public float constantAcceY = 0f;

    @Override
    public void reset() {
        velcapX = 0f;
        maxAcceX = 0f;
        initAcceY = 0f;
        hitCeil = false;
        onGround = false;
        acceAlphaX = 0;
        frictionTime = 0f;
        airControl = 0f;
        acceTimeY = 0f;
        acceTimeYElapsed = 0f;
        isJumping = false;
        constantAcceY = 0f;
    }

    @Override
    public void dispose(JavaKnight instance) {

    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PlayerAcceleratedComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.velcapX = velcapX;
        comp.maxAcceX = maxAcceX;
        comp.initAcceY = initAcceY;
        comp.acceAlphaX = acceAlphaX;
        comp.frictionTime = frictionTime;
        comp.airControl = airControl;
        comp.acceTimeY = acceTimeY;
        comp.constantAcceY = constantAcceY;
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        velcapX = jsonValue.getFloat("velcapX");
        maxAcceX = jsonValue.getFloat("maxAcceX");
        initAcceY = jsonValue.getFloat("initAcceY");
        acceAlphaX = jsonValue.getFloat("acceAlphaX");
        frictionTime = jsonValue.getFloat("frictionTime");
        airControl = jsonValue.getFloat("airControl");
        acceTimeY = jsonValue.getFloat("acceTimeY");
        constantAcceY = jsonValue.getFloat("constantAcceY");
    }

    @Override
    public void write(Json json) {
        json.writeField(velcapX, "velcapX");
        json.writeField(maxAcceX, "maxAcceX");
        json.writeField(initAcceY, "initAcceY");
        json.writeField(acceAlphaX, "acceAlphaX");
        json.writeField(frictionTime, "frictionTime");
        json.writeField(airControl, "airControl");
        json.writeField(acceTimeY, "acceTimeY");
        json.writeField(constantAcceY, "constantAcceY");
    }

}
