package net.sonmok14.fromtheshadows.server.effect;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class EffectSatietyBoost extends MobEffect {
    public EffectSatietyBoost(MobEffectCategory category, int color) {
        super(MobEffectCategory.BENEFICIAL, 0XF2C224);
    }
    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        return true;
    }
}

