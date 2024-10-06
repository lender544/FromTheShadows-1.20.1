package net.sonmok14.fromtheshadows.server.utils.registry;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructures;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraftforge.common.world.ModifiableStructureInfo;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;

public class WorldRegistry {

    public static void modifyStructure(Holder<Structure> structure, ModifiableStructureInfo.StructureInfo.Builder builder) {
        if (structure.is(BuiltinStructures.WOODLAND_MANSION) && FTSConfig.clericSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.clericSpawnRate, 1, 2));
        }
        if (structure.is(BuiltinStructures.PILLAGER_OUTPOST) && FTSConfig.clericSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.CLERIC.get(), FTSConfig.clericSpawnRate, 1, 2));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_COLD) && FTSConfig.bulldrogiothShipwreckSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.bulldrogiothShipwreckSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.OCEAN_RUIN_WARM) && FTSConfig.bulldrogiothShipwreckSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.BULLDROGIOTH.get(), FTSConfig.bulldrogiothShipwreckSpawnRate, 1, 1));
        }
        if (structure.is(BuiltinStructures.FORTRESS) && FTSConfig.nehemothFortressSpawnRate > 0) {
            builder.getStructureSettings().getOrAddSpawnOverrides(MobCategory.MONSTER).addSpawn(new MobSpawnSettings.SpawnerData(EntityRegistry.NEHEMOTH.get(), FTSConfig.nehemothFortressSpawnRate, 1, 1));
        }
    }
}
