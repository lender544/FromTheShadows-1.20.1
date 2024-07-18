package net.sonmok14.fromtheshadows.server.config;


import net.sonmok14.fromtheshadows.server.world.biome.BiomeEntryType;
import net.sonmok14.fromtheshadows.server.world.biome.SpawnBiomeData;

public class DefaultBiomes {

    public static final SpawnBiomeData EMPTY = new SpawnBiomeData();

    public static final SpawnBiomeData NEHEMOTH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "minecraft:is_ocean", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:is_mushroom", 0);

    public static final SpawnBiomeData BULLDROGIOTH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:mangrove_swamp", 1)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_river", 2)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_beach", 3);


    public static final SpawnBiomeData MURLOCK = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "minecraft:is_overworld", 0)
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, false, "forge:is_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, true, "minecraft:mangrove_swamp", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:lush_caves", 1);

    public static final SpawnBiomeData SOUL_NEHEMOTH = new SpawnBiomeData()
            .addBiomeEntry(BiomeEntryType.BIOME_TAG, true, "forge:no_default_monsters", 0)
            .addBiomeEntry(BiomeEntryType.REGISTRY_NAME, false, "minecraft:soul_sand_valley", 0);


}
