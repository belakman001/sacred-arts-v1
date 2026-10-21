package com.sacredarts.client;

import com.sacredarts.SacredArts;
import com.sacredarts.data.ModCapabilities;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/** Madra bar + stage label above the hotbar. */
public class MadraOverlay {
    public static final IGuiOverlay HUD = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        mc.player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
            if (!data.isEnrolled()) return;
            renderBar(mc, guiGraphics, width, height, data.getMadra(), data.getMaxMadra(),
                    data.getStage().getDisplayName().getString()
                            + " • " + data.getPath().getDisplayName().getString());
        });
    };

    private static void renderBar(Minecraft mc, GuiGraphics g, int width, int height,
                                  double madra, double maxMadra, String label) {
        int barW = 120, barH = 6;
        int x = width / 2 - barW / 2;
        int y = height - 52;
        g.fill(x - 1, y - 1, x + barW + 1, y + barH + 1, 0xFF0B0B18);
        float pct = maxMadra <= 0 ? 0 : (float) (madra / maxMadra);
        g.fill(x, y, x + (int) (barW * pct), y + barH, 0xFF3B82F6);
        if (isCycling(mc)) {
            g.fill(x, y, x + (int) (barW * pct), y + barH, 0xFF93C5FD);
        }
        int lx = x + barW / 2 - mc.font.width(label) / 2;
        g.drawString(mc.font, label, lx, y - 11, 0xE0E0E0, false);
        String nums = (int) madra + " / " + (int) maxMadra;
        g.drawString(mc.font, nums, x + barW / 2 - mc.font.width(nums) / 2, y + 8, 0x93C5FD, false);
    }

    private static boolean isCycling(Minecraft mc) {
        return mc.player != null && mc.player.getCapability(ModCapabilities.SACRED_ARTIST)
                .map(d -> d.isEnrolled() && d.isCycling()).orElse(false);
    }
}
