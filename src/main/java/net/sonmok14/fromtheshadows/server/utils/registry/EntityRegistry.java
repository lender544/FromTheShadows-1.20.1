package net.sonmok14.fromtheshadows.server.utils.registry;

import com.google.common.base.Predicates;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.entity.*;
import net.sonmok14.fromtheshadows.server.entity.projectiles.*;

import java.util.function.Predicate;


@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES,
            Fromtheshadows.MODID);


    public static final RegistryObject<EntityType<NehemothEntity>> NEHEMOTH = ENTITY_TYPES.register("nehemoth",
            () -> EntityType.Builder.of(NehemothEntity::new, MobCategory.MONSTER).sized(1.25f, 3.65f)
                    .fireImmune().clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "nehemoth").toString()));
    public static final RegistryObject<EntityType<FroglinEntity>> FROGLIN = ENTITY_TYPES.register("froglin",
            () -> EntityType.Builder.of(FroglinEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F)
                   .clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "froglin").toString()));
    public static final RegistryObject<EntityType<BulldrogiothEntity>> BULLDROGIOTH = ENTITY_TYPES.register("bulldrogioth",
            () -> EntityType.Builder.of(BulldrogiothEntity::new, MobCategory.MONSTER).sized(2.5f, 2.9f)
                    .clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "bulldrogioth").toString()));
    public static final RegistryObject<EntityType<ClericEntity>> CLERIC = ENTITY_TYPES.register("cleric",
            () -> EntityType.Builder.of(ClericEntity::new, MobCategory.MONSTER).sized(0.6F, 1.95F)
                    .clientTrackingRange(9).build(new ResourceLocation(Fromtheshadows.MODID, "cleric").toString()));
    public static final RegistryObject<EntityType<ScreenShakeEntity>> SCREEN_SHAKE = ENTITY_TYPES.register("screen_shake", () -> EntityType.Builder.<ScreenShakeEntity>of(ScreenShakeEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":screen_shake"));

    public static final RegistryObject<EntityType<DoomBreathEntity>> DOOM_BREATH = ENTITY_TYPES.register("doom_breath", () -> EntityType.Builder.<DoomBreathEntity>of(DoomBreathEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":doom_breath"));

    public static final RegistryObject<EntityType<PlayerBreathEntity>> PLAYER_BREATH = ENTITY_TYPES.register("player_breath", () -> EntityType.Builder.<PlayerBreathEntity>of(PlayerBreathEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":player_breath"));

    public static final RegistryObject<EntityType<PlayerProjectileEntity>> PLAYER_PROJECTILE = ENTITY_TYPES.register("player_projectile", () -> EntityType.Builder.<PlayerProjectileEntity>of(PlayerProjectileEntity::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":player_projectile"));

    public static final RegistryObject<EntityType<CoralThornEntity>> CORAL_THORN = ENTITY_TYPES.register("coral_thorn", () -> EntityType.Builder.<CoralThornEntity>of(CoralThornEntity::new, MobCategory.MISC)
            .sized(1.0f, 1.0f)
            .noSummon()
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":coral_thorn"));
    public static final RegistryObject<EntityType<ThrowingDaggerEntity>> THROWING_DAGGER = ENTITY_TYPES.register("throwing_dagger", () -> EntityType.Builder.<ThrowingDaggerEntity>of(ThrowingDaggerEntity::new, MobCategory.MISC)
            .sized(1.0f, 1.0f)
            .noSummon()
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":throwing_dagger"));
    public static final RegistryObject<EntityType<FrogVomit>> FROGLIN_VOMIT = ENTITY_TYPES.register("froglin_vomit", () -> EntityType.Builder.<FrogVomit>of(FrogVomit::new, MobCategory.MISC)
            .noSummon()
            .sized(1.0f, 1.0f)
            .setUpdateInterval(Integer.MAX_VALUE)
            .build(Fromtheshadows.MODID + ":froglin_vomit"));

    public static Predicate<LivingEntity> buildPredicateFromTag(TagKey<EntityType<?>> entityTag){
        if(entityTag == null){
            return Predicates.alwaysFalse();
        }else{
            return (com.google.common.base.Predicate<LivingEntity>) e -> e.isAlive() && e.getType().is(entityTag);
        }
    }

    public static boolean rollSpawn(int rolls, RandomSource random, MobSpawnType reason){
        if(reason == MobSpawnType.SPAWNER){
            return true;
        }else{
            return rolls <= 0 || random.nextInt(rolls) == 0;
        }
    }

    @SubscribeEvent
    public static void initializeAttributes(EntityAttributeCreationEvent event) {
        event.put(EntityRegistry.NEHEMOTH.get(), NehemothEntity.createAttributes().build());
        SpawnPlacements.register(EntityRegistry.NEHEMOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, NehemothEntity::canNehemothSpawn);

        event.put(EntityRegistry.FROGLIN.get(), FroglinEntity.createAttributes().build());
        SpawnPlacements.register(EntityRegistry.FROGLIN.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, FroglinEntity::canFroglinSpawn);

        event.put(EntityRegistry.BULLDROGIOTH.get(), BulldrogiothEntity.createAttributes().build());
        SpawnPlacements.register(EntityRegistry.BULLDROGIOTH.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, BulldrogiothEntity::canBulldrogiothSpawn);

        event.put(EntityRegistry.CLERIC.get(), ClericEntity.createAttributes().build());
    }

}
