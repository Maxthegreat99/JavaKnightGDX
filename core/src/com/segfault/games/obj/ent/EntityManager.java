package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.ashley.core.PooledEngine;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.dongbat.jbump.CollisionFilter;
import com.dongbat.jbump.Item;
import com.dongbat.jbump.Response;
import com.dongbat.jbump.World;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.Rec;
import com.segfault.games.obj.comp.*;
import com.segfault.games.obj.sys.*;
import com.segfault.games.util.indexT;

/**
 * Entity manager, holds the ECS engine and
 * the entity creator for use and handles entity loading
 */
public class EntityManager {
    private final PooledEngine pooledECS;
    private final World<Entity> physicWorld;
    private final EntityCreator entityCreator;
    private final Mappers mappers;
    private Entity player;


    public EntityManager(JavaKnight instance) {
        pooledECS = new PooledEngine();
        physicWorld = new World<>();
        entityCreator = new EntityCreator(instance);
        mappers = new Mappers();

    }


    /**
     * initializes the systems with their priorities and adds the listerner to the engine
     * @param instance
     * @param batch
     */
    public void InitializeSystems(JavaKnight instance, SpriteBatch batch) {
        pooledECS.addEntityListener(new EntityListener(instance));

        pooledECS.addSystem(new LifetimeSystem(instance, 1));
        pooledECS.addSystem(new CooldownSystem(instance, 2));
        pooledECS.addSystem(new SpeedDecreaseSystem(instance, 3));
        pooledECS.addSystem(new MovementInputSystem(instance, 4));
        pooledECS.addSystem(new MovementSystem(instance, 5, 0.02f));
        pooledECS.addSystem(new DamageCollisionSystem(instance, 6));
        pooledECS.addSystem(new BouncingSystem(instance, 7));
        pooledECS.addSystem(new DisposeCollisionSystem(instance, 8));
        pooledECS.addSystem(new AlphaDecreaseSystem(instance, 9));
        pooledECS.addSystem(new TrailingSystem(instance, 10));
        pooledECS.addSystem(new RenderingSystem(instance, batch, 11));
    }

    /**
     * loads the player and entities and adds their prototypes to the entity creator
     * @param instance
     * @param FRAME_WIDTH
     * @param FRAME_HEIGHT
     */
    public void LoadEntities(JavaKnight instance, int FRAME_WIDTH, int FRAME_HEIGHT) {
        loadPlayer(instance);
        player = entityCreator.SpawnEntity(EntityID.PLAYER, true);
        loadEntities(FRAME_WIDTH, FRAME_HEIGHT, instance);
    }


    private void loadEntities(int FRAME_WIDTH, int FRAME_HEIGHT, JavaKnight instance) {
        Entity obs = pooledECS.createEntity();
        CollidesComponent oComp = pooledECS.createComponent(CollidesComponent.class);
        oComp.physicItem = new Item<>(obs);
        oComp.filter = CollisionFilter.defaultFilter;
        oComp.collisionRelationShip = CollisionRelationship.OBSTACLE;
        oComp.x = FRAME_WIDTH / 2f;
        oComp.y = FRAME_HEIGHT / 2f;
        oComp.width = 75;
        oComp.height = 75;
        obs.add(oComp);
        physicWorld.add(oComp.physicItem, FRAME_WIDTH / 2f, FRAME_HEIGHT / 2f, 75, 75);
        pooledECS.addEntity(obs);

        Entity rObs = pooledECS.createEntity();
        RecOwnerComponent rOwn = pooledECS.createComponent(RecOwnerComponent.class);
        rOwn.rectangle = new Rec(FRAME_WIDTH / 4f, FRAME_HEIGHT / 2f, 75, 75);
        rOwn.rectangle.Rotate(57f, rOwn.rectangle.X, rOwn.rectangle.Y);
        instance.GetRectangles().add(rOwn.rectangle);
        rObs.add(rOwn);

        DisposeOnCollisionComponent rDis = pooledECS.createComponent(DisposeOnCollisionComponent.class);
        rDis.rectangle = player.getComponent(RecOwnerComponent.class).rectangle;
        rDis.checkRange2 = 75 * 75 + 75 * 75;
        rObs.add(rDis);

        pooledECS.addEntity(rObs);
    }

    private void loadPlayer(JavaKnight instance) {
        Entity player = pooledECS.createEntity();
        player.add(pooledECS.createComponent(PrototypeComp.class));
        DrawableComponent pDrawable = pooledECS.createComponent(DrawableComponent.class);

        pDrawable.order = 3;
        pDrawable.sprite = new Sprite(instance.GetAssetManager().GetTextures().get(indexT.PLAYER));
        pDrawable.sprite.setOriginCenter();
        pDrawable.sprite.setPosition(0,0);
        player.add(pDrawable);

        player.add(pooledECS.createComponent(MovingComponent.class));
        MovementInputComponent pMoveInput = pooledECS.createComponent(MovementInputComponent.class);
        pMoveInput.speed2 = 70.71f * 70.71f;
        player.add(pMoveInput);

        TrailComponent pTrail = pooledECS.createComponent(TrailComponent.class);
        pTrail.alphaComparator = 0.60f;
        pTrail.trailIninitalAlpha = 0.75f;
        pTrail.trailCooldown = 0.25f;
        pTrail.trailInitialCooldown = 0.15f;
        pTrail.trailAlphaDecrease = 1.5f;

        player.add(pTrail);

        CollidesComponent pCol = pooledECS.createComponent(CollidesComponent.class);
        pCol.height = 16;
        pCol.width = 16;
        pCol.x = -8;
        pCol.y = -8;
        pCol.collisionRelationShip = CollisionRelationship.PLAYER;
        pCol.filter = new CollisionFilter() {
            @Override
            public Response filter(Item item, Item other) {
                if (mappers.Collides.get((Entity) other.userData).collisionRelationShip.equals(CollisionRelationship.OBSTACLE))
                    return Response.slide;
                return null;
            }
        };
        pCol.physicItem = new Item<Entity>(player);
        player.add(pCol);
        RecOwnerComponent pRec = pooledECS.createComponent(RecOwnerComponent.class);
        pRec.rectangle = new Rec(8,8,16, 16);
        player.add(pRec);

        entityCreator.AddPrototype(player, EntityID.PLAYER);
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

    public Entity GetPlayer() {
        return player;
    }

    public void Dispose() {
        pooledECS.clearPools();
    }

}
