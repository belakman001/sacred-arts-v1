package com.sacredarts.item;

import com.sacredarts.client.ClientPacketHandler;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/**
 * Right-click to open the Path selection menu (or your stats if already enrolled).
 * Choosing a Path consumes the manual.
 */
public class FoundationManualItem extends Item {
    public FoundationManualItem() {
        super(new Item.Properties().stacksTo(1));
    }

    /** Enchantment glint so it reads as special, not a plain book. */
    @Override
    public boolean isFoil(ItemStack stack) {
        return true;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (level.isClientSide) {
            // This branch never executes on a dedicated server, so the client-only
            // classes referenced here are never loaded there.
            ClientPacketHandler.openManualScreen(hand);
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
