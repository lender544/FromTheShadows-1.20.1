package net.sonmok14.fromtheshadows.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.sonmok14.fromtheshadows.client.models.EntityTrackingProjectileModel;
import net.sonmok14.fromtheshadows.client.models.FroglinVomitModel;
import net.sonmok14.fromtheshadows.server.entity.projectiles.EntityTrackingProjectile;
import net.sonmok14.fromtheshadows.server.entity.projectiles.FrogVomit;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

public class EntityTrackingProjectileRenderer extends GeoEntityRenderer<EntityTrackingProjectile> {

    public EntityTrackingProjectileRenderer(EntityRendererProvider.Context renderManagerIn) {
        super(renderManagerIn, new EntityTrackingProjectileModel());
    }

    @Override
    public void reRender(BakedGeoModel model, PoseStack poseStack, MultiBufferSource bufferSource, EntityTrackingProjectile animatable, RenderType renderType, VertexConsumer buffer, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        super.reRender(model, poseStack, bufferSource, animatable, renderType, buffer, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void render(EntityTrackingProjectile entity, float entityYaw, float partialTicks, PoseStack stack, MultiBufferSource bufferIn, int packedLightIn) {
        stack.pushPose();
        stack.scale(2, 2, 2);
        super.render(entity, entityYaw, partialTicks, stack, bufferIn, packedLightIn);
        stack.popPose();
    }
}

