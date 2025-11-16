package net.sonmok14.fromtheshadows.server.world.biome;

public enum BiomeEntryType {
    REGISTRY_NAME(false), BIOME_TAG(false), BIOME_DICT(true), BIOME_CATEGORY(true);

    private boolean depreciated;

    BiomeEntryType(boolean depreciated){
        this.depreciated = depreciated;
    }

    public boolean isDepreciated() {
        return depreciated;
    }
}
