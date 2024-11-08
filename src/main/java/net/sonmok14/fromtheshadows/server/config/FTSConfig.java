package net.sonmok14.fromtheshadows.server.config;

import net.minecraftforge.fml.config.ModConfig;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;

public class FTSConfig {



    public static double nehemoth_health_multiplier = 1.0D;
    public static double nehemoth_laser_damage = 3.0D;
    public static double nehemoth_ranged_damage = 3.0D;
    public static double nehemoth_melee_damage_multiplier = 1.0D;


    public static double froglin_health_multiplier = 1.0D;
    public static double froglin_vomit_damage = 7.0D;
    public static double froglin_melee_damage_multiplier = 1.0D;

    public static double bulldrogioth_health_multiplier = 1.0D;
    public static double bulldrogioth_melee_damage_multiplier = 1.0D;

    public static double cleric_projectile_damage = 1.0D;
    public static double cleric_melee_damage_multiplier = 1.0D;
    public static double cleric_health_multiplier = 1.0D;

    public static int bulldrogiothShipwreckSpawnRate = 5;
    public static int clericSpawnRate = 5;
    public static int clericSpawnRolls = 25;
    public static int soulfirenehemothSpawnRate = 1;
    public static int nehemothSpawnRate = 2;
    public static int nehemothSpawnRolls = 15;
    public static int nehemothFortressSpawnRate = 1;
    public static int bulldrogiothSpawnRate = 5;
    public static int bulldrogiothSpawnRolls = 15;
    public static int froglinSpawnRate = 2;
    public static int froglinSpawnRolls = 8;
    //--------------------------------------------------------------------------------
    public static double thirst_for_blood_damage = 8.0D;
    public static double thirst_for_blood_laser_damage = 9.0D;
    public static double devil_splitter_damage = 7.0D;
    public static int diabolium_helmet_armor_value= 3;
    public static int diabolium_chestplate_armor_value = 8;
    public static int diabolium_leggings_armor_value = 6;
    public static int diabolium_armor_durability = 24;
    public static int crust_helmet_armor_value = 4;
    public static int crust_chestplate_armor_value = 9;
    public static int crust_leggings_armor_value = 7;
    public static int crust_armor_durability = 30;
    public static int plague_mask_armor_value = 3;
    public static int plague_mask_durability = 8;
    

    public static void bake(ModConfig config) {
        try {
            froglin_health_multiplier  = ConfigHolder.COMMON.froglin_health.get();
            froglin_vomit_damage = ConfigHolder.COMMON.froglin_vomit_damage.get();
            froglin_melee_damage_multiplier = ConfigHolder.COMMON.froglin_melee_damage.get();

            bulldrogioth_health_multiplier = ConfigHolder.COMMON.bulldrogioth_health.get();
            bulldrogioth_melee_damage_multiplier = ConfigHolder.COMMON.bulldrogioth_melee_damage.get();

            cleric_projectile_damage = ConfigHolder.COMMON.cleric_projectile_damage.get();
            cleric_melee_damage_multiplier = ConfigHolder.COMMON.cleric_melee_damage.get();
            cleric_health_multiplier = ConfigHolder.COMMON.cleric_health.get();

             bulldrogiothShipwreckSpawnRate = ConfigHolder.COMMON.bulldrogiothShipwreckSpawnRate.get();
             clericSpawnRate = ConfigHolder.COMMON.clericSpawnRate.get();
             clericSpawnRolls = ConfigHolder.COMMON.clericSpawnRolls.get();
             soulfirenehemothSpawnRate = ConfigHolder.COMMON.soulfirenehemothSpawnRate.get();
             nehemothSpawnRate = ConfigHolder.COMMON.nehemothSpawnRate.get();
             nehemothSpawnRolls = ConfigHolder.COMMON.nehemothSpawnRate.get();
             nehemothFortressSpawnRate = ConfigHolder.COMMON.nehemothFortressSpawnRate.get();
             bulldrogiothSpawnRate = ConfigHolder.COMMON.bulldrogiothSpawnRate.get();
             bulldrogiothSpawnRolls = ConfigHolder.COMMON.bulldrogiothSpawnRolls.get();
             froglinSpawnRate = ConfigHolder.COMMON.froglinSpawnRate.get();
             froglinSpawnRolls = ConfigHolder.COMMON.froglinSpawnRolls.get();
            //--------------------------------------------------------------------------------
            thirst_for_blood_damage = ConfigHolder.COMMON.thirst_for_blood_damage.get();
            thirst_for_blood_laser_damage = ConfigHolder.COMMON.thirst_for_blood_laser_damage.get();
            devil_splitter_damage = ConfigHolder.COMMON.devil_splitter_damage.get();
            diabolium_helmet_armor_value = ConfigHolder.COMMON.diabolium_helmet_armor_value.get();
            diabolium_chestplate_armor_value = ConfigHolder.COMMON.diabolium_chestplate_armor_value.get();
            diabolium_leggings_armor_value = ConfigHolder.COMMON.diabolium_leggings_armor_value.get();
            diabolium_armor_durability = ConfigHolder.COMMON.diabolium_armor_durability.get();
            crust_helmet_armor_value = ConfigHolder.COMMON.crust_helmet_armor_value.get();
            crust_chestplate_armor_value = ConfigHolder.COMMON.crust_chestplate_armor_value.get();
            crust_leggings_armor_value = ConfigHolder.COMMON.crust_leggings_armor_value.get();
            crust_armor_durability = ConfigHolder.COMMON.crust_armor_durability.get();
            plague_mask_armor_value = ConfigHolder.COMMON.plague_mask_armor_value.get();
            plague_mask_durability = ConfigHolder.COMMON.plague_mask_durability.get();

        } catch (Exception e) {
            Fromtheshadows.LOGGER.warn("An exception was caused trying to load the config for FTS");
            e.printStackTrace();
        }
    }
}