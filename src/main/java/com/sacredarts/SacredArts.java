package com.sacredarts;

import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;

/**
 * Sacred Arts — a Cradle-inspired cultivation mod.
 *
 * Systems roadmap (packages to be added under com.sacredarts):
 *   advancement — Foundation -> Copper -> Iron -> Jade -> Lowgold -> Highgold
 *                 -> Truegold -> Underlord -> Overlord -> Archlord -> Sage/Herald -> Monarch
 *   madra       — madra aspects (blood, dream, fire, sword, ...) and cycling
 *   path        — Paths (Black Flame, Twin Stars, Endless Sword, ...) granting techniques
 *   technique   — Striker / Ruler / Forger / Enforcer techniques bound to keys/items
 *   item/block  — sacred instruments, scripted constructs, iconic weapons
 */
@Mod(SacredArts.MODID)
public class SacredArts {
    public static final String MODID = "sacredarts";
    private static final Logger LOGGER = LogUtils.getLogger();

    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final RegistryObject<CreativeModeTab> SACRED_ARTS_TAB =
            CREATIVE_MODE_TABS.register("sacred_arts", () -> CreativeModeTab.builder()
                    .withTabsBefore(CreativeModeTabs.COMBAT)
                    .icon(() -> new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER))
                    .title(Component.translatable("itemGroup.sacredarts"))
                    .displayItems((parameters, output) -> {
                        // Items register themselves into this tab as they are added.
                    })
                    .build());

    public SacredArts(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);

        LOGGER.info("Sacred Arts stirs. The Way opens.");
    }
}
