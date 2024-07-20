package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.dongbat.jbump.Item;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.wld.MapID;

/**
 * Handles loading entities from the JSON
 */
public class EntityLoader {
    public final JsonReader reader = new JsonReader();
    private final Json json = new Json();

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

            comps.forEach(i -> {
                Component comp = getComponentFromName(i.name, manager);
                comp.read(i, instance);
                entity.add(comp);
            });

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

            /**
             * manually create a collision entity, if the entity has more components stored in the properties add them
             */
            if (po.getName().startsWith("Col")) {
                Entity e = instance.GetEntityManager().GetEngine().createEntity();
                CollidesComponent col = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
                JsonValue jValue = reader.parse(po.getProperties().get("properties", String.class));

                JsonValue collides = jValue.get("collides");
                col.relationship = CollisionRelationship.valueOf(collides.getString("relationship"));

                if (collides.has("filter")) col.filterID = CollisionFiltersID.valueOf(collides.getString("filter"));
                else col.filterID = CollisionFiltersID.DEFAULT;

                col.filter = instance.GetEntityManager().GetCollisionFilters().Filters.get(col.filterID);
                col.physicItem = new Item<>(e);
                instance.GetEntityManager().GetPhysicWorld().add(col.physicItem, vec.x, vec.y, po.getRectangle().width, po.getRectangle().height);
                col.x = vec.x;
                col.y = vec.y;
                col.width = po.getRectangle().width;
                col.height = po.getRectangle().height;

                e.add(col);

                jValue.remove("collides");

                /*
                 * This adds components from the properties, this doesnt mean the properties json file
                 * can be used as entity editor exclusively, this method breaks once you try to create
                 * more complex components with it
                 */
                if (jValue.child() != null)
                    jValue.forEach(j -> {
                        Component comp = getComponentFromName(j.name, instance.GetEntityManager());
                        comp.read(j, instance);
                        e.add(comp);
                    });

                instance.GetEntityManager().GetEngine().addEntity(e);
                continue;
            }
            else if (po.getName().startsWith("Rec")) {
                Entity e = instance.GetEntityManager().GetEngine().createEntity();
                RecOwnerComponent rec = instance.GetEntityManager().GetEngine().createComponent(RecOwnerComponent.class);
                JsonValue jValue = reader.parse(po.getProperties().get("properties", String.class));

                rec.rectangle = new Rec(vec.x, vec.y, po.getRectangle().width, po.getRectangle().height);
                instance.GetRectangles().add(rec.rectangle);

                if (jValue.has("recOwner")) {
                    rec.rectangle.Rotate(jValue.get("recOwner").getFloat("rotation"), vec.x, vec.y);
                    jValue.remove("recOwner");
                }

                e.add(rec);

                /*
                 * This adds components from the properties, this doesnt mean the properties json file
                 * can be used as entity editor exclusively, this method breaks once you try to create
                 * more complex components with it
                 */
                if (jValue.child() != null)
                    jValue.forEach(k -> {
                        Component comp = getComponentFromName(k.name, instance.GetEntityManager());
                        comp.read(k, instance);
                        e.add(comp);
                    });

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

            case "damage":
                return manager.GetEngine().createComponent(DamageComponent.class);

            case "dispose":
                return manager.GetEngine().createComponent(DisposeOnCollisionComponent.class);

            case "drawable":
                return manager.GetEngine().createComponent(DrawableComponent.class);

            case "life":
                return manager.GetEngine().createComponent(LifeComponent.class);

            case "lifetime":
                return manager.GetEngine().createComponent(LifetimeComponent.class);

            case "movementInput":
                return manager.GetEngine().createComponent(MovementInputComponent.class);

            case "moving":
                return manager.GetEngine().createComponent(MovingComponent.class);

            case "pointing":
                return manager.GetEngine().createComponent(PointingComponent.class);

            case "recOwner":
                return manager.GetEngine().createComponent(RecOwnerComponent.class);

            case "rectangleCollision":
                return manager.GetEngine().createComponent(RectangleCollisionComponent.class);

            case "speedDecrease":
                return manager.GetEngine().createComponent(SpeedDecreaseComponent.class);

            case "trail":
                return manager.GetEngine().createComponent(TrailComponent.class);

            case "recoil":
                return manager.GetEngine().createComponent(AngleRecoilComponent.class);

            case "playerGun":
                return manager.GetEngine().createComponent(PlayerGunComponent.class);

            default:
                throw new IllegalArgumentException("Unknown component: " + name);

        }

    }
}
