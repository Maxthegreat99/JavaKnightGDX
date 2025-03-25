package com.segfault.games.obj.comp;

import box2dLight.ConeLight;
import box2dLight.DirectionalLight;
import box2dLight.Light;
import box2dLight.PointLight;
import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.JavaKnight;

/**
 * Objects that can emit light interacting with objects
 */

public class LightHolderComponent extends Component {

    /**
     * pool of lights
     */
    public static Pool<LightObject> LightPool = new Pool<LightObject>(10) {
        @Override
        protected LightObject newObject() {
            return new LightObject();
        }

        protected void reset(LightObject l) {
            l.lightType = LightTypes.POINT;
            l.color.set(1, 1, 1, 1);
            l.direction = 0f;
            l.rays = 0;
            l.distance = 0f;
            l.soft = false;
            l.softLength = 0f;
            l.isStatic = false;
            l.xRay = false;
            l.size = 0f;
            l.collisionFilter = 0;
            l.collisionFilterStrings.clear();
        }
    };

    /**
     * list of lights the object holds
     */
    public Array<LightObject> lights = new Array<>();


    @Override
    public void reset() {

    }

    @Override
    public void dispose(JavaKnight instance) {

        for (LightObject l : lights) {
            l.light.remove();
            l.light = null;
            instance.GetRenderer().GetLights().remove(l.light);
            LightPool.free(l);
        }

        lights.clear();
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        LightHolderComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        for (LightObject l : lights) {
            LightObject newLight = LightPool.obtain();

            newLight.lightType = l.lightType;
            newLight.isStatic = l.isStatic;
            newLight.xRay = l.xRay;
            newLight.soft = l.soft;
            newLight.softLength = l.softLength;
            newLight.color.set(l.color);
            newLight.rays = l.rays;
            newLight.distance = l.distance;
            newLight.direction = l.direction;
            newLight.collisionFilterStrings.addAll(l.collisionFilterStrings);
            newLight.collisionFilter = l.collisionFilter;
            newLight.relationship = l.relationship;

            CollidesComponent phy = instance.GetEntityManager().GetMappers().Collides.get(ent);

            switch (l.lightType) {
                case CONE:
                    newLight.light = new ConeLight(instance.GetRenderer().GetRayHandler(), l.rays, newLight.color, l.distance,
                            0, 0, l.direction * 360f, l.size);
                    ((ConeLight) newLight.light).attachToBody(phy.physicBody, 0, 0, l.direction * 360f);
                    break;
                case POINT:
                    newLight.light = new PointLight(
                            instance.GetRenderer().GetRayHandler(), l.rays, newLight.color, l.distance, 0f, 0f);
                    ((PointLight) newLight.light).attachToBody(phy.physicBody, 0, 0);

                    break;
                case DIRECTIONAL:
                    newLight.light = new DirectionalLight(
                            instance.GetRenderer().GetRayHandler(), l.rays, newLight.color, l.direction * 360f);
                    break;
            }

            newLight.light.setStaticLight(l.isStatic);
            newLight.light.setSoft(l.soft);
            newLight.light.setSoftnessLength(l.softLength);
            newLight.light.setXray(l.xRay);
            newLight.light.setContactFilter((short) l.relationship.flag, (short) 0, l.collisionFilter);

            comp.lights.add(newLight);
            instance.GetRenderer().GetLights().put(newLight.light, newLight);
        }
        return comp;
    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {

        for (JsonValue v : jsonValue.get("lights")) {
            LightObject l = LightPool.obtain();

            l.direction = v.getFloat("direction");
            l.distance = v.getFloat("distance");
            l.rays = v.getInt("rays");
            l.xRay = v.getBoolean("xRay");
            l.color.r = v.getFloat("R");
            l.color.g = v.getFloat("G");
            l.color.b = v.getFloat("B");
            l.color.a = v.getFloat("A");
            l.soft = v.getBoolean("soft");
            l.softLength = v.getFloat("softLength");
            l.size = v.getFloat("size");
            l.isStatic = v.getBoolean("isStatic");
            l.lightType = LightTypes.valueOf(v.getString("lightType"));
            l.relationship = CollisionRelationship.valueOf(v.getString("relationship"));

            String[] filters = v.get("filter").asStringArray();

            for (String f : filters) {
                l.collisionFilter |= (short) CollisionRelationship.valueOf(f).flag;
                l.collisionFilterStrings.add(f);
            }

            lights.add(l);

            if (!maploading)
                return;

            CollidesComponent phy = instance.GetEntityManager().GetMappers().Collides.get(ent);

            switch (l.lightType) {
                case CONE:
                    l.light = new ConeLight(instance.GetRenderer().GetRayHandler(), l.rays, l.color, l.distance,
                            0, 0, l.direction * 360f, l.size);
                    ((ConeLight) l.light).attachToBody(phy.physicBody, 0, 0, l.direction * 360f);
                    break;
                case POINT:
                    l.light = new PointLight(
                            instance.GetRenderer().GetRayHandler(), l.rays, l.color, l.distance, 0f, 0f);
                    ((PointLight) l.light).attachToBody(phy.physicBody, 0, 0);
                    break;
                case DIRECTIONAL:
                    l.light = new DirectionalLight(
                            instance.GetRenderer().GetRayHandler(), l.rays, l.color, l.direction * 360f);
                    break;
            }

            l.light.setStaticLight(l.isStatic);
            l.light.setSoft(l.soft);
            l.light.setSoftnessLength(l.softLength);
            l.light.setXray(l.xRay);
            l.light.setContactFilter((short) l.relationship.flag, (short) 0, l.collisionFilter);

            instance.GetRenderer().GetLights().put(l.light, l);

        }
    }

    @Override
    public void write(Json json) {
        int light = 1;
        json.writeObjectStart("lights");

        for (LightObject l : lights) {
            json.writeObjectStart("light" + light);
            json.writeField(l.direction, "direction");
            json.writeField(l.distance, "distance");
            json.writeField(l.rays, "rays");
            json.writeField(l.xRay, "xRay");
            json.writeField(l.color.r, "R");
            json.writeField(l.color.g, "G");
            json.writeField(l.color.b, "B");
            json.writeField(l.color.a, "A");
            json.writeField(l.soft, "soft");
            json.writeField(l.softLength, "softLength");
            json.writeField(l.isStatic, "isStatic");
            json.writeField(l.lightType.toString(), "lightType");
            json.writeField(l.size, "size");
            json.writeObjectEnd();

            light++;
        }

        json.writeObjectEnd();


    }


    public static class LightObject {
        /**
         * light type enum, used for initialization
         */
        public LightTypes lightType = LightTypes.POINT;
        /**
         * color of the light, alpha varies intensity
         */
        public Color color = new Color(1, 1, 1, 1);
        /**
         * direction of the light, not used for some light types
         */
        public float direction = 0f;
        /**
         * amount of rays the light produces
         */
        public int rays = 0;
        /**
         * distance the light travels
         */
        public float distance = 0f;
        /**
         * whether the light is softer at the tips
         */
        public boolean soft = false;
        /**
         * length of the softness at the tips
         */
        public float softLength = 0f;
        /**
         * makes light stay in place and only react with static objects
         */
        public boolean isStatic = false;
        /**
         * makes light go through objects
         */
        public boolean xRay = false;
        /**
         * size of the cone in degrees, only used for cone lights
         */
        public float size = 0f;
        /**
         * collision relationship of the light, used to determine which objects collide with the light
         */
        public CollisionRelationship relationship = CollisionRelationship.OBJECT;
        /**
         * light's collision filter, used to determine which objects collide with the light
         */
        public short collisionFilter = 0;
        /**
         * collisionFilter in string format
         */
        public Array<String> collisionFilterStrings = new Array<>();
        /**
         * light object instance
         */
        public Light light = null;
    }


}
