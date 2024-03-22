package net.sonmok14.fromtheshadows.server.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.tags.BiomeTags;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;
import net.sonmok14.fromtheshadows.server.utils.registry.EntityRegistry;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FTSWorldRegistry {


    public static void modifyStructure(Holder<Structure> structure, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (structure.is(BuiltinStructures.WOODLAND_MANSION) && FTSConfig.SERVER.clericSpawnRate.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.SERVER.clericSpawnRate.get(), 1, 2));
        }
        if (structure.is(BuiltinStructures.PILLAGER_OUTPOST) && FTSConfig.SERVER.clericSpawnRate.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.SERVER.clericSpawnRate.get(), 1, 2));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_COLD) && FTSConfig.SERVER.bulldrogiothShipwreckSpawnRate.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.SERVER.bulldrogiothShipwreckSpawnRate.get(), 1, 1));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_WARM) && FTSConfig.SERVER.bulldrogiothShipwreckSpawnRate.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.SERVER.bulldrogiothShipwreckSpawnRate.get(), 1, 1));
        }
        if (structure.is(BuiltinStructures.FORTRESS) && FTSConfig.SERVER.nehemothFortressSpawnRate.get() > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 20, 1, 1));
        }
    }


    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (biome.containsTag(BiomeTags.IS_OVERWORLD) && !biome.is(Biomes.DEEP_DARK) && !biome.is(Tags.Biomes.IS_VOID)) {
            if (FTSConfig.SERVER.bulldrogiothSpawnRate.get() > 0) {
                if (biome.is(BiomeTags.IS_BEACH) || biome.is(Biomes.RIVER) || biome.is(Tags.Biomes.IS_SWAMP)) {
                    builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.SERVER.bulldrogiothSpawnRate.get(), 1, 1));
                }
            }
            if (FTSConfig.SERVER.nehemothSpawnRate.get() > 0) {
                if (biome.is(BiomeTags.IS_OVERWORLD) || biome.is(BiomeTags.IS_OCEAN) || biome.is(BiomeTags.IS_DEEP_OCEAN)) {
                    builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), FTSConfig.SERVER.nehemothSpawnRate.get(), 1, 1));
                }
            }
            if (FTSConfig.SERVER.froglinSpawnRate.get() > 0) {
                if (biome.is(Biomes.LUSH_CAVES) || biome.is(Tags.Biomes.IS_SWAMP)) {
                    builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.FROGLIN.get(), FTSConfig.SERVER.froglinSpawnRate.get(), 1, 3));
                }
            }
        }
        if (FTSConfig.SERVER.soulfirenehemothSpawnRate.get() > 0) {
            if (biome.is(Biomes.SOUL_SAND_VALLEY)) {
                builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), FTSConfig.SERVER.soulfirenehemothSpawnRate.get(), 1, 1));
            }
        }
    }

}
