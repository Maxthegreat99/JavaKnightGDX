package com.segfault.games.obj.comp;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.Pool;
import com.segfault.games.JavaKnight;
import com.segfault.games.util.indexT;

/**
 * component for the fire particle effect of the player
 */
public class PlayerParticlesComponent extends Component{

    /**
     * minimum random number for the particle size
     */
    public float sizeRandomMin = 0f;

    /**
     * maximum random number for the particle
     */

    public float sizeRandomMax = 0f;

    /**
     *  starting color of the color over time gradient of particles
     */
    public Color startColor = new Color();

    /**
     *  ending color of the color over time gradient
     */
    public Color endColor = new Color();

    /**
     *  minimum random angle for the particles
     */
    public float angleRandomMin = 0f;

    /**
     * maximum random angle for the particles
     */
    public float angleRandomMax = 0f;

    /**
     * minimum random lifetime for the particles
     */
    public float lifetimeRandomMin = 0f;

    /**
     * maximum random lifetime for the particles
     */
    public float lifetimeRandomMax = 0f;

    /**
     * minimum random velocity x for the particles
     */
    public float velXRandomMin = 0f;

    /**
     * maximum random velocity x for the particles
     */
    public float velXRandomMax = 0f;

    /**
     * minimum random velocity y for the particles
     */
    public float velYRandomMin = 0f;

    /**
     * maximum random velocity y for the particles
     */
    public float velYRandomMax = 0f;

    /**
     * minimum random voronoi scale for the particles
     */
    public float voronoiScaleMin = 0f;

    /**
     * maximum random voronoi scale for the particles
     */
    public float voronoiScaleMax = 0f;

    /**
     * minimum random voronoi speed for the particles
     */
    public float voronoiSpeedMin = 0f;

    /**
     * maximum random voronoi speed for the particles
     */
    public float voronoiSpeedMax = 0f;

    /**
     * default sprite for the particles
     */

    public Sprite sprite = new Sprite();

    /**
     * texture id enum
     */
    public indexT texID = null;

    /**
     * maximum amount of particles allowed
     */
    public int maxParts = 0;

    /**
     * chance that a particle spawns each frame
     */
    public float partSpawnChance = 0f;

    /**
     * list of points for the size lifetime curve
     * point x, point y, tan x, tan y
     */
    public Array<Vector4> sizeCurve = new Array<>();

    /**
     * list of points for the alphaClip lifetime curve
     * point x, point y, tan x, tan y
     */
    public Array<Vector4> alphaClipCurve = new Array<>();

    /**
     * fresnel power for the shader
     */
    public float fresnelPower = 0f;

    /**
     * particle pool
     */

    public Pool<FireParticle> particlePool = new Pool<FireParticle>(0) {


        @Override
        protected FireParticle newObject() {
            FireParticle p = new FireParticle();

            p.angle = MathUtils.random(angleRandomMin, angleRandomMax);
            p.lifeTime = MathUtils.random(lifetimeRandomMin, lifetimeRandomMax);
            p.sprite.set(sprite);
            p.sizeX = p.sizeY = MathUtils.random(sizeRandomMin, sizeRandomMax);
            p.velX = MathUtils.random(velXRandomMin, velXRandomMax);
            p.velY = MathUtils.random(velYRandomMin, velYRandomMax);
            p.voronoiScale = MathUtils.random(voronoiScaleMin, voronoiScaleMax);
            p.voronoiSpeed = MathUtils.random(voronoiSpeedMin, voronoiSpeedMax);
            p.elapsedLifetime = 0f;
            p.sprite.setRotation(p.angle);
            return p;
        }

        @Override
        protected void reset(FireParticle p) {
            p.angle = MathUtils.random(angleRandomMin, angleRandomMax);
            p.lifeTime = MathUtils.random(lifetimeRandomMin, lifetimeRandomMax);
            p.sizeX = p.sizeY = MathUtils.random(sizeRandomMin, sizeRandomMax);
            p.velX = MathUtils.random(velXRandomMin, velXRandomMax);
            p.velY = MathUtils.random(velYRandomMin, velYRandomMax);
            p.voronoiScale = MathUtils.random(voronoiScaleMin, voronoiScaleMax);
            p.voronoiSpeed = MathUtils.random(voronoiSpeedMin, voronoiSpeedMax);
            p.elapsedLifetime = 0f;
            p.sprite.setRotation(p.angle);
        }


    };

