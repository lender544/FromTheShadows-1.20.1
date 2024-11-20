package net.sonmok14.fromtheshadows.server.utils;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class SoundHelper {
    public static void playSoundBasedOnDistance(Entity entity, SoundEvent soundEvent, float maxDistance, float volume, float pitch) {
        if (entity.level().isClientSide) {
            return; // Ensure this runs only on the server side
        }

        // Iterate through all players in the world
        for (Player player : entity.level().players()) {
            double distance = player.distanceTo(entity);

            // Check if the player is within the maximum hearing distance
            if (distance <= maxDistance) {
                // Calculate the volume based on distance (linear falloff)
                float adjustedVolume = volume * (2.0f - (float)(distance / maxDistance));
                if (adjustedVolume > 0) {
                    entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), soundEvent, SoundSource.HOSTILE, adjustedVolume, pitch);
                }
            }
        }
    }
}
