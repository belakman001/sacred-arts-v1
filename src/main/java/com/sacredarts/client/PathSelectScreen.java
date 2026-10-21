package com.sacredarts.client;

import com.sacredarts.network.ModNetwork;
import com.sacredarts.network.SelectPathPacket;
import com.sacredarts.path.Path;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;

/** Choose your Path. The manual is consumed once a Path is chosen. */
public class PathSelectScreen extends Screen {
    private final InteractionHand hand;

    public PathSelectScreen(InteractionHand hand) {
        super(Component.translatable("screen.sacredarts.path_select"));
        this.hand = hand;
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        int y = this.height / 2 - 55;
        Path[] paths = Path.values();
        for (int i = 0; i < paths.length; i++) {
            final Path path = paths[i];
            int col = i % 2;
            int row = i / 2;
            int x = cx - 165 + col * 170;
            int by = y + row * 32;
            this.addRenderableWidget(Button.builder(path.getDisplayName(), btn -> {
                                ModNetwork.CHANNEL.sendToServer(new SelectPathPacket(path.getId(), hand));
                                this.minecraft.setScreen(null);
                            })
                            .bounds(x, by, 160, 24)
                            .tooltip(Tooltip.create(path.getAspects()))
                            .build());
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 85, 0xFFFFFF);
        g.drawCenteredString(this.font,
                Component.translatable("screen.sacredarts.path_select.subtitle"),
                this.width / 2, this.height / 2 - 71, 0xAAAAAA);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
