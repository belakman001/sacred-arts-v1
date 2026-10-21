package com.sacredarts.client;

import com.sacredarts.data.IronBody;
import com.sacredarts.network.ModNetwork;
import com.sacredarts.network.SelectIronBodyPacket;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/** Permanent Iron Body choice on reaching the Iron stage. */
public class IronBodyScreen extends Screen {
    private static final IronBody[] CHOICES = {IronBody.BLOODFORGED, IronBody.RAINDROP, IronBody.OAKHEART};

    protected IronBodyScreen() {
        super(Component.translatable("screen.sacredarts.iron_body"));
    }

    @Override
    protected void init() {
        int y = this.height / 2 - 40;
        for (IronBody body : CHOICES) {
            final IronBody choice = body;
            this.addRenderableWidget(Button.builder(body.getDisplayName(), btn -> {
                        ModNetwork.CHANNEL.sendToServer(new SelectIronBodyPacket(choice));
                        this.minecraft.setScreen(null);
                    })
                    .bounds(this.width / 2 - 110, y, 220, 20)
                    .tooltip(net.minecraft.client.gui.components.Tooltip.create(body.getDescription()))
                    .build());
            y += 26;
        }
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        g.drawCenteredString(this.font, this.title, this.width / 2, this.height / 2 - 70, 0xFFFFFF);
        g.drawCenteredString(this.font,
                Component.translatable("screen.sacredarts.iron_body.subtitle"),
                this.width / 2, this.height / 2 - 56, 0xAAAAAA);
        super.render(g, mouseX, mouseY, partialTick);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