    /**
     *  active particle list
     */
    public Array<FireParticle> activeParts = new Array<FireParticle>();



    @Override
    public void dispose(JavaKnight instance) {
        for (FireParticle p : activeParts) {
            particlePool.free(p);
            particlePool.clear();
        }

        activeParts.clear();
    }

    @Override
    public Component clone(JavaKnight instance, Entity ent, Vector4 pol, JsonValue properties) {
        PlayerParticlesComponent comp = instance.GetEntityManager().GetEngine().createComponent(this.getClass());

        comp.sizeRandomMin = this.sizeRandomMin;
        comp.sizeRandomMax = this.sizeRandomMax;
        comp.startColor.set(this.startColor);
        comp.endColor.set(this.endColor);
        comp.angleRandomMin = this.angleRandomMin;
        comp.angleRandomMax = this.angleRandomMax;
        comp.lifetimeRandomMin = this.lifetimeRandomMin;
        comp.lifetimeRandomMax = this.lifetimeRandomMax;
        comp.velXRandomMin = this.velXRandomMin;
        comp.velXRandomMax = this.velXRandomMax;
        comp.velYRandomMin = this.velYRandomMin;
        comp.velYRandomMax = this.velYRandomMax;
        comp.voronoiScaleMin = this.voronoiScaleMin;
        comp.voronoiScaleMax = this.voronoiScaleMax;
        comp.voronoiSpeedMin = this.voronoiSpeedMin;
        comp.voronoiSpeedMax = this.voronoiSpeedMax;
        comp.maxParts = maxParts;
        comp.partSpawnChance = partSpawnChance;
        comp.sprite.set(this.sprite);
        comp.fresnelPower = fresnelPower;

        comp.alphaClipCurve.addAll(alphaClipCurve);
        comp.sizeCurve.addAll(sizeCurve);

        return comp;

    }

    @Override
    public void read(JsonValue jsonValue, JavaKnight instance, boolean maploading, Entity ent) {
        sizeRandomMin = jsonValue.getFloat("sizeRandomMin");
        sizeRandomMax = jsonValue.getFloat("sizeRandomMax");
        startColor.set(jsonValue.getFloat("startColorR"), jsonValue.getFloat("startColorG"), jsonValue.getFloat("startColorB"), jsonValue.getFloat("startColorA"));
        endColor.set(jsonValue.getFloat("endColorR"), jsonValue.getFloat("endColorG"), jsonValue.getFloat("endColorB"), jsonValue.getFloat("endColorA"));

        angleRandomMin = jsonValue.getFloat("angleRandomMin");
        angleRandomMax = jsonValue.getFloat("angleRandomMax");
        lifetimeRandomMin = jsonValue.getFloat("lifetimeRandomMin");
        lifetimeRandomMax = jsonValue.getFloat("lifetimeRandomMax");
        velXRandomMin = jsonValue.getFloat("velXRandomMin");
        velXRandomMax = jsonValue.getFloat("velXRandomMax");
        velYRandomMin = jsonValue.getFloat("velYRandomMin");
        velYRandomMax = jsonValue.getFloat("velYRandomMax");
        voronoiScaleMin = jsonValue.getFloat("voronoiScaleMin");
        voronoiScaleMax = jsonValue.getFloat("voronoiScaleMax");
        voronoiSpeedMin = jsonValue.getFloat("voronoiSpeedMin");
        voronoiSpeedMax = jsonValue.getFloat("voronoiSpeedMax");
        fresnelPower = jsonValue.getFloat("fresnelPower");
        partSpawnChance = jsonValue.getFloat("partSpawnChance");
        maxParts = jsonValue.getInt("maxParts");

        JsonValue sizeArr = jsonValue.get("sizeCurve");
        if (sizeArr != null) {
            for (JsonValue v : sizeArr) {
                sizeCurve.add(new Vector4(v.getFloat("x"), v.getFloat("y"), v.getFloat("tanX"), v.getFloat("tanY")));
            }
        }

        alphaClipCurve.clear();
        JsonValue alphaArr = jsonValue.get("alphaClipCurve");
        if (alphaArr != null) {
            for (JsonValue v : alphaArr) {
                alphaClipCurve.add(new Vector4(v.getFloat("x"), v.getFloat("y"), v.getFloat("tanX"), v.getFloat("tanY")));
            }
        }

        sprite = new Sprite(instance.GetAssetManager().GetTextures().get(indexT.valueOf(jsonValue.getString("texID"))));

    }

