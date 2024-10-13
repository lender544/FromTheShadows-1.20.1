package net.sonmok14.fromtheshadows.server.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.BiomeTags;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.*;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.navigation.WallClimberNavigation;
import net.minecraft.world.entity.animal.IronGolem;
import net.minecraft.world.entity.animal.allay.Allay;
import net.minecraft.world.entity.animal.axolotl.Axolotl;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.raid.Raider;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fluids.FluidType;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.entity.projectiles.DoomBreathEntity;
import net.sonmok14.fromtheshadows.server.entity.projectiles.ScreenShakeEntity;
import net.sonmok14.fromtheshadows.server.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.server.utils.registry.EntityRegistry;
import net.sonmok14.fromtheshadows.server.utils.registry.SoundRegistry;
import net.sonmok14.fromtheshadows.server.utils.registry.TagRegistry;
import org.jetbrains.annotations.Nullable;
import software.bernie.geckolib.animatable.GeoEntity;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.EnumSet;
import java.util.List;
import java.util.UUID;

public class NehemothEntity extends Monster implements Enemy, GeoEntity {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
    private static final EntityDataAccessor<Boolean> IS_RIGHT = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> STONE = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Boolean> CLIMBING = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(NehemothEntity.class, EntityDataSerializers.INT);
    public int attackID;
    private int stunnedTick;
    public int attacktick;
    public int attackCooldown;
    public int biteCooldown;
    public int smashCooldown;
    public int breathCooldown;
    public int roarCooldown;
    public float growlProgress;
    public static final byte MELEE_ATTACK = 1;
    public static final byte MELEE_ATTACK_ONCE = 2;
    public static final byte BITE_ATTACK = 3;
    public static final byte ROAR_ATTACK = 4;
    public static final byte SMASH_ATTACK = 5;
    public static final byte BREATH_ATTACK = 6;
    public static final byte GUARD = 7;

    public static final ResourceLocation STONE_LOOT = new ResourceLocation("fromtheshadows", "entities/nehemoth_stone");

