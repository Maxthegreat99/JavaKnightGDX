package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.EllipseMapObject;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector4;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
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
                comp.read(i, instance, false, entity);
                entity.add(comp);
            }

            String fileName = jsonEntity.file().getName().replace(".json", "");

            manager.GetEntityCreator().AddPrototype(entity, EntityID.valueOf(fileName));

        }
    }

    private final Vector4 pol = new Vector4();
    private final String[] defaultFilter = new String[]{"PLAYER", "OUT_OF_BOUNDS", "OBSTACLE", "OBJECT","BULLET"};
    public void LoadMapEntities(MapID id, JavaKnight instance) {
        TiledMap map = instance.GetMapLoader().maps.get(id);
        JsonReader reader = instance.GetEntityManager().GetEntityLoader().reader;

        int count = map.getLayers().get("Objects").getObjects().getCount();
        for (int i = 0; i < count; i++) {
            MapObject o = map.getLayers().get("Objects").getObjects().get(i);

            float x;
            float y;
            float w;
            float h;

            float[] vertices = null;
            if (o instanceof RectangleMapObject){
                x = ((RectangleMapObject)o).getRectangle().x;
                y = ((RectangleMapObject)o).getRectangle().y;
                w = ((RectangleMapObject)o).getRectangle().width;
                h = ((RectangleMapObject)o).getRectangle().height;

            }

            /**
             * currently only support triangles
             */

            else if (o instanceof PolygonMapObject) {
                x = ((PolygonMapObject) o).getPolygon().getX();
                y = ((PolygonMapObject) o).getPolygon().getY();
                vertices = ((PolygonMapObject) o).getPolygon().getTransformedVertices();

                float minX = Float.MAX_VALUE, minY = Float.MAX_VALUE;
                float maxX = Float.MIN_VALUE, maxY = Float.MIN_VALUE;

                for (int l = 0; l < vertices.length; l += 2) {
                    float X = vertices[l];     // X coordinate
                    float Y = vertices[l + 1]; // Y coordinate

                    // Update min/max values
                    if (X < minX) minX = X;
                    if (X > maxX) maxX = X;
                    if (Y < minY) minY = Y;
                    if (Y > maxY) maxY = Y;
                }

                w = maxX - minX;
                h = maxY - minY;


                for (int j = 0; j < vertices.length; j+=2) {
                    vertices[j] = (vertices[j] - (x + w / 2)) / Renderer.PIXEL_TO_METERS;
                    vertices[j + 1] = (vertices[j + 1] - (y + h / 2)) / Renderer.PIXEL_TO_METERS;
                }
            }

            else if (o instanceof EllipseMapObject){
                x = ((EllipseMapObject) o).getEllipse().x;
                y = ((EllipseMapObject) o).getEllipse().y;
                w = ((EllipseMapObject) o).getEllipse().width;
                h = ((EllipseMapObject) o).getEllipse().height;

            }
            else continue;
            /*
             * manually create a collision entity, if the entity has more components stored in the properties add them
             */
            if (o.getName().startsWith("Col")) {
                Entity e = instance.GetEntityManager().GetEngine().createEntity();
                CollidesComponent col = instance.GetEntityManager().GetEngine().createComponent(CollidesComponent.class);
                JsonValue jValue = reader.parse(o.getProperties().get("properties", String.class));

                JsonValue collides = jValue.get("collides");
                col.relationship = CollisionRelationship.valueOf(collides.getString("relationship"));

                String shape = collides.getString("shape", "RECTANGLE");
                col.shape = EntityManager.Shapes.valueOf(shape);

                col.restitution = collides.getFloat("restitution");
                col.friction = collides.getFloat("friction");
                col.density = collides.getFloat("density");

                col.bodyType = BodyDef.BodyType.valueOf(collides.getString("bodyType"));
                col.hasCollisionEvent = collides.getBoolean("hasCollisionEvent");
                col.linearDamping = collides.getFloat("linearDamping", 5f);
                col.rotationFixed = collides.getBoolean("rotationFixed", true);

                String[] filter;
                if (collides.get("filter") == null) filter = defaultFilter;
                else filter = collides.get("filter").asStringArray();

                for (String s : filter)
                    col.collisionFilter |= (short) CollisionRelationship.valueOf(s).flag;

                col.collisionFilterStrings.addAll(filter);

                col.x = x / Renderer.PIXEL_TO_METERS + (w / Renderer.PIXEL_TO_METERS) / 2;
                col.y = y / Renderer.PIXEL_TO_METERS + (h / Renderer.PIXEL_TO_METERS) / 2;
                col.width = w / Renderer.PIXEL_TO_METERS;
                col.height = h / Renderer.PIXEL_TO_METERS;

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

                if (col.shape.equals(EntityManager.Shapes.CIRCLE)) fixDef.shape.setRadius(col.width / 2);
                else if (col.shape.equals(EntityManager.Shapes.RECTANGLE)) ((PolygonShape)fixDef.shape).setAsBox(col.width / 2, col.height / 2);
                else ((PolygonShape)fixDef.shape).set(vertices);

                fixDef.filter.maskBits = col.collisionFilter;
                fixDef.filter.categoryBits = (short) col.relationship.flag;

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
                        comp.read(j, instance, true, e);
                        e.add(comp);
                    }

                instance.GetEntityManager().GetEngine().addEntity(e);
                continue;
            }

            pol.set(x, y, w, h);

            JsonValue value = null;
            if (o.getProperties().containsKey("properties")) value = reader.parse(o.getProperties().get("properties", String.class));


            Entity e = instance.GetEntityManager().GetEntityCreator().SpawnEntity(EntityID.valueOf(o.getName()), true, pol, value);

            if (o.getName().startsWith("PLAYER")) instance.GetEntityManager().SetPlayer(e);



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

            case "cameraFollower":
                return manager.GetEngine().createComponent(CameraFollowerComponent.class);

            case "normal":
                return manager.GetEngine().createComponent(NormalComponent.class);

            case "lightHolder":
                return manager.GetEngine().createComponent(LightHolderComponent.class);

            case "rotating":
                return manager.GetEngine().createComponent(RotatingComponent.class);

            case "playerAcceleration":
                return manager.GetEngine().createComponent(PlayerAcceleratedComponent.class);

            case "acceleratedBody":
                return manager.GetEngine().createComponent(AcceleratedBodyComponent.class);

            default:
                throw new IllegalArgumentException("Unknown component: " + name);

        }

    }
}
