package net.sonmok14.fromtheshadows.client.particle;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.sonmok14.fromtheshadows.client.renderer.FTSRenderType;

public class ShadowParticle extends TextureSheetParticle {
    @SuppressWarnings("unused")
    private final SpriteSet sprites;
    private final float initialSize;
    private float swirlAngle;

    public ShadowParticle(ClientLevel world, double x, double y, double z,
                                double vx, double vy, double vz, SpriteSet sprites) {
        super(world, x, y, z, vx, vy, vz);
        this.sprites = sprites;


        this.lifetime = 40 + world.random.nextInt(20);


        this.initialSize = 0.5f + world.random.nextFloat() * 0.65f;
        this.quadSize = initialSize;


        this.alpha = 0.1f;


        this.swirlAngle = world.random.nextFloat() * (float)Math.PI * 2;


        this.pickSprite(sprites);
    }

    @Override
    public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
        float var = (age + partialTicks)/(float)lifetime;
        alpha = 0.2f * ((float) (1 - Math.exp(5 * (var - 1)) - Math.pow(2000, -var)));
        if (alpha < 0.01) alpha = 0.01f;
        float progress = (float) age / lifetime;
        quadSize = initialSize + initialSize * progress;

        super.render(buffer, renderInfo, partialTicks);
    }


    @Override
    public void tick() {
        super.tick();

        float progress = (float) age / lifetime;


        if (progress < 0.2f) {
            alpha = (progress / 0.2f) * 0.5f;
        } else {
            alpha = (1f - progress) * 0.5f;
        }


        quadSize = initialSize + initialSize * progress;


        swirlAngle += 0.1f;
        xd += Math.cos(swirlAngle) * 0.01;
        zd += Math.sin(swirlAngle) * 0.01;


        xd += (random.nextDouble() - 0.2) * 0.002;
        yd += (random.nextDouble() - 0.2) * 0.002;
        zd += (random.nextDouble() - 0.2) * 0.002;


        setColor(0.45f, 0.45f, 0.45f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return FTSRenderType.PARTICLE_SHEET_TRANSLUCENT_NO_DEPTH;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprite;

        public Provider(SpriteSet p_108492_) {
            this.sprite = p_108492_;
        }

        public Particle createParticle(SimpleParticleType p_108503_, ClientLevel p_108504_, double p_108505_, double p_108506_, double p_108507_, double p_108508_, double p_108509_, double p_108510_) {
            ShadowParticle shadowParticle = new ShadowParticle(p_108504_, p_108505_, p_108506_, p_108507_, p_108508_,p_108509_,p_108510_,sprite);
            shadowParticle.pickSprite(this.sprite);
            return shadowParticle;
        }
    }
    }
