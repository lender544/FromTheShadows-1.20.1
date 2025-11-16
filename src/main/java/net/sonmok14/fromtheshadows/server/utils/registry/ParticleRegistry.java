package net.sonmok14.fromtheshadows.server.utils.registry;

import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.sonmok14.fromtheshadows.server.Fromtheshadows;


public class ParticleRegistry {
    public static final DeferredRegister<ParticleType<?>> PARTICLES = DeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, Fromtheshadows.MODID);

    public static final RegistryObject<SimpleParticleType> SHADOW = PARTICLES.register("shadow", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> BLOOD = PARTICLES.register("blood", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LIGHTNING_TEST = PARTICLES.register("lightning_test", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LIGHTNING = PARTICLES.register("lightning", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> RED_LIGHTNING = PARTICLES.register("red_lightning", () -> new SimpleParticleType(false));
    public static final RegistryObject<SimpleParticleType> LIGHTNING_ON_GROUND = PARTICLES.register("lightning-on_ground", () -> new SimpleParticleType(false));
}
