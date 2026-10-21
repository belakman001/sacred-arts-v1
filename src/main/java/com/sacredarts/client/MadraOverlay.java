package com.sacredarts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Madra bar + stage label above the hotbar.
 * Reads from ClientData (server-synced) — the client's own capability copy
 * never gets enrolled, so reading it directly would always show nothing.
 */
public class MadraOverlay {
    public static final IGuiOverlay HUD = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!ClientData.enrolled) return;
        renderBar(mc, guiGraphics, width, height, ClientData.madra, ClientData.maxMadra,
                com.sacredarts.advancement.Stage.byId(ClientData.stageId).getDisplayName().getString()
                        + " • " + com.sacredarts.path.Path.byId(ClientData.pathId).getDisplayName().getString());
    };

    private static void renderBar(Minecraft mc, GuiGraphics g, int width, int height,
                                  double madra, double maxMadra, String label) {
        int barW = 120, barH = 6;
        int x = width / 2 - barW / 2;
        int y = height - 52;
        g.fill(x - 1, y - 1, x + barW + 1, y + barH + 1, 0xFF0B0B18);
        float pct = maxMadra <= 0 ? 0 : (float) (madra / maxMadra);
        int color = ClientData.cycling ? 0xFF93C5FD : 0xFF3B82F6;
        g.fill(x, y, x + (int) (barW * pct), y + barH, color);
        int lx = x + barW / 2 - mc.font.width(label) / 2;
        g.drawString(mc.font, label, lx, y - 11, 0xE0E0E0, false);
        String nums = (int) madra + " / " + (int) maxMadra;
        g.drawString(mc.font, nums, x + barW / 2 - mc.font.width(nums) / 2, y + 8, 0x93C5FD, false);
    }
}
