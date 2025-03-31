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
    public final ComponentMapper<CollidesComponent> Collides = ComponentMapper.getFor(CollidesComponent.class);
    public final ComponentMapper<LifetimeComponent> Lifetime = ComponentMapper.getFor(LifetimeComponent.class);
    public final ComponentMapper<SpeedDecreaseComponent> SpeedDecrease = ComponentMapper.getFor(SpeedDecreaseComponent.class);
    public final ComponentMapper<AlphaDecreaseComponent> AlphaDecrease = ComponentMapper.getFor(AlphaDecreaseComponent.class);
    public final ComponentMapper<BounceComponent> Bounce = ComponentMapper.getFor(BounceComponent.class);
    public final ComponentMapper<TrailComponent> Trail = ComponentMapper.getFor(TrailComponent.class);
    public final ComponentMapper<MovementInputComponent> MovementInput = ComponentMapper.getFor(MovementInputComponent.class);
    public final ComponentMapper<PointingComponent> Pointing = ComponentMapper.getFor(PointingComponent.class);
    public final ComponentMapper<AngleRecoilComponent> AngleRecoil = ComponentMapper.getFor(AngleRecoilComponent.class);
    public final ComponentMapper<PlayerGunComponent> PlayerGun = ComponentMapper.getFor(PlayerGunComponent.class);
    public final ComponentMapper<PositionRecoilComponent> PositionRecoil = ComponentMapper.getFor(PositionRecoilComponent.class);
    public final ComponentMapper<ScreenRecoilComponent> ScreenRecoil = ComponentMapper.getFor(ScreenRecoilComponent.class);
    public final ComponentMapper<BulletSpawnComponent> BulletSpawn = ComponentMapper.getFor(BulletSpawnComponent.class);
    public final ComponentMapper<PhysicComponent> Physic = ComponentMapper.getFor(PhysicComponent.class);
    public final ComponentMapper<CollisionEventComponent> CollisionEvent = ComponentMapper.getFor(CollisionEventComponent.class);
    public final ComponentMapper<CameraFollowerComponent> CameraFollower = ComponentMapper.getFor(CameraFollowerComponent.class);
    public final ComponentMapper<NormalComponent> Normal = ComponentMapper.getFor(NormalComponent.class);
    public final ComponentMapper<LightHolderComponent> LightHolder = ComponentMapper.getFor(LightHolderComponent.class);
    public final ComponentMapper<RotatingComponent> Rotating = ComponentMapper.getFor(RotatingComponent.class);
    public final ComponentMapper<PlayerAcceleratedComponent> PlayerAcceleration = ComponentMapper.getFor(PlayerAcceleratedComponent.class);
    public final ComponentMapper<AcceleratedBodyComponent> AcceleratedBody = ComponentMapper.getFor(AcceleratedBodyComponent.class);
    public final ComponentMapper<GroundCheckComponent> GroundCheck = ComponentMapper.getFor(GroundCheckComponent.class);
    public final ComponentMapper<PlayerDashComponent> PlayerDash = ComponentMapper.getFor(PlayerDashComponent.class);
}
