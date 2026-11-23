package com.sacredarts.client;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

/**
 * Spirit Scan readout near the crosshair: what Jade Sense sees in the target.
 * Reads ClientData.scanText, which the server refreshes while you look at it.
 */
public class SpiritScanOverlay {
    public static final IGuiOverlay HUD = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.options.hideGui) return;
        if (!ClientData.enrolled || ClientData.scanText == null) return;
        if (mc.level == null || mc.level.getGameTime() > ClientData.scanExpiryTick) return;
        String[] lines = ClientData.scanText.split("\n");
        int y = height / 2 + 26;
        int maxW = 0;
        for (String line : lines) {
            maxW = Math.max(maxW, mc.font.width(line));
        }
        int x0 = width / 2 - maxW / 2 - 6;
        int y0 = y - 4;
        int y1 = y + lines.length * 11 + 3;
        guiGraphics.fill(x0, y0, x0 + maxW + 12, y1, 0xB0000000);
        for (String line : lines) {
            guiGraphics.drawString(mc.font, line, width / 2 - mc.font.width(line) / 2, y, 0xE8D9FF, false);
            y += 11;
        }
    };
}
