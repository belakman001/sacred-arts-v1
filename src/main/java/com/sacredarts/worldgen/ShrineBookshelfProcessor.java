package com.sacredarts.worldgen;

import com.mojang.serialization.Codec;
import com.sacredarts.SacredArts;
import com.sacredarts.item.ModItems;
import net.minecraft.core.BlockPos;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructurePlaceSettings;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessor;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

/**
 * Runs when the Abandoned Shrine generates. Two jobs:
 * <ul>
 *   <li>Puts a real Foundation Manual into slot 1 of the chiseled bookshelf.
 *       The Structure Block save only kept the "slot occupied" flag, not the
 *       placeholder book's item data, so the manual is written in here.</li>
 *   <li>Attaches the shrine loot table to the chest (saved empty on purpose).</li>
 * </ul>
 */
public class ShrineBookshelfProcessor extends StructureProcessor {
    public static final Codec<ShrineBookshelfProcessor> CODEC = Codec.unit(ShrineBookshelfProcessor::new);

    public static final DeferredRegister<StructureProcessorType<?>> TYPES =
            DeferredRegister.create(Registries.STRUCTURE_PROCESSOR, SacredArts.MODID);

    public static final RegistryObject<StructureProcessorType<ShrineBookshelfProcessor>> SHRINE_BOOKSHELF =
            // In 1.20.1 StructureProcessorType is a functional interface (single
            // method codec()), not a class or record — a lambda implements it.
            // (The record form came in 1.20.2.)
            TYPES.register("shrine_bookshelf", () -> () -> CODEC);

    private static final ResourceLocation CHEST_LOOT =
            new ResourceLocation(SacredArts.MODID, "chests/abandoned_shrine");

    @Override
    protected StructureProcessorType<?> getType() {
        return SHRINE_BOOKSHELF.get();
    }

    // NOTE (2026-11-25): processBlock gets TWO block infos. blockInfo is the
    // TEMPLATE-LOCAL one; relativeBlockInfo carries the WORLD position.
    // placeInWorld uses the RETURNED info's pos() directly as the world
    // position for setBlock — so returning blockInfo (local pos) silently
    // placed every block at world coords 0..6, 0..7, 0..7 (near the origin,
    // underground) instead of at the shrine. Always return relativeBlockInfo
    // (or a new info built from its pos/state) — never blockInfo.
    @Override
    public StructureTemplate.StructureBlockInfo processBlock(LevelReader level, BlockPos pos, BlockPos piecePos,
            StructureTemplate.StructureBlockInfo blockInfo, StructureTemplate.StructureBlockInfo relativeBlockInfo,
            StructurePlaceSettings settings) {
        if (relativeBlockInfo.state().is(Blocks.CHISELED_BOOKSHELF)) {
            CompoundTag manual = new ItemStack(ModItems.FOUNDATION_MANUAL.get()).save(new CompoundTag());
            manual.putByte("Slot", (byte) 1);
            ListTag items = new ListTag();
            items.add(manual);
            CompoundTag nbt = new CompoundTag();
            nbt.put("Items", items);
            return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), nbt);
        }
        if (relativeBlockInfo.state().is(Blocks.CHEST)) {
            CompoundTag nbt = new CompoundTag();
            nbt.putString("LootTable", CHEST_LOOT.toString());
            return new StructureTemplate.StructureBlockInfo(relativeBlockInfo.pos(), relativeBlockInfo.state(), nbt);
        }
        return relativeBlockInfo;
    }
}
