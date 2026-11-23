package com.sacredarts.item;

import com.sacredarts.data.ModCapabilities;
import com.sacredarts.network.ModNetwork;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

/** Bottle 25 stored vital aura into a Filled Aura Vial. */
public class AuraVialItem extends Item {
    public AuraVialItem() {
        super(new Item.Properties().stacksTo(16));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (!data.isEnrolled()) {
                    sp.displayClientMessage(Component.translatable("message.sacredarts.not_enrolled"), false);
                    return;
                }
                if (data.getAura() < 25) {
                    sp.displayClientMessage(Component.translatable("message.sacredarts.not_enough_aura"), true);
                    return;
                }
                data.setAura(data.getAura() - 25);
                stack.shrink(1);
                ItemStack filled = new ItemStack(ModItems.FILLED_AURA_VIAL.get());
                if (!sp.getInventory().add(filled)) {
                    sp.drop(filled, false);
                }
                level.playSound(null, sp.blockPosition(), SoundEvents.BOTTLE_FILL, SoundSource.PLAYERS, 1.0f, 1.0f);
                ModNetwork.syncPlayer(sp);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
