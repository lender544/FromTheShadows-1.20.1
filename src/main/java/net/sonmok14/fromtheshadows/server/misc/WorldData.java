package net.sonmok14.fromtheshadows.server.misc;

import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.storage.DimensionDataStorage;

import java.util.*;
import java.util.stream.Collectors;

public class WorldData extends SavedData {
    private boolean hasBeenUsed = false;

    public static WorldData load(CompoundTag tag) {
        WorldData data = new WorldData();
        data.hasBeenUsed = tag.getBoolean("HasBeenUsed");
        return data;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("HasBeenUsed", hasBeenUsed);
        return tag;
    }

    public boolean hasBeenUsed() {
        return hasBeenUsed;
    }

    public void markUsed() {
        hasBeenUsed = true;
        setDirty(); // 저장 데이터 변경됨 표시
    }

    public static WorldData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(WorldData::load, WorldData::new, "my_item_use_tracker");
    }

}
