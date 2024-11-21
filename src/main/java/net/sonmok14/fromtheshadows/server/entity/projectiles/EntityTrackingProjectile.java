package net.sonmok14.fromtheshadows.server.entity.projectiles;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.projectile.ProjectileUtil;
import net.minecraft.world.entity.projectile.ShulkerBullet;
import net.minecraft.world.entity.projectile.WitherSkull;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;
import net.sonmok14.fromtheshadows.server.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.server.utils.registry.EntityRegistry;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.List;

public class EntityTrackingProjectile extends Projectile implements GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private LivingEntity target;
    private int ticksAlive = 0;

    public EntityTrackingProjectile(EntityType<? extends Projectile> p_38252_, Level p_38253_) {
        super(p_38252_, p_38253_);
    }

    public EntityTrackingProjectile(Level p_37235_, LivingEntity p_37236_) {
        this(EntityRegistry.TRACKING_PROJECTILE.get(), p_37235_);
        this.setOwner(p_37236_);
        this.setPos(p_37236_.getX() - (double)(p_37236_.getBbWidth() + 1.0F) * 0.5D * (double) Mth.sin(p_37236_.yBodyRot * ((float)Math.PI / 180F)), p_37236_.getEyeY() - (double)0.1F, p_37236_.getZ() + (double)(p_37236_.getBbWidth() + 1.0F) * 0.5D * (double)Mth.cos(p_37236_.yBodyRot * ((float)Math.PI / 180F)));
    }

    @Override
    protected void defineSynchedData() {
    }

    @Override
    public void tick() {
        super.tick();
        Vec3 vec3 = this.getDeltaMovement();
        HitResult hitresult = ProjectileUtil.getHitResultOnMoveVector(this, this::canHitEntity);
        if (hitresult.getType() != HitResult.Type.MISS && !net.minecraftforge.event.ForgeEventFactory.onProjectileImpact(this, hitresult))
            this.onHit(hitresult);
        double d0 = this.getX() + vec3.x;
        double d1 = this.getY() + vec3.y;
        double d2 = this.getZ() + vec3.z;
        this.updateRotation();
        if (this.level().getBlockStates(this.getBoundingBox()).noneMatch(BlockBehaviour.BlockStateBase::isAir)) {
            this.discard();
        } else if (this.isInWaterOrBubble()) {
            this.discard();
        } else {
            this.setDeltaMovement(vec3.scale((double)0.25F));
            if (!this.isNoGravity()) {
                this.setDeltaMovement(this.getDeltaMovement().add(0.0D, (double)-0.06F, 0.0D));
            }

            this.setPos(d0, d1, d2);
        }
        if (ticksAlive < 5) {
            ticksAlive++;
            return;
        }
        if (this.level() != null) {
            List<LivingEntity> nearbyEntities = this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(10), e -> e != this.getOwner() && !e.isDeadOrDying());
            if (!nearbyEntities.isEmpty()) {
                this.target = getClosestTarget(nearbyEntities);
            }
        }
        if (this.target != null) {
            Vec3 targetPos = this.target.position();
            Vec3 direction = targetPos.subtract(this.position()).normalize();
            this.setDeltaMovement(direction.scale(0.15));
        }
        ticksAlive++;
        if (this.ticksAlive >= 100) {
            this.discard();
        }
        if (this.level().isClientSide) {
            this.yOld = this.getY();
        }
    }

    protected void onHitEntity(EntityHitResult p_37573_) {
        boolean flag;
        Entity entity = this.getOwner();
        Entity entity2 = p_37573_.getEntity();
        if (entity instanceof LivingEntity livingentity) {
            flag = entity2.hurt(this.damageSources().mobProjectile(this, livingentity), (float) (15 * FTSConfig.bulldrogioth_melee_damage_multiplier) / 2);
            if (flag && entity2 instanceof LivingEntity livingEntity2) {
                if (entity.isAlive() && entity2 != null) {
                    Vec3 originalVelocity = livingEntity2.getDeltaMovement();
                    livingEntity2.setDeltaMovement(originalVelocity);
                    livingEntity2.addEffect(new MobEffectInstance(EffectRegistry.HEAL_BLOCK.get(), 100), this);
                    discard();
                }
            }

        }
    }

    protected void onHitBlock(BlockHitResult p_37239_) {
        super.onHitBlock(p_37239_);
        if (!this.level().isClientSide) {
            this.discard();
        }

    }

    private LivingEntity getClosestTarget(List<LivingEntity> entities) {
        LivingEntity closestEntity = null;
        double closestDistance = Double.MAX_VALUE;

        for (LivingEntity entity : entities) {
            double distance = this.position().distanceTo(entity.position());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestEntity = entity;
            }
        }

        return closestEntity;
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {

    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return cache;
    }
}