    @Override
    public void write(Json json) {
        json.writeValue("sizeRandomMin", sizeRandomMin);
        json.writeValue("sizeRandomMax", sizeRandomMax);
        json.writeValue("startColorR", startColor.r);
        json.writeValue("startColorG", startColor.g);
        json.writeValue("startColorB", startColor.b);
        json.writeValue("startColorA", startColor.a);

        json.writeValue("endColorR", endColor.r);
        json.writeValue("endColorG", endColor.g);
        json.writeValue("endColorB", endColor.b);
        json.writeValue("endColorA", endColor.a);

        json.writeValue("angleRandomMin", angleRandomMin);
        json.writeValue("angleRandomMax", angleRandomMax);
        json.writeValue("lifetimeRandomMin", lifetimeRandomMin);
        json.writeValue("lifetimeRandomMax", lifetimeRandomMax);
        json.writeValue("velXRandomMin", velXRandomMin);
        json.writeValue("velXRandomMax", velXRandomMax);
        json.writeValue("velYRandomMin", velYRandomMin);
        json.writeValue("velYRandomMax", velYRandomMax);
        json.writeValue("voronoiScaleMin", voronoiScaleMin);
        json.writeValue("voronoiScaleMax", voronoiScaleMax);
        json.writeValue("voronoiSpeedMin", voronoiSpeedMin);
        json.writeValue("voronoiSpeedMax", voronoiSpeedMax);
        json.writeValue("texID", texID.toString());

        json.writeValue("maxParts", maxParts);
        json.writeValue("partSpawnChance", partSpawnChance);
        json.writeValue("fresnelPower", fresnelPower);


        json.writeArrayStart("sizeCurve");
        for (Vector4 v : sizeCurve) {
            json.writeObjectStart();
            json.writeValue("x", v.x);
            json.writeValue("y", v.y);
            json.writeValue("tanX", v.z);
            json.writeValue("tanY", v.w);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

        json.writeArrayStart("alphaClipCurve");
        for (Vector4 v : alphaClipCurve) {
            json.writeObjectStart();
            json.writeValue("x", v.x);
            json.writeValue("y", v.y);
            json.writeValue("tanX", v.z);
            json.writeValue("tanY", v.w);
            json.writeObjectEnd();
        }
        json.writeArrayEnd();

    }

    @Override
    public void reset() {
        sizeRandomMin = 0f;
        sizeRandomMax = 0f;
        startColor.set(Color.WHITE);
        endColor.set(Color.WHITE);
        angleRandomMin = 0f;
        angleRandomMax = 0f;
        lifetimeRandomMin = 0f;
        lifetimeRandomMax = 0f;
        velXRandomMin = 0f;
        velXRandomMax = 0f;
        velYRandomMin = 0f;
        velYRandomMax = 0f;
        voronoiScaleMin = 0f;
        voronoiScaleMax = 0f;
        voronoiSpeedMin = 0f;
        voronoiSpeedMax = 0f;
        maxParts = 0;
        partSpawnChance = 0f;
        fresnelPower = 0f;


        sizeCurve.clear();
        alphaClipCurve.clear();
    }


    public class FireParticle {
        public Sprite sprite = new Sprite();
        public float velX = 0f;
        public float velY = 0f;
        public float sizeX = 0f;
        public float sizeY = 0f;
        public float angle = 0f;
        public float voronoiScale = 0f;
        public float voronoiSpeed = 0f;
        public float lifeTime = 0f;
        public float elapsedLifetime = 0f;
    }
}


