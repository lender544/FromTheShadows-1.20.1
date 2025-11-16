package net.sonmok14.fromtheshadows.client.particle;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sonmok14.fromtheshadows.client.renderer.misc.LightningBoltData;
import net.sonmok14.fromtheshadows.client.renderer.misc.LightningRender;
import org.joml.Vector4f;

public class LightningParticle extends Particle {
    float progress = (float) age / (float) lifetime;
    float alpha = 0.8F - progress;
    private LightningRender lightningRender = new LightningRender();
    private LightningRender lightningRenderInner = new LightningRender();
    private LightningRender lightningRenderBg = new LightningRender();

    private LightningBoltData redBoltData; // 번개 데이터를 필드로 저장
    private LightningBoltData whiteBoltData;

    public LightningParticle(ClientLevel world, double x, double y, double z, double xd, double yd, double zd) {
        super(world, x, y, z);
        this.setSize(5F, 5F);
        this.x = x;
        this.y = y;
        this.z = z;
        this.xd = 0 + random.nextFloat() * 0.5;
        this.yd = 0 + random.nextFloat() * 0.5;
        this.zd = 0 + random.nextFloat() * 0.5;

        Vec3 lightningTo = findLightningToPos(level, x, y, z, (int) (5 + random.nextFloat() * 2.5));
        Vec3 to = lightningTo.subtract(x, y, z);
        int sections =  4 * this.lifetime;
        int boltLifetime = (int) (Math.ceil(to.length()) + random.nextFloat() * 12);

        float commonBaseWidth = 0.2F + random.nextFloat() * 0.2F;
        float commonBranchWidth = 0.01F + random.nextFloat() * 0.2F;
        float commonFadeStart = 0.2F;
        float commonFadeEnd = 0.6F;
        float commonOverallAlpha = 0.5F;
        float commonSize = 0.03F + random.nextFloat() * 0.15F;
        float innerSizeRatio = 0.6F;
        float innerSize = commonSize * innerSizeRatio;

        int sections2 = (int) (to.length() * 3);

        long sharedSeed = this.random.nextLong();

        LightningBoltData.BoltRenderInfo boltData = new LightningBoltData.BoltRenderInfo(
                commonBaseWidth, commonBranchWidth, commonFadeStart, commonFadeEnd,
                new Vector4f(0.35f, 0.25f, 0.95f, 0.75f), commonOverallAlpha
        );

        LightningBoltData.BoltRenderInfo innerBoltData = new LightningBoltData.BoltRenderInfo(
                commonBaseWidth, commonBranchWidth, commonFadeStart, commonFadeEnd,
                new Vector4f(1f, 1f, 1f, 1f - random.nextFloat() * 0.15F), commonOverallAlpha
        );

        float whiteBoltYOffset = -0.1F;
        this.redBoltData = new LightningBoltData(boltData, Vec3.ZERO, to, sections)
                .size(commonSize)
                .lifespan(boltLifetime + 1)
                .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                .seed(sharedSeed);
        lightningRender.update(this, this.redBoltData, 1.0F);

        this.whiteBoltData = new LightningBoltData(innerBoltData, Vec3.ZERO, to, sections)
                .size(innerSize)
                .lifespan(boltLifetime + 1)
                .yOffset(whiteBoltYOffset) // yOffset 값 설정
                .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE)
                .seed(sharedSeed);
        lightningRenderInner.update(this, this.whiteBoltData, 1.0F);

        LightningBoltData.BoltRenderInfo BgBoltData = new LightningBoltData.BoltRenderInfo(
                commonBaseWidth, commonBranchWidth, commonFadeStart, commonFadeEnd,
                new Vector4f(0.15f, 0.25f, 0.95f, 0.15f), commonOverallAlpha
        );
        LightningBoltData BgBolt = new LightningBoltData(BgBoltData, Vec3.ZERO, to, sections)
                .size(0.2F + random.nextFloat() * 0.25F)
                .lifespan(boltLifetime)
                .spawn(LightningBoltData.SpawnFunction.CONSECUTIVE);
        lightningRenderBg.update(this, BgBolt, 1.0F);
    }

    public boolean shouldCull() {
        return false;
    }

    private Vec3 findLightningToPos(ClientLevel world, double x, double y, double z, int range) {
        Vec3 vec3 = new Vec3(x, y, z);
        for (int i = 0; i < 10; i++) {
            Vec3 vec31 = vec3.add(random.nextFloat() * range - range / 2F, random.nextFloat() * range - range / 2F, random.nextFloat() * range - range / 2F);
            if (canSeeBlock(vec3, vec31)) {
                return vec31;
            }
        }
        return vec3;
    }

    private boolean canSeeBlock(Vec3 from, Vec3 to) {
        BlockHitResult result = this.level.clip(new ClipContext(from, to, ClipContext.Block.COLLIDER, ClipContext.Fluid.NONE, null));
        return Vec3.atCenterOf(result.getBlockPos()).distanceTo(to) < 3.0F;
    }


    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        this.xd = 0;
        this.yd = 0;
        this.zd = 0;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.move(this.xd, this.yd, this.zd);
            this.yd -= (double) this.gravity;
        }
    }


    public void render(VertexConsumer consumer, Camera camera, float partialTick) {
        MultiBufferSource.BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
        Vec3 cameraPos = camera.getPosition();
        float x = (float) (Mth.lerp((double) partialTick, this.xo, this.x));
        float y = (float) (Mth.lerp((double) partialTick, this.yo, this.y));
        float z = (float) (Mth.lerp((double) partialTick, this.zo, this.z));
        PoseStack posestack = new PoseStack();
        posestack.pushPose();
        posestack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);
        posestack.translate(x, y, z);
        lightningRender.render(partialTick, posestack, multibuffersource$buffersource);
        posestack.translate(0, this.whiteBoltData.getYOffset(), 0);
        lightningRender.render(partialTick, posestack, multibuffersource$buffersource);
        lightningRenderInner.render(partialTick, posestack, multibuffersource$buffersource);
        multibuffersource$buffersource.endBatch();
        posestack.popPose();
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.CUSTOM;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Factory implements ParticleProvider<SimpleParticleType> {

        public Factory() {
        }

        public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
            return new LightningParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
        }
    }
}
