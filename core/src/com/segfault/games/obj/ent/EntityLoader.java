package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.Entity;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.segfault.games.JavaKnight;
import com.segfault.games.obj.comp.*;


public class EntityLoader {
    private final JsonReader reader = new JsonReader();
    private final Json json = new Json();

    public void LoadEntities(EntityManager manager, JavaKnight instance) {

        for (FileHandle jsonEntity : Gdx.files.internal("Entities").list()) {
            JsonValue comps = reader.parse(jsonEntity).get("components");
            Entity entity = manager.GetEngine().createEntity();

            comps.forEach(i -> {
                Component comp = getComponentFromName(i.name, manager);
                comp.read(i, instance);
                entity.add(comp);
            });

            String fileName = jsonEntity.file().getName().replace(".json", "");

            manager.GetEntityCreator().AddPrototype(entity, EntityID.valueOf(fileName));

            if (fileName.equals("PLAYER")) manager.SetPlayer(manager.GetEntityCreator().SpawnEntity(EntityID.PLAYER, true));

        }
    }

    private Component getComponentFromName(String name, EntityManager manager) {
        switch (name) {
            case "alphaDecrease" -> {
                return manager.GetEngine().createComponent(AlphaDecreaseComponent.class);
            }
            case "bounce" -> {
                return manager.GetEngine().createComponent(BounceComponent.class);
            }
            case "collides" -> {
                return manager.GetEngine().createComponent(CollidesComponent.class);
            }
            case "cooldown" -> {
                return manager.GetEngine().createComponent(CooldownComponent.class);
            }
            case "damage" -> {
                return manager.GetEngine().createComponent(DamageComponent.class);
            }
            case "dispose" -> {
                return manager.GetEngine().createComponent(DisposeOnCollisionComponent.class);
            }
            case "drawable" -> {
                return manager.GetEngine().createComponent(DrawableComponent.class);
            }
            case "life" -> {
                return manager.GetEngine().createComponent(LifeComponent.class);
            }
            case "lifetime" -> {
                return manager.GetEngine().createComponent(LifetimeComponent.class);
            }
            case "movementInput" -> {
                return manager.GetEngine().createComponent(MovementInputComponent.class);
            }
            case "moving" -> {
                return manager.GetEngine().createComponent(MovingComponent.class);
            }
            case "pointing" -> {
                return manager.GetEngine().createComponent(PointingComponent.class);
            }
            case "recOwner" -> {
                return manager.GetEngine().createComponent(RecOwnerComponent.class);
            }
            case "rectangleCollision" -> {
                return manager.GetEngine().createComponent(RectangleCollisionComponent.class);
            }
            case "speedDecrease" -> {
                return manager.GetEngine().createComponent(SpeedDecreaseComponent.class);
            }
            case "trail" -> {
                return manager.GetEngine().createComponent(TrailComponent.class);
            }
            default -> throw new IllegalArgumentException("Unknown component: " + name);

        }

    }
}
