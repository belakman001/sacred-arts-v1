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

/** Drink bottled vital aura: +25 stored aura and +20 madra. Returns the empty vial. 5-second cooldown. */
public class FilledAuraVialItem extends Item {
    public FilledAuraVialItem() {
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
                if (data.getAura() >= 100 && data.getMadra() >= data.getMaxMadra()) {
                    sp.displayClientMessage(Component.translatable("message.sacredarts.aura_full"), true);
                    return;
                }
                data.setAura(data.getAura() + 25);
                data.setMadra(data.getMadra() + 20);
                stack.shrink(1);
                ItemStack empty = new ItemStack(ModItems.AURA_VIAL.get());
                if (!sp.getInventory().add(empty)) {
                    sp.drop(empty, false);
                }
                sp.getCooldowns().addCooldown(this, 100);
                level.playSound(null, sp.blockPosition(), SoundEvents.GENERIC_DRINK, SoundSource.PLAYERS, 1.0f, 1.2f);
                ModNetwork.syncPlayer(sp);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
