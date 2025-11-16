package net.sonmok14.fromtheshadows.server.utils.event;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.animal.AbstractFish;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.living.*;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.sonmok14.fromtheshadows.server.utils.registry.*;

import java.util.*;

@Mod.EventBusSubscriber
public class ServerEvents {

    private static final double BASE_CHANCE = 0.25;
    private static final double LOOTING_BONUS_PER_LEVEL = 0.15;

    private static final double MAX_DAMAGE_BONUS = 5.0;
    private static final double MAX_ATTACK_SPEED_BONUS = 0.5;
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("7CB67D3C-60ED-4F1E-9242-11BBEF6F43AC");

    @SubscribeEvent
    public void onPlayerTick(net.minecraftforge.event.TickEvent.PlayerTickEvent event) {
        if (event.phase != net.minecraftforge.event.TickEvent.Phase.END) return;
        Player player = event.player;
        ItemStack weapon = player.getMainHandItem();
        if (weapon.getItem() != ItemRegistry.CRYSTALLIZED_DAGGER.get()) return;

        double health = player.getHealth();

        AttributeInstance speedAttr = player.getAttribute(Attributes.ATTACK_SPEED);
        if (speedAttr == null) return;
        speedAttr.removeModifier(SPEED_MODIFIER_UUID);

        if (health < 10.0) {
            double factor = (10.0 - health) / 10.0;
            double bonus = factor * MAX_ATTACK_SPEED_BONUS;

            AttributeModifier speedMod = new AttributeModifier(
                    SPEED_MODIFIER_UUID,
                    "LowHealthAttackSpeedBonus",
                    bonus,
                    AttributeModifier.Operation.ADDITION
            );

            speedAttr.addTransientModifier(speedMod);
        }
    }
    @SubscribeEvent
    public void onLivingDrops(LivingDropsEvent event) {
        LivingEntity target = event.getEntity();
        DamageSource source = event.getSource();
        Level world = target.level();
        if (!(source.getEntity() instanceof Player player)) return;

        ItemStack weapon = player.getMainHandItem();
        if (weapon.getItem() != ItemRegistry.CRYSTALLIZED_DAGGER.get()) {
            return;
        }


        if (target.getType().is(TagRegistry.NOT_DROP_CRYSTALLIZED_FRAGMENTS) || target instanceof Animal || target instanceof AbstractFish || target instanceof AbstractVillager || target instanceof AbstractGolem) {
            return;
        }


        int lootingLevel = net.minecraft.world.item.enchantment.EnchantmentHelper.getItemEnchantmentLevel(
                Enchantments.MOB_LOOTING, player.getMainHandItem());


        double chance = BASE_CHANCE + LOOTING_BONUS_PER_LEVEL * lootingLevel;

        if (Math.random() < chance) {
            ItemStack crystallized_dagger = new ItemStack(ItemRegistry.CRYSTALLIZED_FRAGMENT.get());
            ItemEntity drop = new ItemEntity(world, target.getX(), target.getY(), target.getZ(), crystallized_dagger);
            event.getDrops().add(drop);
        }
    }

