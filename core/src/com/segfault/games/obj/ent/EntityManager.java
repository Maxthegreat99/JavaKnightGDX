package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dongbat.jbump.World;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.sys.*;
import com.segfault.games.obj.sys.phy.PhysicsSystem;
import com.segfault.games.obj.wld.MapID;

/**
 * Entity manager, holds the ECS engine and
 * the entity creator for use and handles entity loading
 */
public class EntityManager {
    private final PooledEngine pooledECS;
    private final World<Entity> physicWorld;
    private final EntityCreator entityCreator;
    private final Mappers mappers;
    private final CollisionFilters filters;
    private final TargetGetter targetGetter;
    private final EntityLoader entityLoader;
    private Entity player;



    public EntityManager(JavaKnight instance) {
        pooledECS = new PooledEngine();
        physicWorld = new World<>();
        entityCreator = new EntityCreator(instance);
        mappers = new Mappers();
        filters = new CollisionFilters(mappers);
        targetGetter = new TargetGetter(this);
        entityLoader = new EntityLoader();

    }


    /**
     * initializes the systems with their priorities and adds the listerner to the engine
     * @param instance
     * @param batch
     */
    public void InitializeSystems(JavaKnight instance, SpriteBatch batch) {
        pooledECS.addEntityListener(new EntityListener(instance));

        pooledECS.addSystem(new LifetimeSystem(instance, 10));
        pooledECS.addSystem(new PhysicsSystem(instance, 0.02f, 20));
        pooledECS.addSystem(new PointingSystem(instance, 30));
        pooledECS.addSystem(new AlphaDecreaseSystem(instance, 40));
        pooledECS.addSystem(new TrailingSystem(instance, 50));
        pooledECS.addSystem(new PlayerGunSystem(instance, 60));
        pooledECS.addSystem(new RecoilSystem(instance, 70));
        pooledECS.addSystem(new RenderingSystem(instance, batch, 80));
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
    public World<Entity> GetPhysicWorld() {
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
    public void Dispose() {
        pooledECS.clearPools();
    }

}
