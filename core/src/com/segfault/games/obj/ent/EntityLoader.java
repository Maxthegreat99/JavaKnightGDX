package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.gra.Renderer;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.wld.MapID;

/**
 * Handles loading entities from the JSON
 */
public class EntityLoader {
    public final JsonReader reader = new JsonReader();

    /**
     * loads entities stored in assets/Entities from json
     * @param manager
     * @param instance
     */

    public void LoadEntities(EntityManager manager, JavaKnight instance) {

        for (FileHandle jsonEntity : Gdx.files.internal("Entities").list()) {
            if (!jsonEntity.name().endsWith(".json")) continue;

            JsonValue comps = reader.parse(jsonEntity).get("components");
            Entity entity = manager.GetEngine().createEntity();

            for (JsonValue i : comps)
            {
                Component comp = getComponentFromName(i.name, manager);
                comp.read(i, instance);
                entity.add(comp);
            }

            String fileName = jsonEntity.file().getName().replace(".json", "");

            manager.GetEntityCreator().AddPrototype(entity, EntityID.valueOf(fileName));

        }
    }

    private final Vector4 pol = new Vector4();
    public void LoadMapEntities(MapID id, JavaKnight instance) {
        TiledMap map = instance.GetMapLoader().maps.get(id);
        JsonReader reader = instance.GetEntityManager().GetEntityLoader().reader;

        int count = map.getLayers().get("Objects").getObjects().getCount();
        for (int i = 0; i < count; i++) {
            MapObject o = map.getLayers().get("Objects").getObjects().get(i);
            RectangleMapObject po;

            if (!(o instanceof RectangleMapObject)) continue;
            else po = (RectangleMapObject) o;

            Vector2 vec = instance.GetMapLoader().tiledPosToGDX(po.getRectangle().x, po.getRectangle().y, po.getRectangle().width, po.getRectangle().height, po.getName());

            /*
             * manually create a collision entity, if the entity has more components stored in the properties add them
             */
            if (po.getName().startsWith("Col")) {
                Entity e = instance.GetEntityManager().GetEngine().createEntity();
                CollidesComponent col = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
                JsonValue jValue = reader.parse(po.getProperties().get("properties", String.class));

                JsonValue collides = jValue.get("collides");
                col.relationship = CollisionRelationship.valueOf(collides.getString("relationship"));

                String shape = "POLYGONE";
                col.shape = EntityManager.Shapes.valueOf(shape);

                col.restitution = collides.getFloat("restitution");
                col.friction = collides.getFloat("friction");
                col.density = collides.getFloat("density");

                col.bodyType = BodyDef.BodyType.valueOf(collides.getString("bodyType"));
                col.hasCollisionEvent = collides.getBoolean("hasCollisionEvent");
                col.linearDamping = collides.getFloat("linearDamping", 5f);
                col.rotationFixed = collides.getBoolean("rotationFixed", true);

                col.x = vec.x / Renderer.PIXEL_TO_METERS + (po.getRectangle().width / Renderer.PIXEL_TO_METERS) / 2;
                col.y = vec.y / Renderer.PIXEL_TO_METERS + (po.getRectangle().height / Renderer.PIXEL_TO_METERS) / 2;
                col.width = po.getRectangle().width / Renderer.PIXEL_TO_METERS;
                col.height = po.getRectangle().height / Renderer.PIXEL_TO_METERS;

                BodyDef bdyDef = instance.GetEntityManager().GetBodyDef();
                FixtureDef fixDef = instance.GetEntityManager().GetFixtureDef();

                bdyDef.position.set(col.x, col.y);
                bdyDef.type = col.bodyType;
                bdyDef.linearDamping = col.linearDamping;
                bdyDef.fixedRotation = col.rotationFixed;

                fixDef.friction = col.friction;
                fixDef.density = col.density;
                fixDef.restitution = col.restitution;

                fixDef.shape = instance.GetEntityManager().GetShape(col.shape.ordinal());


                ((PolygonShape)fixDef.shape).setAsBox(col.width / 2, col.height / 2);

                col.physicBody = instance.GetEntityManager().GetPhysicWorld().createBody(bdyDef);
                col.fixture = col.physicBody.createFixture(fixDef);

                instance.GetEntityManager().ResetDefinitions();

                col.physicBody.setUserData(col);
                col.fixture.setUserData(col);
                col.entity = e;

                e.add(col);

                jValue.remove("collides");

                /*
                 * This adds components from the properties, this doesnt mean the properties json file
                 * can be used as entity editor exclusively, this method breaks once you try to create
                 * more complex components with it
                 */
                if (jValue.child() != null)
                    for (JsonValue j : jValue)
                    {
                        Component comp = getComponentFromName(j.name, instance.GetEntityManager());
                        comp.read(j, instance);
                        e.add(comp);
                    }

                instance.GetEntityManager().GetEngine().addEntity(e);
                continue;
            }

            pol.set(vec.x, vec.y, po.getRectangle().width, po.getRectangle().height);

            JsonValue value = null;
            if (po.getProperties().containsKey("properties")) value = reader.parse(po.getProperties().get("properties", String.class));


            Entity e = instance.GetEntityManager().GetEntityCreator().SpawnEntity(EntityID.valueOf(po.getName()), true, pol, value);

            if (po.getName().startsWith("PLAYER")) instance.GetEntityManager().SetPlayer(e);



        }
    }

    private Component getComponentFromName(String name, EntityManager manager) {
        switch (name) {
            case "alphaDecrease":
                return manager.GetEngine().createComponent(AlphaDecreaseComponent.class);

            case "bounce":
                return manager.GetEngine().createComponent(BounceComponent.class);

            case "collides":
                return manager.GetEngine().createComponent(CollidesComponent.class);

            case "drawable":
                return manager.GetEngine().createComponent(DrawableComponent.class);

            case "lifetime":
                return manager.GetEngine().createComponent(LifetimeComponent.class);

            case "movementInput":
                return manager.GetEngine().createComponent(MovementInputComponent.class);

            case "moving":
                return manager.GetEngine().createComponent(MovingComponent.class);

            case "pointing":
                return manager.GetEngine().createComponent(PointingComponent.class);

            case "speedDecrease":
                return manager.GetEngine().createComponent(SpeedDecreaseComponent.class);

            case "trail":
                return manager.GetEngine().createComponent(TrailComponent.class);

            case "recoil":
                return manager.GetEngine().createComponent(AngleRecoilComponent.class);

            case "playerGun":
                return manager.GetEngine().createComponent(PlayerGunComponent.class);

            case "positionRecoil":
                return manager.GetEngine().createComponent(PositionRecoilComponent.class);

            case "screenRecoil":
                return manager.GetEngine().createComponent(ScreenRecoilComponent.class);

            case "bulletSpawn":
                return manager.GetEngine().createComponent(BulletSpawnComponent.class);

            case "physic":
                return manager.GetEngine().createComponent(PhysicComponent.class);

            case "collisionEvent":
                return manager.GetEngine().createComponent(CollisionEventComponent.class);

            default:
                throw new IllegalArgumentException("Unknown component: " + name);

        }

    }
}
