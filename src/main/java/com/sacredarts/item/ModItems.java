package com.sacredarts.item;

import com.sacredarts.SacredArts;
import com.sacredarts.entity.ModEntities;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, SacredArts.MODID);

    public static final RegistryObject<Item> FOUNDATION_MANUAL =
            ITEMS.register("foundation_manual", FoundationManualItem::new);
    public static final RegistryObject<Item> REMNANT_CORE =
            ITEMS.register("remnant_core", RemnantCoreItem::new);
    public static final RegistryObject<Item> MADRA_PILL =
            ITEMS.register("madra_pill", MadraPillItem::new);
    public static final RegistryObject<Item> AURA_VIAL =
            ITEMS.register("aura_vial", AuraVialItem::new);
    public static final RegistryObject<Item> FILLED_AURA_VIAL =
            ITEMS.register("filled_aura_vial", FilledAuraVialItem::new);

    // Colored Remnant Cores (Sav's art). Plain items for now — no job yet.
    // Earmarked as Soul Smithing material: crafting special items, tools,
    // and weapons. That system is still just an idea, not designed.
    public static final RegistryObject<Item> REMNANT_CORE_BLACK =
            ITEMS.register("remnant_core_black", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REMNANT_CORE_GOLD =
            ITEMS.register("remnant_core_gold", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REMNANT_CORE_RED =
            ITEMS.register("remnant_core_red", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REMNANT_CORE_BLUE =
            ITEMS.register("remnant_core_blue", () -> new Item(new Item.Properties()));
    public static final RegistryObject<Item> REMNANT_CORE_LIGHT_GREEN =
            ITEMS.register("remnant_core_light_green", () -> new Item(new Item.Properties()));

    /**
     * Sacred Fox spawn egg — white with purple spots, matching Kaleb's fox.
     * ForgeSpawnEggItem takes the RegistryObject as a Supplier, so the fox
     * entity type is only resolved when the egg is actually used — calling
     * .get() here crashed the game at launch (0.14.0), because items
     * register before entity types.
     */
    public static final RegistryObject<Item> SACRED_FOX_SPAWN_EGG =
            ITEMS.register("sacred_fox_spawn_egg", () -> new ForgeSpawnEggItem(
                    ModEntities.SACRED_FOX, 0xF5F5F5, 0x966EB0, new Item.Properties()));
}