    public NehemothEntity(EntityType<? extends NehemothEntity> type, Level world) {
        super(type, world);
        this.getNavigation().setCanFloat(true);
        this.setPathfindingMalus(BlockPathTypes.UNPASSABLE_RAIL, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_OTHER, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.POWDER_SNOW, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.LAVA, 8.0F);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        xpReward = 30;
    }
    //animation

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar data) {
        data.add(
                new AnimationController<>(this, "eyeglow", 5, event -> {
  event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.eyeglow"));
                    return PlayState.CONTINUE;
                }));
        data.add(
                new AnimationController<>(this, "growl", 15, event -> {
                    if (this.growlProgress <= 50 && isAlive() && !isStone() && this.getTarget() == null)
                    {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.growl"));
                    }
                    event.resetCurrentAnimation();
                    return PlayState.STOP;
                }));
        data.add(new AnimationController<>(this, "stone", 50,  event -> {
            if(isStone())
            {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.stop1"));
            }
            return PlayState.STOP;
        }));
        data.add(new AnimationController<>(this, "controller", 20,  event -> {
            if(!isStone())
            {
                if (!event.isMoving() && attackID == 0 && !isClimbing() && attackCooldown <= 0) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.idle"));
                }
                if (attackID == BREATH_ATTACK && attacktick < 18) {
                    return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.breathready"));

                }
            }
            return PlayState.STOP;
        }));
        data.add(new AnimationController<>(this, "controller2", 8, event ->  {
        if(isAlive()) {
            if(attackID == 0)
            {
               event.resetCurrentAnimation();
            }
            if (attackID == SMASH_ATTACK && onGround() && this.attacktick < 15) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.smash_start"));
            }
            if (attackID == SMASH_ATTACK && !onGround()) {
                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.jump"));
            }
            if (attackID == 1 && !isRight()) {
                return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.nehemoth.left_melee"));
            }
            if (attackID == 1 && isRight()) {
                return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.nehemoth.right_melee"));
            }
            if (attackID == 2 && !isRight()) {
                return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.nehemoth.left_melee_once"));
            }
            if (attackID == 2 && isRight()) {
                return event.setAndContinue(RawAnimation.begin().thenPlayAndHold("animation.nehemoth.right_melee_once"));
            }
        }
            return PlayState.STOP;
    }));
                data.add(new AnimationController<>(this, "controller3", 3, event -> {
                    if (!isStone()) {
                        if(attackCooldown > 0)
                        {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.smash"));
                        }
                        if (event.isMoving() && this.walkAnimation.speed() > 0.35F && attackID == 0) {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.run"));
                        }
                        if (isClimbing() && attackID == 0) {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.climb"));
                        }
                        if (attackID == ROAR_ATTACK) {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.roar"));
                        }
                        if (!this.isImmobile()) {
                            if (attackID == 1) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.none"));
                            }
                            if (attackID == BITE_ATTACK) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.bite"));
                            }
                            if (attackID == SMASH_ATTACK && !onGround()) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.jump"));
                            }
                            if (attackID == 5) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.none"));
                            }
                            if (attackID == 7) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.guard"));
                            }
                            if (attackID == 6 && attacktick < 18) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.none"));
                            }
                            if (attackID == 6) {
                                return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.breath"));
                            }
                        }
                        if (isImmobile() && isAlive()) {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.stun"));
                        }
                        if (event.isMoving() && attackID == 0) {
                            return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.walk"));
                        }
                    } else if (isStone()) {
                        return event.setAndContinue(RawAnimation.begin().thenLoop("animation.nehemoth.none"));
                    }
                    return PlayState.STOP;
                }));
    }


    public static AttributeSupplier.Builder createAttributes() {
        return LivingEntity.createLivingAttributes()
                .add(Attributes.FOLLOW_RANGE, 26.0D)
                .add(Attributes.MOVEMENT_SPEED, 0.35D)
                .add(Attributes.MAX_HEALTH, 100)
                .add(Attributes.ATTACK_DAMAGE, 7)
                .add(Attributes.ATTACK_KNOCKBACK, 0.0D)
                .add(Attributes.KNOCKBACK_RESISTANCE, 10.0D)
                .add(Attributes.ARMOR, 10.0D)
                .add(Attributes.ARMOR_TOUGHNESS, 5.0D)
                .add(Attributes.ATTACK_SPEED, 2.0D);
    }

    public static void setConfigattribute(LivingEntity entity, double hpconfig, double dmgconfig) {
        AttributeInstance maxHealthAttr = entity.getAttribute(Attributes.MAX_HEALTH);
        if (maxHealthAttr != null) {
            double difference = maxHealthAttr.getBaseValue() * hpconfig - maxHealthAttr.getBaseValue();
            maxHealthAttr.addTransientModifier(new AttributeModifier(UUID.fromString("6d57ab59-6f61-4bb9-9fee-6a0c75aea861"), "Health config multiplier", difference, AttributeModifier.Operation.ADDITION));
            entity.setHealth(entity.getMaxHealth());
        }
        AttributeInstance attackDamageAttr = entity.getAttribute(Attributes.ATTACK_DAMAGE);
        if (attackDamageAttr != null) {
            double difference = attackDamageAttr.getBaseValue() * dmgconfig - attackDamageAttr.getBaseValue();
            attackDamageAttr.addTransientModifier(new AttributeModifier(UUID.fromString("6d57ab59-6f61-4bb9-9fee-6a0c75aea861"), "Attack config multiplier", difference, AttributeModifier.Operation.ADDITION));

        }
    }


    public void addAdditionalSaveData(CompoundTag compound) {
        super.addAdditionalSaveData(compound);
        compound.putInt("Variant", this.getVariant());
        compound.putBoolean("Stone", this.isStone());
    }



    public void readAdditionalSaveData(CompoundTag compound) {
        super.readAdditionalSaveData(compound);
        this.setVariant(compound.getInt("Variant"));
        this.setStone(compound.getBoolean("Stone"));
    }





    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_RIGHT, false);
        this.entityData.define(VARIANT, 0);
        this.entityData.define(CLIMBING, false);
        this.entityData.define(STONE, false);
    }

    public boolean isRight() {
        return this.entityData.get(IS_RIGHT);
    }

    public void setRight(boolean p_32759_) {
        this.entityData.set(IS_RIGHT, p_32759_);
    }
    public boolean isStone() {
        return this.entityData.get(STONE);
    }

    public void setStone(boolean p_32759_) {
        this.entityData.set(STONE, p_32759_);
    }

    public boolean isClimbing() {
        return this.entityData.get(CLIMBING);
    }

    public void setClimbing(boolean p_32759_) {
        this.entityData.set(CLIMBING, p_32759_);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT).intValue();
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, Integer.valueOf(variant));
    }


    @Override
    @Nullable
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor worldIn, DifficultyInstance difficultyIn, MobSpawnType
            reason, @Nullable SpawnGroupData spawnDataIn, @Nullable CompoundTag dataTag) {
        if(this.isBiomeSoulSandValley(worldIn, this.blockPosition())){
            this.setVariant(1);
        }else{
            this.setVariant(0);
        }
        this.setAirSupply(this.getMaxAirSupply());
        this.setXRot(0.0F);
        return super.finalizeSpawn(worldIn, difficultyIn, reason, spawnDataIn, dataTag);
    }

    private static boolean isNether(ServerLevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(BiomeTags.IS_NETHER);
    }
    private static boolean isBiomeSoulSandValley(LevelAccessor worldIn, BlockPos position) {
        return worldIn.getBiome(position).is(Biomes.SOUL_SAND_VALLEY);
    }


    public int getMaxSpawnClusterSize() {
        return 1;
    }


    public static <T extends Mob> boolean canNehemothSpawn(EntityType<NehemothEntity> entityType, ServerLevelAccessor iServerWorld, MobSpawnType reason, BlockPos pos, RandomSource random) {
        return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && (pos.getY() <= 0 || isNether(iServerWorld, pos) && isBiomeSoulSandValley(iServerWorld, pos)) && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);

        // return reason == MobSpawnType.SPAWNER || !iServerWorld.canSeeSky(pos) && pos.getY() <= 0 && checkMonsterSpawnRules(entityType, iServerWorld, reason, pos, random);
    }



    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() {
        return this.cache;
    }
    @Override
    public MobType getMobType() {
        if(getVariant() == 1)
        {
            return MobType.UNDEAD;
        }
        return FTSMobType.DEMON;
    }

    protected float nextStep() {
        return this.isAggressive() ? this.moveDist + 2F : this.moveDist + 3.3F;
    }

    protected void playStepSound(BlockPos p_33350_, BlockState p_33351_) {
        if (this.isStone()) {
            super.playStepSound(p_33350_, p_33351_);
        } else {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.STOMP.get(), SoundSource.HOSTILE, 0.25F, 0.25F + this.getRandom().nextFloat() * 0.1F);
        }

    }

    public boolean canBeSeenAsEnemy() {
        return !isStone() && super.canBeSeenAsEnemy();
    }
    @Override
    public boolean isPushedByFluid(FluidType type) {
        return false;
    }
    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.35, true));
        this.goalSelector.addGoal(0, new MeleeAttackOnceGoal(this));
        this.goalSelector.addGoal(0, new DoubleMeleeAttackGoal(this));
        this.goalSelector.addGoal(0, new BiteAttackGoal(this));
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(0, new RoarGoal(this));
        this.goalSelector.addGoal(0, new SmashGoal(this));
        this.goalSelector.addGoal(0, new BreathGoal(this));
        this.goalSelector.addGoal(0, new DoNothingGoal(this));
        this.goalSelector.addGoal(0, new GuardGoal(this));
        this.goalSelector.addGoal(1, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 6.0F));
        this.goalSelector.addGoal(10, new LookAtPlayerGoal(this, Mob.class, 8.0F));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, IronGolem.class, true));
        this.targetSelector.addGoal(7, new NearestAttackableTargetGoal<>(this, Piglin.class, true));
        this.targetSelector.addGoal(6, new NearestAttackableTargetGoal<>(this, Axolotl.class, true));
        this.targetSelector.addGoal(5, new NearestAttackableTargetGoal<>(this, Raider.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Player.class, true));
        this.targetSelector.addGoal(3, new NearestAttackableTargetGoal<>(this, Allay.class, true));
        this.targetSelector.addGoal(4, new NearestAttackableTargetGoal<>(this, AbstractVillager.class, true));
        this.targetSelector.addGoal(1, new HurtByTargetGoal(this));
        this.targetSelector.addGoal(1, (new HurtByTargetGoal(this)).setAlertOthers(NehemothEntity.class));
        this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(2, new RandomStrollGoal(this, 0.5D, 25, true));
    }
    public void setAttackID(int id) {
        this.attackID = id;
        this.attacktick = 0;
        this.level().broadcastEntityEvent(this, (byte) -id);
    }

    @Override
    public void handleEntityEvent(byte id) {
        if (id <= 0) {
            this.attackID = Math.abs(id);
            this.attacktick = 0;
        } else if (id == 39) {
            this.stunnedTick = 40;
        }
        else if (id == 45) {
            this.attackCooldown = 10;
        }else {
            super.handleEntityEvent(id);
        }
    }
    //ai

    //tick
    @Override
    public boolean doHurtTarget(Entity target) {
        if (!this.level().isClientSide && this.attackID == 0) {
            if (this.random.nextInt(2) != 0) {
                this.attackID = MELEE_ATTACK;
            }
            else
                this.attackID = MELEE_ATTACK_ONCE;
        }
        return true;
    }

    public boolean onClimbable() {
        return this.isClimbing();
    }

    @Override
    public void tick() {
        super.tick();
        this.setMaxUpStep(1.0F);
        if (this.growlProgress == 80 && isAlive() && !isStone() && this.getTarget() == null)
        {
            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SoundRegistry.NEHEMOTH_IDLE.get(),SoundSource.HOSTILE, 0.5F, 0.5F + this.getRandom().nextFloat() * 0.1F);
        }
        if (this.attackCooldown > 0) {
            --this.attackCooldown;
        }
        if (!this.getPassengers().isEmpty() && this.getPassengers().get(0).isShiftKeyDown() && attackID == BITE_ATTACK) {
            this.getPassengers().get(0).setShiftKeyDown(false);
        }
        if(attackID == 0)
        {
            setRight(false);
        }
        if (level().isNight()) {
           setStone(false);
        }
        if(level().isClientSide)
        {
            if(isAlive() && getTarget() != null){
                Fromtheshadows.PROXY.playWorldSound(this, (byte) 0);
            }

        }
        if (this.attackID != 0) {
            ++this.attacktick;
        }
        if (this.breathCooldown > 0) {
            --this.breathCooldown;
        }
        if (this.biteCooldown > 0) {
            --this.biteCooldown;
        }
        if (this.roarCooldown > 0) {
            --this.roarCooldown;
        }
        if (this.smashCooldown > 0) {
            --this.smashCooldown;
        }
        if (this.smashCooldown == 0 && this.attackID == SMASH_ATTACK) {
            this.smashCooldown = 200;
        }
        if (this.biteCooldown == 0 && this.attackID == BITE_ATTACK) {
            this.biteCooldown = 250;
        }
        if (this.breathCooldown == 0 && this.attackID == BREATH_ATTACK) {
            this.breathCooldown = 250;
        }
        if (this.roarCooldown == 0 && this.attackID == ROAR_ATTACK) {
            this.roarCooldown = 4500;
        }
        if (this.growlProgress == 0) {
            this.growlProgress = 150;
        }
        if (this.growlProgress > 0) {
            --this.growlProgress;
        }
        if (this.stunnedTick > 0) {
            --this.stunnedTick;
        }
        if (isStone()) {
            setAttackID(0);
        }
        if(this.isAlive())
        {
            if(this.getTarget() != null && attackID != BREATH_ATTACK)
            {yBodyRot = yHeadRot;
                setYRot(yBodyRot);
                getLookControl().setLookAt(getTarget(),1.5F, 90.0F);
            }
    }
        }

    public void remove(Entity.RemovalReason removalReason) {
        Fromtheshadows.PROXY.clearSoundCacheFor(this);
        super.remove(removalReason);
    }


    protected PathNavigation createNavigation(Level p_33802_) {
        return new WallClimberNavigation(this, p_33802_);
    }

    protected boolean isImmobile() {
        return super.isImmobile() || this.attackCooldown > 0 || this.stunnedTick > 0 || isStone();
    }

    public boolean hasLineOfSight(Entity p_149755_) {
        return this.attackCooldown <= 0 || this.stunnedTick <= 0 && super.hasLineOfSight(p_149755_);
    }
    @Override
    public boolean killedEntity(ServerLevel p_216988_, LivingEntity p_216989_) {
        if(isAlive())
        {
            breathCooldown = 0;
            biteCooldown = 0;
          roarCooldown = 0;
          if(this.getTarget() == null)
          {

          }
        }
        return super.killedEntity(p_216988_, p_216989_);
    }
    public static float updateRotation(float angle, float targetAngle, float maxIncrease) {
        float f = Mth.wrapDegrees(targetAngle - angle);
        if (f > maxIncrease) {
            f = maxIncrease;
        }
        if (f < -maxIncrease) {
            f = -maxIncrease;
        }
        return angle + f;
    }
    private void smash(int distance) {
        double perpFacing = this.yBodyRot * (Math.PI / 180);
        double facingAngle = perpFacing + Math.PI / 2;
        int hitY = Mth.floor(this.getBoundingBox().minY - 0.5);
        double spread = Math.PI * 2;
        int arcLen = Mth.ceil(distance * spread);
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(3D))) {
                if(!(livingentity instanceof NehemothEntity)) {
                    livingentity.hurt(this.damageSources().mobAttack(this), (float) FTSConfig.nehemoth_ranged_damage);
                    this.strongKnockback(livingentity);
                }
            }
        }
        for (int i = 0; i < arcLen; i++) {
            double theta = (i / (arcLen - 1.0) - 0.5) * spread + facingAngle;
            double vx = Math.cos(theta);
            double vz = Math.sin(theta);
            double px = this.getX() + vx * distance;
            double pz = this.getZ() + vz * distance;
            if (ForgeEventFactory.getMobGriefingEvent(this.level(), this)) {
                int hitX = Mth.floor(px);
                int hitZ = Mth.floor(pz);
                BlockPos pos = new BlockPos(hitX, hitY, hitZ);
                BlockPos abovePos = new BlockPos(pos).above();
                BlockState block = level().getBlockState(pos);
                BlockState blockAbove = level().getBlockState(abovePos);

                if (level().getBlockState(pos) != Blocks.AIR.defaultBlockState() && !block.is(TagRegistry.NEHEMOTH_FALLING_BLOCK_IMMUNE)) {
                    FallingBlockEntity fallingBlockEntity = new FallingBlockEntity(level(), hitX + 0.5D, hitY + 0.5D, hitZ + 0.5D, block);
                    level().setBlock(pos, block.getFluidState().createLegacyBlock(), 3);
                    fallingBlockEntity.push(getRandom().nextGaussian() * 0.2, 0.2D + getRandom().nextGaussian() * 0.2D, getRandom().nextGaussian() * 0.2);
                    level().addFreshEntity(fallingBlockEntity);
                }

            }
        }

    }

    @Override
    public boolean isAlliedTo(Entity p_32665_) {
        if (p_32665_ == null) {
            return false;
        } else if (p_32665_ == this) {
            return true;
        }
        else if (p_32665_ instanceof Warden) {
            return true;
        }
        else if (p_32665_ instanceof NehemothEntity) {
            return true;
        }
        else if (p_32665_ instanceof BulldrogiothEntity) {
            return true;
        }else
            return super.isAlliedTo(p_32665_);
    }

    private void strongKnockback(Entity p_33340_) {
        double d0 = p_33340_.getX() - this.getX();
        double d1 = p_33340_.getZ() - this.getZ();
        double d2 = Math.max(d0 * d0 + d1 * d1, 0.001D);
        p_33340_.push(d0 / d2, 0.1D, d1 / d2);
    }
    protected void blockedByShield(LivingEntity p_33361_) {
            if (this.random.nextDouble() < 0.5D && this.attackID == BITE_ATTACK) {
                this.stunnedTick = 40;
                this.level().broadcastEntityEvent(this, (byte)39);
                p_33361_.push(this);
            }
            p_33361_.hurtMarked = true;
    }

    @Override
    protected int calculateFallDamage(float p_21237_, float p_21238_) {
        return 0;
    }
    public  List<LivingEntity> getEntityLivingBaseNearby(double distanceX, double distanceY, double distanceZ, double radius) {
        return getEntitiesNearby(LivingEntity.class, distanceX, distanceY, distanceZ, radius);
    }
    public <T extends Entity> List<T> getEntitiesNearby(Class<T> entityClass, double dX, double dY, double dZ, double r) {
        return level().getEntitiesOfClass(entityClass, getBoundingBox().inflate(dX, dY, dZ), e -> e != this && distanceTo(e) <= r + e.getBbWidth() / 2f && e.getY() <= getY() + dY);
    }
    private void meleeattack() {
        float range = 3.5f;
        float arc = 90;
        List<LivingEntity> entitiesHit = this.getEntityLivingBaseNearby(range, 3.5, range, range);
        for (LivingEntity entityHit : entitiesHit) {
            float entityHitAngle = (float) ((Math.atan2(entityHit.getZ() - this.getZ(), entityHit.getX() - this.getX()) * (180 / Math.PI) - 90) % 360);
            float entityAttackingAngle = this.yHeadRot % 360;
            if (entityHitAngle < 0) {
                entityHitAngle += 360;
            }
            if (entityAttackingAngle < 0) {
                entityAttackingAngle += 360;
            }
            float entityRelativeAngle = entityHitAngle - entityAttackingAngle;
            float entityHitDistance = (float) Math.sqrt((entityHit.getZ() - this.getZ()) * (entityHit.getZ() - this.getZ()) + (entityHit.getX() - this.getX()) * (entityHit.getX() - this.getX()));
            if (entityHitDistance <= range && (entityRelativeAngle <= arc / 2 && entityRelativeAngle >= -arc / 2) && (entityRelativeAngle >= 360 - arc / 2 == entityRelativeAngle <= -360 + arc / 2)) {
                if (!(entityHit instanceof NehemothEntity)) {
                    boolean flag = entityHit.hurt(this.damageSources().mobAttack(this), (float) this.getAttributeValue(Attributes.ATTACK_DAMAGE));
                    if(!entityHit.isBlocking()) {
                        entityHit.invulnerableTime = 0;
                        if (flag) {
                            entityHit.addEffect(new MobEffectInstance(EffectRegistry.BLEEDING.get(), 60), this);
                            entityHit.playSound(SoundRegistry.NEHEMOTH_GORE_SOUND.get(), 1f, 1F + this.getRandom().nextFloat() * 0.1F);
                        }
                    }
                }
            }
        }
    }

    public void positionRider(Entity passenger, Entity.MoveFunction moveFunc) {
        super.positionRider(passenger, moveFunc);
        if (hasPassenger(passenger)) {
            int tick = 5;
            if (attackID == BITE_ATTACK) {
                tick = this.attacktick;
                if (this.attacktick == 32) {
                    passenger.stopRiding();
                }
                this.setYRot(this.yRotO);
                this.yBodyRot = this.getYRot();
                this.yHeadRot = this.getYRot();
            }
            float radius = 0.3F;
            float math = 1f;
            float angle = (0.01745329251F * this.yBodyRot);
            float f = Mth.cos(this.getYRot() * ((float) Math.PI / 180F));
            float f1 = Mth.sin(this.getYRot() * ((float) Math.PI / 180F));
            double extraX = radius * Mth.sin((float) (Math.PI + angle));
            double extraZ = radius * Mth.cos(angle);
            double extraY = tick < 5 ? 0 : 0.2F * Mth.clamp(tick - 5, 0, 5);
            if (passenger.getBbHeight() < 1F)
            {
                passenger.setPos(this.getX() + f * math + extraX, this.getY() + extraY + 1.4F, this.getZ() + f1 * math + extraZ);
            }
            else {
                passenger.setPos(this.getX() + f * math + extraX, this.getY() + extraY + 0.7F, this.getZ() + f1 * math + extraZ);
            }
        }
    }

    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> p_21104_) {
        super.onSyncedDataUpdated(p_21104_);
    }
    private void roar() {
        if (this.isAlive()) {
            for(LivingEntity livingentity : this.level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(8D, 1D, 8D))) {
                if(!(livingentity instanceof NehemothEntity)) {
                    this.strongKnockback(livingentity);
                }
            }
        }
    }
    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource p_33034_) {
        return this.isStone() ? SoundEvents.STONE_HIT : SoundRegistry.NEHEMOTH_HURT.get();
    }

    @Override
    protected SoundEvent getDeathSound() {
        return this.isStone() ? SoundEvents.STONE_BREAK : SoundRegistry.NEHEMOTH_DEATH.get();
    }

    @Override
    public float getVoicePitch() {
        return this.isStone() ? 1F : 0.4F;

    }

    @Override
    protected void tickDeath() {
        if (this.isStone() && deathTime == 0) {
            remove(RemovalReason.KILLED);
            dropExperience();
        }
        else
        super.tickDeath();
    }

    @Override

    public boolean isPushable() {
        return !this.isStone();
    }
    @Nullable
    protected ResourceLocation getDefaultLootTable() {
        return this.isStone() ? STONE_LOOT : super.getDefaultLootTable();
    }


    @Override
    public boolean hurt(DamageSource p_21016_, float p_21017_) {
        Entity entity = p_21016_.getEntity();

        if (this.attackID == GUARD) {
            return false;
        }
        if (p_21016_.is(DamageTypeTags.IS_PROJECTILE)) {
            return super.hurt(p_21016_, p_21017_ / 4);
        }
        if(this.isStone()) {
            if (p_21016_.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                return super.hurt(p_21016_, p_21017_);
            }
                else {
                        return false;
                    }

        }
        return super.hurt(p_21016_, p_21017_);
    }

    public static boolean checkMonsterSpawnRules(EntityType<? extends Monster> p_33018_, ServerLevelAccessor p_33019_, MobSpawnType p_33020_, BlockPos p_33021_, RandomSource p_33022_) {
        return p_33019_.getDifficulty() != Difficulty.PEACEFUL && checkMobSpawnRules(p_33018_, p_33019_, p_33020_, p_33021_, p_33022_);
    }

    @Override
    public boolean dampensVibrations() {
        return true;
    }
    @Override
    public boolean canRiderInteract() {
        return false;
    }

    @Override
    protected boolean canRide(Entity p_20339_) {
        return false;
    }
    public boolean shouldRiderSit() {
        return false;
    }

    public void travel(Vec3 travelVector) {
        if (this.attackID != 0 || isStone()) {
            if (this.getNavigation().getPath() != null) {
                this.getNavigation().stop();
            }
            travelVector = Vec3.ZERO;
            super.travel(travelVector);
            return;
        }
        super.travel(travelVector);

    }
    @Nullable
    public LivingEntity getControllingPassenger() {
        return null;
    }

    class MeleeAttackOnceGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public MeleeAttackOnceGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 2;
        }

        public void start() {
            setRight(random.nextInt(2) != 0);
            this.nehemoth.setAttackID(2);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 47;
        }


        public void tick() {
            if (attacktick == 16) {
                if (attackTarget != null) {
                    nehemoth.getLookControl().tick();
                    nehemoth.getLookControl().setLookAt(attackTarget, 90.0F, 90.0F);
                }
            }
            if (attacktick == 17) {
                float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90));
                float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90));
                push(f1 * 0.3, 0, f2 * 0.3);
            }
            if (attacktick == 19) {
                meleeattack();
                playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3f, 0.5F + getRandom().nextFloat() * 0.1F);

            }
            getNavigation().recomputePath();
        }
    }

    class DoubleMeleeAttackGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public DoubleMeleeAttackGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 1;
        }

        public void start() {
            setRight(random.nextInt(2) != 0);
            this.nehemoth.setAttackID(1);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 72;
        }


        public void tick() {
            if (attacktick == 16) {
                if (attackTarget != null) {
                    nehemoth.getLookControl().tick();
                    nehemoth.getLookControl().setLookAt(attackTarget, 90.0F, 90.0F);
                }
            }
                if (attacktick == 17) {
                    float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90));
                    push(f1 * 0.3, 0, f2 * 0.3);
                }
            if (attacktick == 34) {
                if (attackTarget != null) {
                    nehemoth.getLookControl().tick();
                    nehemoth.getLookControl().setLookAt(attackTarget, 90.0F, 90.0F);
                }
            }
                if (attacktick == 35) {
                    float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90));
                    push(f1 * 0.3, 0, f2 * 0.3);
                }
                if (attacktick == 19) {
                    meleeattack();
                    playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3f, 0.5F + getRandom().nextFloat() * 0.1F);

                }

                if (attacktick == 41) {
                    meleeattack();
                    playSound(SoundEvents.PLAYER_ATTACK_SWEEP, 3f, 0.5F + getRandom().nextFloat() * 0.1F);
                }
            getNavigation().recomputePath();
            }
    }
    class BiteAttackGoal extends Goal {
            private final NehemothEntity nehemoth;
            private LivingEntity attackTarget;

            public BiteAttackGoal(NehemothEntity p_i45837_1_) {
                this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
                this.nehemoth = p_i45837_1_;
            }

            public boolean canUse() {
                this.attackTarget = this.nehemoth.getTarget();
                return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget) <= 3 && random.nextInt(2) == 0 && getVariant() == 0 && nehemoth.getPassengers().isEmpty() && biteCooldown == 0 && attackTarget.getBbWidth() < nehemoth.getBbWidth();
            }

            public void start() {
                this.nehemoth.setAttackID(BITE_ATTACK);
            }

            public void stop() {
                this.nehemoth.setAttackID(0);
                this.attackTarget = null;
                stopRiding();
            }
            @Override
            public boolean requiresUpdateEveryTick() {
                return true;
            }


            @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 48 && nehemoth.attackID != 0;
        }

            @Override
        public void tick() {
                if (nehemoth.attacktick > 20 && attackTarget != null && !this.nehemoth.hasPassenger(attackTarget)) {
                    stop();
                }
                if (attackTarget != null)
                    if (attacktick == 1) {
                        playSound(SoundRegistry.BITE_WARN.get(), 2f, 0.6F + getRandom().nextFloat() * 0.1F);
                    }
                    if (attacktick == 12) {
                        float f1 = (float) Math.cos(Math.toRadians(nehemoth.getYRot() + 90));
                        float f2 = (float) Math.sin(Math.toRadians(nehemoth.getYRot() + 90));

                        nehemoth.push(f1 * 0.2, 0, f2 * 1);
                    }
                if (nehemoth.attacktick == 16) {
                    if (nehemoth.distanceTo(attackTarget) < 3.5F && nehemoth.hasLineOfSight(attackTarget) && attackTarget != null) {
                        boolean flag = attackTarget.isBlocking();
                        if (!flag) {
                            if (attackTarget.getBbWidth() < nehemoth.getBbWidth() && nehemoth.getPassengers().isEmpty() && !attackTarget.isShiftKeyDown()) {
                                attackTarget.startRiding(nehemoth);
                            }
                        }
                    }
                }
                if (nehemoth.attacktick > 16) {
                    double d0 = nehemoth.getX() - attackTarget.getX();
                    double d2 = nehemoth.getZ() - attackTarget.getZ();
                    double d1 = nehemoth.getY() - 1 - attackTarget.getY();
                    double d3 = Math.sqrt(d0 * d0 + d2 * d2);
                    float f = (float) (Mth.atan2(d2, d0) * (180D / Math.PI)) - 90.0F;
                    float f1 = (float) (-(Mth.atan2(d1, d3) * (180D / Math.PI)));
                    attackTarget.setXRot(updateRotation(attackTarget.getXRot(), f1, 30F));
                    attackTarget.setYRot(updateRotation(attackTarget.getYRot(), f, 30F));
                }
                if (nehemoth.attacktick == 24 && nehemoth.hasPassenger(attackTarget)) {
                    nehemoth.level().playSound(null, nehemoth.getX(), nehemoth.getY(), nehemoth.getZ(), SoundRegistry.NEHEMOTH_BITE.get(), nehemoth.getSoundSource(), 2F, 0.8F + (nehemoth.random.nextFloat() - nehemoth.random.nextFloat()) * 0.2F);
            }
                    if (nehemoth.attacktick == 27 && nehemoth.hasPassenger(attackTarget)) {
                        attackTarget.hurt(nehemoth.damageSources().mobAttack(nehemoth), (float) nehemoth.getAttributeValue(Attributes.ATTACK_DAMAGE));
                        if (!attackTarget.isBlocking()) {

                            attackTarget.addEffect(new MobEffectInstance(EffectRegistry.HEAL_BLOCK.get(), 100), nehemoth);
                            heal((float) nehemoth.getAttributeValue(Attributes.ATTACK_DAMAGE)/2);

                        }
                        if (attackTarget.isBlocking() && nehemoth.random.nextDouble() < 0.5D) {
                            blockedByShield(nehemoth);
                            ejectPassengers();
                            stop();
                        }
                    }
                    if (nehemoth.attacktick == 32 && hasPassenger(attackTarget)) {
                        ejectPassengers();
                    }

            getNavigation().recomputePath();
        }
    }
    class SmashGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public SmashGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget) > 5.0D && onGround() && random.nextInt(2) == 0 && smashCooldown == 0 && !horizontalCollision;
        }

        public void start() {
            this.nehemoth.setAttackID(SMASH_ATTACK);
        }

        public void stop() {

            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 38 && nehemoth.attackID != 0;
        }

        public void tick() {
 if(attackTarget != null)
            if (attacktick < 9) {
                if (attackTarget != null) {
                    getLookControl().setLookAt(attackTarget,30F, 90.0F);
                }
            }
            if (attacktick == 2) {
                float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90));
                float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90));

                push(f1 * 0.4, 0, f2 * 0.4);
            }
            if (attacktick == 9) {
                playSound(SoundRegistry.NEHEMOTH_CHARGE.get(), 2f, 0.7F + getRandom().nextFloat() * 0.1F);
                setDeltaMovement((attackTarget.getX() - getX()) * 0.21D, 0.9D, (attackTarget.getZ() - getZ()) * 0.21D);
            }
            if (attacktick > 10 && onGround()) {
                attackCooldown = 10;
                nehemoth.level().broadcastEntityEvent(this.nehemoth, (byte)45);
                this.attackTarget = this.nehemoth.getTarget();
                if (attackTarget instanceof Player && attackTarget != null) {
                    Player player = (Player) attackTarget;
                    if (player.isBlocking())
                        player.disableShield(true);
                }
                ScreenShakeEntity.ScreenShake(level(), position(), 15, 0.2f, 0, 10);
                smash(4);
                playSound(SoundEvents.GENERIC_EXPLODE, 2f, 0.2F + getRandom().nextFloat() * 0.1F);
                stop();
            }
            getNavigation().recomputePath();
        }
    }
    class RoarGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public RoarGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && onGround() && roarCooldown == 0;
        }

        public void start() {
            this.nehemoth.setAttackID(ROAR_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 58;
        }

        public void tick() {
                if (attacktick < 58 && attackTarget.isAlive() && attackTarget != null) {
                    getLookControl().setLookAt(attackTarget,80F, 90.0F);
                }

                if(attacktick > 8 && attacktick < 52)
                {
                    roar();
                }
                if (attacktick == 8) {
                    yBodyRot = yHeadRot;
                    float f1 = (float) Math.cos(Math.toRadians(getYRot() + 90));
                    float f2 = (float) Math.sin(Math.toRadians(getYRot() + 90));
                    push(f1 * 0.3, 0, f2 * 0.3);
                    playSound(SoundRegistry.NEHEMOTH_ROAR.get(), 1f, 0.65F + getRandom().nextFloat() * 0.1F);
                    ScreenShakeEntity.ScreenShake(level(), position(), 50, 0.1f, 30, 30);
                }


            getNavigation().recomputePath();
        }
    }
    class GuardGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public GuardGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget) <= 3.0D && random.nextInt(6) == 0 && (attackTarget.swinging);
        }

        public void start() {
            this.nehemoth.setAttackID(GUARD);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }

        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 20;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            if (nehemoth.attacktick < 20 && attackTarget.isAlive()) {
                yBodyRot = yHeadRot;
                lookAt(attackTarget, 30.0F, 30.0F);
            }

            getNavigation().recomputePath();
        }


    }
    class DoNothingGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public DoNothingGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.JUMP, Goal.Flag.LOOK));
            this.nehemoth = p_i45837_1_;
        }
        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            BlockPos blockpos = new BlockPos.MutableBlockPos(getX(), getEyeY(), getZ());
            return (level().isDay() && level().canSeeSky(blockpos) && getVariant() == 0);
        }



        @Override
        public void tick() {
            attackID = 0;
            this.attackTarget = null;
            setStone(true);

        }
    }
    class BreathGoal extends Goal {
        private final NehemothEntity nehemoth;
        private LivingEntity attackTarget;

        public BreathGoal(NehemothEntity p_i45837_1_) {
            this.setFlags(EnumSet.of(Flag.JUMP, Flag.LOOK, Flag.MOVE));
            this.nehemoth = p_i45837_1_;
        }

        public boolean canUse() {
            this.attackTarget = this.nehemoth.getTarget();
            return attackTarget != null && this.nehemoth.attackID == 0 && distanceTo(attackTarget) <= 1024 && onGround() && random.nextInt(8) == 0 && getVariant() == 1 && breathCooldown == 0;
        }

        @Override
        public boolean requiresUpdateEveryTick() {
            return true;
        }


        public void start() {
            this.nehemoth.setAttackID(BREATH_ATTACK);
        }

        public void stop() {
            this.nehemoth.setAttackID(0);
            this.attackTarget = null;
        }


        @Override
        public boolean canContinueToUse() {
            return nehemoth.attacktick < 43;
        }

        public void tick() {
            stuckSpeedMultiplier = Vec3.ZERO;
            float radius1 = 0.2f;
            if (nehemoth.attacktick == 1) {

                nehemoth.playSound(SoundRegistry.SOUL_LASER_READY.get(), 2f, 0.3F + nehemoth.getRandom().nextFloat() * 0.1F);
            }
            if (nehemoth.attacktick == 3) {
                DoomBreathEntity doomBreath = new DoomBreathEntity(EntityRegistry.DOOM_BREATH.get(), nehemoth.level(), nehemoth, nehemoth.getX() + radius1 * Math.sin(-nehemoth.getYRot() * Math.PI / 180), nehemoth.getY() + 1.4, nehemoth.getZ() + radius1 * Math.cos(-nehemoth.getYRot() * Math.PI / 180), (float) ((nehemoth.yHeadRot + 90) * Math.PI / 180), (float) (-nehemoth.getXRot() * Math.PI / 180), 10);
                nehemoth.level().addFreshEntity(doomBreath);
            }
            if (nehemoth.attacktick == 18) {
                ScreenShakeEntity.ScreenShake(level(), position(), 30, 0.1f, 30, 30);
                nehemoth.playSound(SoundRegistry.SOUL_LASER.get(), 3f, 0.8F + nehemoth.getRandom().nextFloat() * 0.1F);
            }
            if (nehemoth.attacktick >= 15) {
                if (attackTarget != null) {
                    nehemoth.getLookControl().setLookAt(attackTarget.getX(), attackTarget.getY() + attackTarget.getBbHeight() / 2, attackTarget.getZ(), 1.5f, 90);
                }
            }
            if (nehemoth.attacktick <= 14) {
                if (attackTarget != null) {
                    getLookControl().setLookAt(attackTarget,30F, 90.0F);
                }
            }

            getNavigation().recomputePath();
        }
    }
}
