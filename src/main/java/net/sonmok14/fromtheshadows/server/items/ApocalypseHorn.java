package net.sonmok14.fromtheshadows.server.items;

import net.minecraft.ChatFormatting;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.sonmok14.fromtheshadows.server.entity.projectiles.ScreenShakeEntity;
import net.sonmok14.fromtheshadows.server.misc.WorldData;
import net.sonmok14.fromtheshadows.server.utils.registry.EffectRegistry;
import net.sonmok14.fromtheshadows.server.utils.registry.ParticleRegistry;

import java.util.List;

public class ApocalypseHorn extends Item {

    public ApocalypseHorn(Properties p_41383_) {
        super(new Item.Properties().stacksTo(1).rarity(Rarity.RARE));
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        ScreenShakeEntity.ScreenShake(level, player.position(), 5, 0.1f, 40, 10);
        player.playSound(SoundEvents.GOAT_HORN_SOUND_VARIANTS.get(5).value(),4f,0.2f);
        if (level instanceof ClientLevel clientLevel) {
            for (int i = 0; i < 15; i++) {
                double angle = 2 * Math.PI * i / 15;
                double vx = Math.cos(angle) * 1;
                double vz = Math.sin(angle) * 1;
                clientLevel.addAlwaysVisibleParticle(
                       ParticleRegistry.SHADOW.get(),
                        player.getX(), player.getY(), player.getZ(),
                        vx, 0, vz
                );
            }
        }
        if (!level.isClientSide) {
            AABB range = player.getBoundingBox().inflate(10);
            List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, range, e -> e != player && e.getTeam() == null);
            for (LivingEntity target : list) {
                target.addEffect(new MobEffectInstance(EffectRegistry.HEAL_BLOCK.get(), 200, 0));
                target.addEffect(new MobEffectInstance(EffectRegistry.BLEEDING.get(), 200, 0));
            }
        }
        player.getCooldowns().addCooldown(this, 400);
        if (!level.isClientSide && level instanceof ServerLevel serverLevel) {
            WorldData tracker = WorldData.get(serverLevel);

            if (!tracker.hasBeenUsed()) {
                tracker.markUsed();
                player.displayClientMessage(Component.translatable("fromtheshadows.apocalypsehorn.text").withStyle(ChatFormatting.DARK_RED), true);
            }else
                player.displayClientMessage(Component.translatable("fromtheshadows.apocalypsehorn_used.text").withStyle(ChatFormatting.DARK_BLUE), true);
        }

        return InteractionResultHolder.pass(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 120;
    }

    @Override
    public void releaseUsing(ItemStack stack, Level world, LivingEntity entity, int timeLeft) {
    }
}
