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

/**
 * Reading this at Foundation enrolls you — and the Path chooses you.
 * Random assignment from the launch pool, like the power choosing its wielder.
 */
public class FoundationManualItem extends Item {
    public FoundationManualItem() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!level.isClientSide && player instanceof ServerPlayer sp) {
            sp.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                if (data.isEnrolled()) {
                    sp.displayClientMessage(Component.translatable("message.sacredarts.already_enrolled",
                            data.getPath().getDisplayName()), false);
                    return;
                }
                Path[] paths = Path.values();
                Path chosen = paths[sp.getRandom().nextInt(paths.length)];
                data.enroll(chosen);
                sp.displayClientMessage(
                        Component.translatable("message.sacredarts.enrolled", chosen.getDisplayName()), false);
                sp.displayClientMessage(
                        Component.translatable("message.sacredarts.enrolled_aspects", chosen.getAspects()), false);
                sp.displayClientMessage(Component.translatable("message.sacredarts.enrolled_hint"), false);
                stack.shrink(1);
                level.playSound(null, sp.blockPosition(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.2f);
            });
        }
        return InteractionResultHolder.sidedSuccess(stack, level.isClientSide);
    }
}
