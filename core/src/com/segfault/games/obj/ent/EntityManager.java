package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.CollisionEvents;
import com.segfault.games.obj.sys.*;
import com.segfault.games.obj.sys.phy.*;
import com.segfault.games.obj.sys.phy.col_event.BouncingCollisionSystem;
import com.segfault.games.obj.sys.phy.col_event.CollisionEventSystem;
import com.segfault.games.obj.sys.phy.col_event.DisposeCollisionSystem;
import com.segfault.games.obj.sys.phy.col_event.GroundCheckCollisionSystem;
import com.segfault.games.obj.wld.MapID;

/**
 * Entity manager, holds the ECS engine and
 * the entity creator for use and handles entity loading
 */
public class EntityManager {
    private final PooledEngine pooledECS;
    private final World physicWorld;
    private final EntityCreator entityCreator;
    private final Mappers mappers;
    private final TargetGetter targetGetter;
    private final EntityLoader entityLoader;
    private final ObjectMap<indexEntitySystems, Object> systems = new ObjectMap<>();
    private final CollisionEventSystem[] collisionEvents = new CollisionEventSystem[32];
    private Entity player;

    /**
     *  Shapes used for creating new entity collisions
     */
    private final Shape[] shapes = {
            new CircleShape(),
            new PolygonShape(),
            new PolygonShape()
    };

    /**
     * used for ease of access to our shapes
     */
    public enum Shapes {
        CIRCLE,
        RECTANGLE,
        POLYGONE
    }

    /**
     * Fixture definition of our body, sets initial physical properties
     * of entities
     */
    private final FixtureDef fixtureDefinition = new FixtureDef();

    /**
     *  body definition used to set inital properties our entities' physic body
     */
    private final BodyDef bodyDefinition = new BodyDef();


    public EntityManager(JavaKnight instance) {
        pooledECS = new PooledEngine();
        physicWorld = new World(new Vector2(0, -9.81f), true);
        entityCreator = new EntityCreator(instance);
        mappers = new Mappers();
        targetGetter = new TargetGetter(this);
        entityLoader = new EntityLoader();

    }


    /**
     * initializes the systems with their priorities and adds the listener to the engine
     * @param instance
     * @param batch
     */
    public void InitializeSystems(JavaKnight instance, SpriteBatch batch) {
        pooledECS.addEntityListener(new EntityListener(instance));

        systems.put(indexEntitySystems.LIFETIME_SYSTEM, new LifetimeSystem(instance, 10));
        systems.put(indexEntitySystems.PHYSICS_SYSTEM, new PhysicsSystem(instance, 0.01666666666f, 20));
        systems.put(indexEntitySystems.SPRITE_POSITIONING_SYSTEM, new SpritePositioningSystem(instance, 30));
        systems.put(indexEntitySystems.POINTING_SYSTEM, new PointingSystem(instance, 40));
        systems.put(indexEntitySystems.ALPHA_DECREASE_SYSTEM, new AlphaDecreaseSystem(instance, 50));
        systems.put(indexEntitySystems.TRAILING_SYSTEM, new TrailingSystem(instance, 60));
        systems.put(indexEntitySystems.PLAYER_GUN_SYSTEM, new PlayerGunSystem(instance, 70));
        systems.put(indexEntitySystems.ANGLE_RECOIL_SYSTEM, new AngleRecoilSystem(instance, 80));
        systems.put(indexEntitySystems.POSITION_RECOIL_SYSTEM, new PositionRecoilSystem(instance, 90));
        systems.put(indexEntitySystems.SCREEN_RECOIL_SYSTEM, new ScreenRecoilSystem(instance, 100));
        systems.put(indexEntitySystems.BULLET_SPAWN_SYSTEM, new BulletSpawnSystem(instance, 110));
        systems.put(indexEntitySystems.CAMERA_FOLLOWER_SYSTEM, new CameraFollowerSystem(instance, 112));
        systems.put(indexEntitySystems.NORMAL_RENDERING_SYSTEM, new NormalRenderingSystem(instance, instance.GetRenderer().GetNormalBatch(), 115));
        systems.put(indexEntitySystems.RENDERING_SYSTEM, new RenderingSystem(instance, batch, 120));

        for (Object system : systems.values())
            pooledECS.addSystem( (EntitySystem) system);


        systems.put(indexEntitySystems.ROTATING_SYSTEM, new RotatingSystem(instance));
        systems.put(indexEntitySystems.SPEED_DECREASE_SYSTEM, new SpeedDecreaseSystem(instance));
        systems.put(indexEntitySystems.MOVEMENT_INPUT_SYSTEM, new MovementInputSystem(instance));
        systems.put(indexEntitySystems.MOVEMENT_SYSTEM, new MovementSystem(instance));
        systems.put(indexEntitySystems.PLAYER_ACCELERATION_SYSTEM, new PlayerAccelerationSystem(instance));
        systems.put(indexEntitySystems.PLAYER_DASH_SYSTEM, new PlayerDashSystem(instance));

        collisionEvents[CollisionEvents.DISPOSE.ordinal()] = new DisposeCollisionSystem(instance);
        collisionEvents[CollisionEvents.BOUNCING.ordinal()] = new BouncingCollisionSystem(instance);
        collisionEvents[CollisionEvents.GROUND_CHECK.ordinal()] = new GroundCheckCollisionSystem(instance);

        physicWorld.setContactListener(new CollisionListener(this));

    }

    /**
     * loads the player and entities and adds their prototypes to the entity creator
     * @param instance
     */
    public void LoadEntities(JavaKnight instance) {
        entityLoader.LoadEntities(this, instance);
        entityLoader.LoadMapEntities(MapID.BOSS_ROOM, instance);
    }

    public Shape GetShape(int i) {
        return shapes[i];
    }

    public FixtureDef GetFixtureDef() {
        return fixtureDefinition;
    }

    public BodyDef GetBodyDef() {
        return bodyDefinition;
    }

    public void ResetDefinitions() {
        bodyDefinition.type = BodyDef.BodyType.StaticBody;
        bodyDefinition.position.set(0,0);
        bodyDefinition.fixedRotation = true;
        bodyDefinition.linearDamping = 0f;

        fixtureDefinition.filter.categoryBits = 0;
        fixtureDefinition.filter.maskBits = 0;
        fixtureDefinition.shape = null;
        fixtureDefinition.density = 0f;
        fixtureDefinition.restitution = 0f;
        fixtureDefinition.friction = 0f;
    }

    public PooledEngine GetEngine() {
        return pooledECS;
    }
    public World GetPhysicWorld() {
        return physicWorld;
    }
    public Mappers GetMappers() {
        return mappers;
    }
    public Entity GetPlayer() {
        return player;
    }
    public void SetPlayer(Entity player) {
        this.player = player;
    }
    public TargetGetter GetTargetGetter() {
        return targetGetter;
    }
    public EntityCreator GetEntityCreator() {
        return entityCreator;
    }
    public EntityLoader GetEntityLoader() {
        return entityLoader;
    }

    public ObjectMap<indexEntitySystems, Object> GetSystems() {
        return systems;
    }

    public CollisionEventSystem[] GetCollisionEvents() {
        return collisionEvents;
    }

    public void Dispose() {
        pooledECS.clearPools();
        for (Shape shape : shapes) shape.dispose();

    }

}
