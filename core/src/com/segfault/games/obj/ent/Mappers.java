package com.segfault.games.obj.ent;

import com.badlogic.ashley.core.ComponentMapper;
import com.segfault.games.obj.comp.*;

/**
 * class to keep all componentMappers, component mappers are the same as entity.getComponent
 * except that they tend to be faster
 */
public class Mappers {
    public final ComponentMapper<DrawableComponent> Drawable = ComponentMapper.getFor(DrawableComponent.class);
    public final ComponentMapper<MovingComponent> Moving = ComponentMapper.getFor(MovingComponent.class);
    public final ComponentMapper<RecOwnerComponent> RecOwner = ComponentMapper.getFor(RecOwnerComponent.class);
    public final ComponentMapper<CollidesComponent> Collides = ComponentMapper.getFor(CollidesComponent.class);
    public final ComponentMapper<LifetimeComponent> Lifetime = ComponentMapper.getFor(LifetimeComponent.class);
    public final ComponentMapper<SpeedDecreaseComponent> SpeedDecrease = ComponentMapper.getFor(SpeedDecreaseComponent.class);
    public final ComponentMapper<AlphaDecreaseComponent> AlphaDecrease = ComponentMapper.getFor(AlphaDecreaseComponent.class);
    public final ComponentMapper<LifeComponent> Life = ComponentMapper.getFor(LifeComponent.class);
    public final ComponentMapper<RectangleCollisionComponent> RecCollision = ComponentMapper.getFor(RectangleCollisionComponent.class);
    public final ComponentMapper<DamageComponent> Damage = ComponentMapper.getFor(DamageComponent.class);
    public final ComponentMapper<DisposeOnCollisionComponent> DisposeOnCollision = ComponentMapper.getFor(DisposeOnCollisionComponent.class);
    public final ComponentMapper<BounceComponent> Bounce = ComponentMapper.getFor(BounceComponent.class);
    public final ComponentMapper<TrailComponent> Trail = ComponentMapper.getFor(TrailComponent.class);
    public final ComponentMapper<MovementInputComponent> MovementInput = ComponentMapper.getFor(MovementInputComponent.class);
    public final ComponentMapper<PointingComponent> Pointing = ComponentMapper.getFor(PointingComponent.class);
    public final ComponentMapper<AngleRecoilComponent> AngleRecoil = ComponentMapper.getFor(AngleRecoilComponent.class);
    public final ComponentMapper<PlayerGunComponent> PlayerGun = ComponentMapper.getFor(PlayerGunComponent.class);
    public final ComponentMapper<PositionRecoilComponent> PositionRecoil = ComponentMapper.getFor(PositionRecoilComponent.class);
    public final ComponentMapper<ScreenRecoilComponent> ScreenRecoil = ComponentMapper.getFor(ScreenRecoilComponent.class);

}