    @SubscribeEvent
    public void onLivingUpdateEvent(LivingEvent.LivingTickEvent event) {


        if (event.getEntity() instanceof LivingEntity) {
            LivingEntity attacker = (LivingEntity) event.getEntity();
            List<Item> equipmentList = new ArrayList<>();
            attacker.getAllSlots().forEach((x) -> equipmentList.add(x.getItem()));

            List<Item> armorList = new ArrayList<>(1);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    if (attacker.getItemBySlot(slot) != null) {
                        armorList.add(attacker.getItemBySlot(slot).getItem());
                    }
                }

                boolean isWearingAll = armorList
                        .containsAll(Arrays.asList(ItemRegistry.PLAGUE_DOCTOR_MASK.get()));
                if (isWearingAll) {
                    if (!attacker.level().isClientSide) {
                        if(attacker.hasEffect(EffectRegistry.PLAGUE.get()))
                        {
                            attacker.removeEffect(EffectRegistry.PLAGUE.get());
                        }
                        Iterator<MobEffectInstance> itr = attacker.getActiveEffects().iterator();
                        ArrayList<MobEffect> compatibleEffects = new ArrayList<>();

                        while (itr.hasNext()) {
                            MobEffectInstance effect = itr.next();
                            if (effect.getEffect().getCategory().equals(MobEffectCategory.HARMFUL) && effect.isCurativeItem(new ItemStack(Items.MILK_BUCKET))) {
                                compatibleEffects.add(effect.getEffect());
                            }
                        }
                        if (compatibleEffects.size() > 0) {
                            MobEffectInstance selectedEffect = attacker.getEffect(compatibleEffects.get(attacker.level().random.nextInt(compatibleEffects.size())));
                            if (selectedEffect != null && !net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(new net.minecraftforge.event.entity.living.MobEffectEvent.Remove(attacker, selectedEffect))) {
                                if(attacker instanceof Player)
                                {
                                    final ItemStack itemStack = attacker.getItemBySlot(EquipmentSlot.HEAD);
                                    if (!((Player) attacker).getCooldowns().isOnCooldown(itemStack.getItem()))
                                    {
                                        boolean flag = attacker.removeEffect(selectedEffect.getEffect());
                                        if (flag) {
                                                ((Player) attacker).getCooldowns().addCooldown(itemStack.getItem(), 400);
                                            itemStack.hurtAndBreak(1, attacker, p -> p.broadcastBreakEvent(EquipmentSlot.HEAD));
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    @SubscribeEvent
    public void onFoodConsumed(LivingEntityUseItemEvent.Finish event) {
        LivingEntity entity = event.getEntity();
        ItemStack item = event.getItem();

        if (!(entity instanceof Player player)) return;
        if (!item.isEdible()) return;

        MobEffectInstance effect = player.getEffect(EffectRegistry.BLESSING_OF_GLUTTONY.get());
        if (effect == null) return;

        FoodProperties food = item.getItem().getFoodProperties();
        if (food == null) return;

        int originalHunger = food.getNutrition();
        float saturation = food.getSaturationModifier();

        int bonus = Math.round(originalHunger * 0.2f * (effect.getAmplifier() + 1));

        player.getFoodData().eat(bonus, saturation);
    }

    @SubscribeEvent
    public void onLivingDamage(LivingHurtEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        if (player.isCrouching()) {
            List<Item> armorList = new ArrayList<>(3);
            for (EquipmentSlot slot : EquipmentSlot.values()) {
                if (slot.getType() == EquipmentSlot.Type.ARMOR) {
                    if (player.getItemBySlot(slot) != null) {
                        armorList.add(player.getItemBySlot(slot).getItem());
                    }
                }
                boolean isWearingAll = armorList
                        .containsAll(Arrays.asList(ItemRegistry.CRUST_HEAD.get(),
                                ItemRegistry.CRUST_CHEST.get(), ItemRegistry.CRUST_LEGGINGS.get()));
                if (isWearingAll) {
                    event.setAmount(event.getAmount() * 0.5F);
                    for (EquipmentSlot crust_armor : new EquipmentSlot[]{EquipmentSlot.HEAD, EquipmentSlot.CHEST, EquipmentSlot.LEGS}) {
                        ItemStack armorPiece = player.getItemBySlot(crust_armor);
                        armorPiece.hurtAndBreak(10, player, (p) -> {
                            p.broadcastBreakEvent(crust_armor);
                        });
                    }
                    player.level().playSound(null, player.blockPosition(), SoundEvents.SHIELD_BLOCK, SoundSource.PLAYERS, 1.0F, 1.0F);
                }
            }
        }
        }

    private static boolean isWearingFullDiamondArmor(Player player) {
        ItemStack head = player.getInventory().getArmor(3);
        ItemStack chest = player.getInventory().getArmor(2);
        ItemStack legs = player.getInventory().getArmor(1);
        ItemStack feet = player.getInventory().getArmor(0);

        return head.getItem() == ItemRegistry.DIABOLIUM_HEAD.get() &&
                chest.getItem() == ItemRegistry.DIABOLIUM_CHEST.get() &&
                legs.getItem() == ItemRegistry.DIABOLIUM_LEGGINGS.get() &&
                feet.getItem() == ItemRegistry.DIABOLIUM_BOOTS.get();
    }

    @SubscribeEvent
    public void onLivingHurt(LivingDamageEvent event) {

        if (event.getSource() instanceof DamageSource) {
            if (event.getSource().getEntity() instanceof LivingEntity) {
                LivingEntity attacker = (LivingEntity) event.getSource().getEntity();
                LivingEntity target = event.getEntity();
                List<Item> equipmentList = new ArrayList<>();
                attacker.getAllSlots().forEach((x) -> equipmentList.add(x.getItem()));
                if (EnchantmentHelper.getEnchantments(attacker.getMainHandItem()).containsKey(EnchantmentRegistry.FREEZING.get())) {
                    int i = EnchantmentHelper.getItemEnchantmentLevel(EnchantmentRegistry.FREEZING.get(), attacker.getMainHandItem());
                    if (i > 0 && target.getTicksFrozen() == 0) {
                        target.playSound(SoundEvents.PLAYER_HURT_FREEZE, 2f, 1F);
                        int frozen = 250 * i;
                        target.setTicksFrozen(frozen);
                    }
                }
                DamageSource source = event.getSource();
                if (!(source.getEntity() instanceof Player armored_player)) {
                    return;
                }
                if (!isWearingFullDiamondArmor(armored_player)) {
                    return;
                }
                if (isWearingFullDiamondArmor(armored_player) && !target.hasEffect(EffectRegistry.BLEEDING.get())) {
                    target.addEffect(new MobEffectInstance(EffectRegistry.BLEEDING.get(), 200));
                }
                float damageDealt = event.getAmount();
                float healAmount = damageDealt / 6.0f;
                armored_player.heal(healAmount);
            }
        }
    }

    }