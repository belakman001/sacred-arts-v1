package com.sacredarts.item;

import com.sacredarts.data.ModCapabilities;
import com.sacredarts.path.Path;
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

import java.util.ArrayList;
import java.util.List;

/** Abandon your Path and let a new one choose you. Stage and madra are kept; mastery resets. */
public class RemnantCoreItem extends Item {
    public RemnantCoreItem() {
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
                List<Path> others = new ArrayList<>(List.of(Path.values()));
                others.remove(data.getPath());
                Path chosen = others.get(sp.getRandom().nextInt(others.size()));
                data.setPath(chosen);
                data.setMastery(0);
                data.setMadra(data.getMaxMadra());
                sp.displayClientMessage(
                        Component.translatable("message.sacredarts.rerolled", chosen.getDisplayName()), false);
                stack.shrink(1);
                level.playSound(null, sp.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0f, 1.2f);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
