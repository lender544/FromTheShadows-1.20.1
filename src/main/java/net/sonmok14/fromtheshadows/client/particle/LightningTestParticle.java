package net.sonmok14.fromtheshadows.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.sonmok14.fromtheshadows.server.utils.registry.ParticleRegistry;

public class LightningTestParticle extends Particle {
    private double flowX;
    private double flowY;
    private double flowZ;

    private double originX;

    private double originY;

    private double originZ;

    private double targetX;

    private double targetY;

    private double targetZ;

    protected LightningTestParticle(ClientLevel clientLevel, double x, double y, double z) {
        super(clientLevel, x, y, z);
        flowX = xd;
        flowY = yd;
        flowZ = zd;
        originX = x;
        originY = y;
        originZ = z;
        targetX = flowX;
        targetY = flowY;
        targetZ = flowZ;
        this.lifetime = 40 + this.random.nextInt(24);
        this.gravity = 0;
    }

    public void tick() {
        Vec3 vec3 = distanceVec(targetX, targetY, targetZ);
        if (distanceVec(flowX, flowY, flowZ).length() < 1) {
            targetX = originX;
            targetY = originY;
            targetZ = originZ;
        } else if (distanceVec(originX, originY, originZ).length() < 1) {
            targetX = flowX;
            targetY = flowY;
            targetZ = flowZ;
        }
        Vec3 movement = vec3.normalize().scale(5F);

        this.level.addParticle(ParticleRegistry.LIGHTNING.get(), movement.x, movement.y, movement.z, 0, 0, 0);
        super.tick();
    }
    private Vec3 distanceVec(double x, double y, double z) {
        return new Vec3(x, y, z).subtract(this.x, this.y, this.z);
    }
    @Override
    public void render(VertexConsumer p_107261_, Camera p_107262_, float p_107263_) {

    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.NO_RENDER;
    }



    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LightningTestParticle(worldIn, x, y, z);
        }
    }

}
