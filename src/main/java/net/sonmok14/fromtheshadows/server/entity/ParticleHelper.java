package net.sonmok14.fromtheshadows.server.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;

public class ParticleHelper {
    public static void spawnCircularParticles(Entity entity, double radius, int particleCount) {
        Level level = entity.level();
        if (level.isClientSide) {
            return;
        }

        double centerX = entity.getX();
        double centerY = entity.getY() + entity.getBbHeight() / 2; // Adjust to spawn around the center height
        double centerZ = entity.getZ();

        for (int i = 0; i < particleCount; i++) {
            double angle = 2 * Math.PI * i / particleCount;
            double offsetX = radius * Math.cos(angle);
            double offsetZ = radius * Math.sin(angle);

            double particleX = centerX + offsetX;
            double particleY = centerY;
            double particleZ = centerZ + offsetZ;

            // Spawns the particle
            level.addParticle(ParticleTypes.FLAME, particleX, particleY, particleZ, 0, 0, 0);
        }
    }
}
