package net.sonmok14.fromtheshadows.server.world.biome;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.config.BiomeConfig;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;
import net.sonmok14.fromtheshadows.server.utils.registry.EntityRegistry;
import org.apache.commons.lang3.tuple.Pair;

@Mod.EventBusSubscriber(modid = Fromtheshadows.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FTSWorldRegistry {


    public static void modifyStructure(Holder<Structure> structure, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (structure.is(BuiltinStructures.WOODLAND_MANSION) && FTSConfig.clericSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.clericSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.PILLAGER_OUTPOST) && FTSConfig.clericSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.clericSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_COLD) && FTSConfig.bulldrogiothShipwreckSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.bulldrogiothShipwreckSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_WARM) && FTSConfig.bulldrogiothShipwreckSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.bulldrogiothShipwreckSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.FORTRESS) && FTSConfig.nehemothFortressSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), 20, 1, 1));
        }
    }

    private static ResourceLocation getBiomeName(Holder<Biome> biome) {
        return biome.unwrap().map((resourceKey) -> resourceKey.location(), (noKey) -> null);
    }

    public static boolean testBiome(Pair<String, SpawnBiomeData> entry, Holder<Biome> biome) {
        boolean result = false;
        try {
            result = BiomeConfig.test(entry, biome, getBiomeName(biome));
        } catch (Exception e) {
            Fromtheshadows.LOGGER.warn("could not test biome config for " + entry.getLeft() + ", defaulting to no spawns for mob");
            result = false;
        }
        return result;
    }


    public static void addBiomeSpawns(Holder<Biome> biome, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (testBiome(BiomeConfig.bulldrogith, biome) && FTSConfig.bulldrogiothSpawnRate > 0) {
            builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.bulldrogiothSpawnRate, 1, 1));
        }
        if (testBiome(BiomeConfig.nehemoth, biome) && FTSConfig.nehemothSpawnRate > 0) {
            builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), FTSConfig.nehemothSpawnRate, 1, 1));
        }
        if (testBiome(BiomeConfig.murlock, biome) && FTSConfig.froglinSpawnRate > 0) {
            builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.FROGLIN.get(), FTSConfig.froglinSpawnRate, 1, 3));
        }

        if (testBiome(BiomeConfig.soulnehemoth, biome) && FTSConfig.soulfirenehemothSpawnRate > 0) {
            builder.getMobSpawnSettings().getSpawner(MobCategory.MONSTER).add(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), FTSConfig.soulfirenehemothSpawnRate, 1, 1));
        }
    }
}
