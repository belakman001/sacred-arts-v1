package com.sacredarts;

import com.mojang.logging.LogUtils;
import com.sacredarts.advancement.AdvancementHandler;
import com.sacredarts.command.SacredArtsCommand;
import com.sacredarts.config.SacredArtsConfig;
import com.sacredarts.data.ModCapabilities;
import com.sacredarts.data.SacredArtistProvider;
import com.sacredarts.entity.ModEntities;
import com.sacredarts.item.ModItems;
import com.sacredarts.network.ModNetwork;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Enemy;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * Sacred Arts — a Cradle-inspired cultivation mod.
 *
 * Systems:
 *   advancement — Foundation -> ... -> Monarch stage-ups with trials
 *   data        — per-player capability: stage, path, mastery, madra, iron body
 *   technique   — Striker / Enforcer / Ruler / Forger techniques per Path
 *   network     — keybind -> server technique casting, cycling, iron body choice
 *   client      — keybinds, madra HUD, iron body screen
 */
@Mod(SacredArts.MODID)
public class SacredArts {
    public static final String MODID = "sacredarts";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = ModItems.ITEMS;
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> SACRED_ARTS_TAB =
            CREATIVE_MODE_TABS.register("sacred_arts", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(ModItems.FOUNDATION_MANUAL.get()))
                    .title(Component.translatable("itemGroup.sacredarts"))
                    .displayItems((parameters, output) -> {
                        output.accept(ModItems.FOUNDATION_MANUAL.get());
                        output.accept(ModItems.REMNANT_CORE.get());
                        output.accept(ModItems.MADRA_PILL.get());
                    })
                    .build());

    public SacredArts(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        ModEntities.TYPES.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        ModNetwork.register();
        context.registerConfig(ModConfig.Type.COMMON, SacredArtsConfig.SPEC);

        MinecraftForge.EVENT_BUS.register(this);
        // NOTE: the filter must be Entity.class, not Player.class — Forge fires
        // AttachCapabilitiesEvent with Entity's base class as the generic type,
        // so a Player.class filter never matches and the capability never attaches.
        MinecraftForge.EVENT_BUS.addGenericListener(Entity.class, this::onAttachCapabilities);

        LOGGER.info("Sacred Arts stirs. The Way opens.");
    }

    private void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (!(event.getObject() instanceof Player)) return;
        SacredArtistProvider provider = new SacredArtistProvider();
        event.addCapability(new ResourceLocation(MODID, "sacred_artist"), provider);
        event.addListener(provider::invalidate);
    }

    @SubscribeEvent
    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END || event.player.level().isClientSide) return;
        if (event.player instanceof ServerPlayer sp) {
            sp.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (data.isEnrolled()) {
                    AdvancementHandler.tick(sp, data);
                }
            });
        }
    }

    @SubscribeEvent
    public void onLivingDeath(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer sp && event.getEntity() instanceof Enemy) {
            sp.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (!data.isEnrolled()) return;
                data.addKill();
                data.addMastery(SacredArtsConfig.MASTERY_PER_KILL.get());
                data.addLevelXp(SacredArtsConfig.LEVEL_XP_PER_KILL.get());
            });
        }
    }

    /** Mastery is never lost on death — copy the data to the respawned player. */
    @SubscribeEvent
    public void onPlayerClone(PlayerEvent.Clone event) {
        if (!event.isWasDeath()) return;
        event.getOriginal().getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(oldData ->
                event.getEntity().getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(newData ->
                        newData.deserializeNBT(oldData.serializeNBT())));
    }

    /** Push fresh state to the client on login so the HUD and menus are correct. */
    @SubscribeEvent
    public void onPlayerLogin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer sp) {
            ModNetwork.syncPlayer(sp);
        }
    }

    @SubscribeEvent
    public void onRegisterCommands(RegisterCommandsEvent event) {
        SacredArtsCommand.register(event.getDispatcher());
    }
}
