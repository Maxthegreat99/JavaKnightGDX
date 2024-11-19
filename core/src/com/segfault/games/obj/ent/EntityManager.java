package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.EntitySystem;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ObjectMap;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.sys.*;
import com.segfault.games.obj.sys.phy.*;
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
    private final CollisionFilters filters;
    private final TargetGetter targetGetter;
    private final EntityLoader entityLoader;
    private final ObjectMap<indexEntitySystems, Object> systems = new ObjectMap();
    private Entity player;



    public EntityManager(JavaKnight instance) {
        pooledECS = new PooledEngine();
        physicWorld = new World(new Vector2(0, 0), true);
        entityCreator = new EntityCreator(instance);
        mappers = new Mappers();
        filters = new CollisionFilters(mappers);
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
        systems.put(indexEntitySystems.PHYSICS_SYSTEM, new PhysicsSystem(instance, 0.02f, 20));
        systems.put(indexEntitySystems.POINTING_SYSTEM, new PointingSystem(instance, 30));
        systems.put(indexEntitySystems.ALPHA_DECREASE_SYSTEM, new AlphaDecreaseSystem(instance, 40));
        systems.put(indexEntitySystems.TRAILING_SYSTEM, new TrailingSystem(instance, 50));
        systems.put(indexEntitySystems.PLAYER_GUN_SYSTEM, new PlayerGunSystem(instance, 60));
        systems.put(indexEntitySystems.ANGLE_RECOIL_SYSTEM, new AngleRecoilSystem(instance, 70));
        systems.put(indexEntitySystems.POSITION_RECOIL_SYSTEM, new PositionRecoilSystem(instance, 80));
        systems.put(indexEntitySystems.SCREEN_RECOIL_SYSTEM, new ScreenRecoilSystem(instance, 90));
        systems.put(indexEntitySystems.BULLET_SPAWN_SYSTEM, new BulletSpawnSystem(instance, 100));
        systems.put(indexEntitySystems.RENDERING_SYSTEM, new RenderingSystem(instance, batch, 110));

        for (Object system : systems.values())
            pooledECS.addSystem( (EntitySystem) system);


        systems.put(indexEntitySystems.SPEED_DECREASE_SYSTEM, new SpeedDecreaseSystem(instance));
        systems.put(indexEntitySystems.MOVEMENT_INPUT_SYSTEM, new MovementInputSystem(instance));
        systems.put(indexEntitySystems.MOVEMENT_SYSTEM, new MovementSystem(instance));

    }

    /**
     * loads the player and entities and adds their prototypes to the entity creator
     * @param instance
     */
    public void LoadEntities(JavaKnight instance) {
        entityLoader.LoadEntities(this, instance);
        entityLoader.LoadMapEntities(MapID.BOSS_ROOM, instance);
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
    public CollisionFilters GetCollisionFilters() {
        return filters;
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
    public void Dispose() {
        pooledECS.clearPools();
    }

}
