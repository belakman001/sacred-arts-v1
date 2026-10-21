package com.sacredarts.item;

import com.sacredarts.SacredArts;
import net.minecraft.world.item.Item;
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
}
