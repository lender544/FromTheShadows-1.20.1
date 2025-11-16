package net.sonmok14.fromtheshadows.client.models.entity;

import net.minecraft.ChatFormatting;
import net.minecraft.resources.ResourceLocation;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;
import net.sonmok14.fromtheshadows.server.entity.mob.NehemothEntity;
import software.bernie.geckolib.constant.DataTickets;
import software.bernie.geckolib.core.animatable.model.CoreGeoBone;
import software.bernie.geckolib.core.animation.AnimationState;
import software.bernie.geckolib.model.GeoModel;
import software.bernie.geckolib.model.data.EntityModelData;

public class NehemothModel extends GeoModel<NehemothEntity> {

    @Override
    public ResourceLocation getAnimationResource(NehemothEntity entity) {
        return new ResourceLocation(Fromtheshadows.MODID, "animations/nehemoth.animation.json");
    }

    @Override
    public ResourceLocation getModelResource(NehemothEntity entity) {
        return new ResourceLocation(Fromtheshadows.MODID, "geo/nehemoth.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(NehemothEntity entity) {
        String s = ChatFormatting.stripFormatting(entity.getName().getString());
        if(entity.isStone())
        {
            return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_stone.png");
        }
        if(entity.getVariant() == 1 && s != null)
        {
            return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/soul_retexture.png");
        }

        return new ResourceLocation(Fromtheshadows.MODID, "textures/entity/nehemoth_retexture.png");
    }

    @Override
    public void setCustomAnimations(NehemothEntity animatable, long instanceId,
                                    AnimationState<NehemothEntity> animationState) {
        super.setCustomAnimations(animatable, instanceId, animationState);
        CoreGeoBone head2 = this.getAnimationProcessor().getBone("head");
        CoreGeoBone head = this.getAnimationProcessor().getBone("headrotate");
        CoreGeoBone jaw = this.getAnimationProcessor().getBone("jaw");
        EntityModelData entityData = animationState.getData(DataTickets.ENTITY_MODEL_DATA);
        head.setRotX(entityData.headPitch() * 0.01F);
        head.setRotY(entityData.netHeadYaw() * 0.01F);
        head.setRotX(-1F);
        head.updateScale(0.98f,0.98f,0.98f);
        if(animatable.hurtTime != 0)
        {
            head2.setRotX(animationState.getLimbSwingAmount() * -0.25f);
            jaw.setRotX(animationState.getLimbSwingAmount() * -0.5f);
        }

    }
}
