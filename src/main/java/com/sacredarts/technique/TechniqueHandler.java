package com.sacredarts.technique;

import com.sacredarts.config.SacredArtsConfig;
import com.sacredarts.data.ModCapabilities;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;

import java.util.List;

/** Server-side entry point for technique casts arriving from keybind packets. */
public class TechniqueHandler {

    /**
     * @param slot    0-3, matching the four keybinds (Striker, Enforcer, Ruler, Forger)
     * @param sneaking sneak + slot 3 casts the Path's ultimate instead
     */
    public static void castSlot(ServerPlayer player, int slot, boolean sneaking) {
        player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
            if (!data.isEnrolled()) {
                player.displayClientMessage(Component.translatable("message.sacredarts.not_enrolled"), true);
                return;
            }
            List<Technique> list = Techniques.forPath(data.getPath());
            Technique tech;
            if (sneaking && slot == 3) {
                tech = Techniques.ultimateFor(data.getPath());
            } else {
                if (slot < 0 || slot >= list.size()) return;
                tech = list.get(slot);
            }
            if (data.getMastery() < tech.getMasteryRequired()) {
                player.displayClientMessage(Component.translatable("message.sacredarts.mastery_locked",
                        tech.getDisplayName(), tech.getMasteryRequired()), true);
                return;
            }
            if (!data.spendMadra(tech.getMadraCost())) {
                player.displayClientMessage(Component.translatable("message.sacredarts.no_madra"), true);
                player.serverLevel().playSound(null, player.blockPosition(),
                        SoundEvents.VILLAGER_NO, SoundSource.PLAYERS, 0.8f, 1.0f);
                return;
            }
            tech.cast(player, data);
            data.addMastery(SacredArtsConfig.MASTERY_PER_CAST.get());
        });
    }
}
