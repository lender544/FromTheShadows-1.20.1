package net.sonmok14.fromtheshadows.server;


import com.mojang.serialization.Codec;
import net.minecraft.world.entity.raid.Raid;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.StructureModifier;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.sonmok14.fromtheshadows.client.ClientProxy;
import net.sonmok14.fromtheshadows.server.config.BiomeConfig;
import net.sonmok14.fromtheshadows.server.config.ConfigHolder;
import net.sonmok14.fromtheshadows.server.config.FTSConfig;
import net.sonmok14.fromtheshadows.server.utils.event.ServerEvents;
import net.sonmok14.fromtheshadows.server.utils.registry.*;
import net.sonmok14.fromtheshadows.server.world.biome.FTSMobSpawnBiomeModifier;
import net.sonmok14.fromtheshadows.server.world.biome.FTSMobSpawnStructureModifier;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import software.bernie.geckolib.GeckoLib;


@Mod(Fromtheshadows.MODID)
public class Fromtheshadows
{
    public static Fromtheshadows instance;
    public static final String MODID = "fromtheshadows";
    public static final Logger LOGGER = LogManager.getLogger();

    public static CommonProxy PROXY = DistExecutor.runForDist(() -> ClientProxy::new, () -> CommonProxy::new);
    public Fromtheshadows() {
        instance = this;
        GeckoLib.initialize();
        final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        modEventBus.addListener(this::setup);
        PROXY.commonInit();
        modEventBus.addListener(this::onModConfigEvent);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::setup);
        modEventBus.addListener(this::enqueueIMC);
        modEventBus.addListener(this::processIMC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC, "from_the_shadows.toml");
        MinecraftForge.EVENT_BUS.register(this);
        MinecraftForge.EVENT_BUS.register(new ServerEvents());
        EntityRegistry.ENTITY_TYPES.register(modEventBus);
        BlockRegistry.BLOCKS.register(modEventBus);
        ParticleRegistry.PARTICLES.register(modEventBus);
        SoundRegistry.MOD_SOUNDS.register(modEventBus);
        EnchantmentRegistry.ENCHANTMENT.register(modEventBus);
        EffectRegistry.EFFECT.register(modEventBus);
        EffectRegistry.POTION.register(modEventBus);
        ItemRegistry.ITEMS.register(modEventBus);
        FTSCreativeTabRegistry.TABS.register(modEventBus);
        final DeferredRegister<Codec<? extends BiomeModifier>> biomeModifiers = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, Fromtheshadows.MODID);
        biomeModifiers.register(modEventBus);
        biomeModifiers.register("from_the_shadows_mob_spawns", FTSMobSpawnBiomeModifier::makeCodec);
        final DeferredRegister<Codec<? extends StructureModifier>> structureModifiers = DeferredRegister.create(ForgeRegistries.Keys.STRUCTURE_MODIFIER_SERIALIZERS, Fromtheshadows.MODID);
        structureModifiers.register(modEventBus);
        structureModifiers.register("from_the_shadows_structure_spawns", FTSMobSpawnStructureModifier::makeCodec);


    }

    private void setup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            Raid.RaiderType.create("fromtheshadows", EntityRegistry.CLERIC.get(), new int[]{0, 1, 2, 2, 0, 2, 0, 4});
        });
        EffectRegistry.init();
    }

    private void clientSetup(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> PROXY.clientInit());
    }

    @SubscribeEvent
    public void onModConfigEvent(final ModConfigEvent event) {
        final ModConfig config = event.getConfig();
        // Rebake the configs when they change
        if (config.getSpec() == ConfigHolder.COMMON_SPEC) {
            FTSConfig.bake(config);
        }
        BiomeConfig.init();
    }

    private void enqueueIMC(final InterModEnqueueEvent event)
    {
    }

    private void processIMC(final InterModProcessEvent event)
    {
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }
}
