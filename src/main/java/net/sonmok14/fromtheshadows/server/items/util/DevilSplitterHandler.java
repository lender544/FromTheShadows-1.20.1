package net.sonmok14.fromtheshadows.server.items.util;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.sonmok14.fromtheshadows.server.entity.FTSMobType;
import net.sonmok14.fromtheshadows.server.entity.mob.NehemothEntity;
import net.sonmok14.fromtheshadows.server.utils.registry.ParticleRegistry;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DevilSplitterHandler{
    private static final int MAX_HITS = 6;
    private static final long TIME_LIMIT_MS = 20 * 1000; // 20초

    // Map<공격자 UUID, Map<대상 UUID, HitData>>
    private static final Map<UUID, Map<UUID, HitData>> hitTracker = new HashMap<>();

    public static void onHitEntity(LivingEntity attacker, LivingEntity target) {
        UUID attackerId = attacker.getUUID();
        UUID targetId = target.getUUID();

        long now = System.currentTimeMillis();

        hitTracker.putIfAbsent(attackerId, new HashMap<>());
        Map<UUID, HitData> targetMap = hitTracker.get(attackerId);
        HitData hitData = targetMap.getOrDefault(targetId, new HitData());

        // 시간이 초과되었으면 초기화
        if (now - hitData.startTime > TIME_LIMIT_MS) {
            hitData = new HitData();
        }

        hitData.hits++;
        hitData.startTime = hitData.startTime == 0 ? now : hitData.startTime;

        if (hitData.hits == MAX_HITS) {
            float damage = getFullAttackDamage((Player) attacker, target);

            // 2배 피해
            target.hurt(target.damageSources().playerAttack((Player) attacker), damage);
            if (attacker.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(ParticleRegistry.RED_LIGHTNING.get(),
                        target.getX(), target.getY() + target.getBbHeight() / 2, target.getZ(),
                        12,
                        0, 0, 0,
                        1);
            }
            attacker.level().playSound(null, attacker.blockPosition(), SoundEvents.ENDER_DRAGON_HURT, SoundSource.PLAYERS, 1.0F, 1.0F);

            // 초기화
            targetMap.remove(targetId);
        } else {
            targetMap.put(targetId, hitData);
        }

        // 정리
        hitTracker.put(attackerId, targetMap);
    }

    private static float getFullAttackDamage(Player player, LivingEntity target) {
        float baseDamage = (float) player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        ItemStack weapon = player.getMainHandItem();

        // 인챈트 배율 적용
        float enchantBonus = EnchantmentHelper.getDamageBonus(weapon, target.getMobType());

        // 크리티컬 히트인지 확인
        boolean isCrit = player.fallDistance > 0.0F && !player.onGround() && !player.onClimbable() &&
                !player.isInWater() && !player.hasEffect(MobEffects.BLINDNESS) && !player.isPassenger();

        // 공격 차징 상태 (0.0 ~ 1.0)
        float attackStrength = player.getAttackStrengthScale(0.5F); // 0.5F는 현재 틱 기준

        // 차징 보정
        float totalDamage = baseDamage * (0.2F + attackStrength * attackStrength * 0.8F);
        if (enchantBonus > 0) {
            totalDamage += enchantBonus * attackStrength;
        }

        if (isCrit) {
            totalDamage *= 1.5F; // 크리티컬 배율
        }
        if (player.hasEffect(MobEffects.DAMAGE_BOOST)) {
            int amplifier = player.getEffect(MobEffects.DAMAGE_BOOST).getAmplifier();
            totalDamage += 3 * (amplifier + 1);
        }
        return totalDamage * 2.0F; // 능력 발동 시 2배
    }

    private static class HitData {
        int hits = 0;
        long startTime = 0;
    }
}