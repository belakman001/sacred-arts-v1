package com.sacredarts.item;

import com.sacredarts.data.ModCapabilities;
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

/** Restores 40 madra instantly. 5-second cooldown. */
public class MadraPillItem extends Item {
    private static final double RESTORE = 40.0;

    public MadraPillItem() {
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
                if (data.getMadra() >= data.getMaxMadra()) {
                    sp.displayClientMessage(Component.translatable("message.sacredarts.madra_full"), true);
                    return;
                }
                data.setMadra(data.getMadra() + RESTORE);
                stack.shrink(1);
                sp.getCooldowns().addCooldown(this, 100);
                level.playSound(null, sp.blockPosition(), SoundEvents.GENERIC_EAT, SoundSource.PLAYERS, 1.0f, 1.2f);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
