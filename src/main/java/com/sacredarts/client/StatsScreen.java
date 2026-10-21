package com.sacredarts.client;

import com.sacredarts.advancement.Stage;
import com.sacredarts.data.IronBody;
import com.sacredarts.path.Path;
import com.sacredarts.technique.Technique;
import com.sacredarts.technique.Techniques;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.List;

/** K menu: Path, stage, advancement requirement, techniques. */
public class StatsScreen extends Screen {
    protected StatsScreen() {
        super(Component.translatable("screen.sacredarts.stats"));
    }

    @Override
    protected void init() {
        this.addRenderableWidget(Button.builder(Component.translatable("screen.sacredarts.done"),
                        b -> this.minecraft.setScreen(null))
                .bounds(this.width / 2 - 50, this.height - 32, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        int cx = this.width / 2;
        int y = 20;
        g.drawCenteredString(this.font, this.title, cx, y, 0xFFFFFF);
        y += 18;

        Path path = Path.byId(ClientData.pathId);
        Stage stage = Stage.byId(ClientData.stageId);
        List<String> lines = new ArrayList<>();
        lines.add(label("stats.sacredarts.path") + ": " + path.getDisplayName().getString()
                + " (" + path.getAspects().getString() + ")");
        lines.add(label("stats.sacredarts.stage") + ": " + stage.getDisplayName().getString());
        lines.add(label("stats.sacredarts.madra") + ": " + (int) ClientData.madra + " / " + (int) ClientData.maxMadra);
        lines.add(label("stats.sacredarts.mastery") + ": " + ClientData.mastery
                + "    " + label("stats.sacredarts.kills") + ": " + ClientData.kills);
        lines.add(label("stats.sacredarts.iron_body") + ": "
                + IronBody.byId(ClientData.ironBodyId).getDisplayName().getString());
        lines.add(label("stats.sacredarts.goldsign") + ": "
                + label(ClientData.goldsign ? "stats.sacredarts.yes" : "stats.sacredarts.no"));
        lines.add(label("stats.sacredarts.to_advance") + ": "
                + Component.translatable("advancement.sacredarts." + ClientData.stageId + ".hint").getString());
        lines.add("");
        lines.add(label("stats.sacredarts.techniques") + ":");

        List<Technique> techs = Techniques.forPath(path);
        for (int i = 0; i < techs.size(); i++) {
            Technique t = techs.get(i);
            String keyName = keyName(i);
            String line = keyName + " — " + t.getDisplayName().getString() + " (" + t.getMadraCost() + " madra)";
            if (ClientData.mastery < t.getMasteryRequired()) {
                line = "§7" + line + " [requires " + t.getMasteryRequired() + " mastery]";
            }
            lines.add(line);
        }
        Technique ult = Techniques.ultimateFor(path);
        String u = "Sneak+" + keyName(3) + " — " + ult.getDisplayName().getString()
                + " (" + ult.getMadraCost() + " madra)";
        if (ClientData.mastery < ult.getMasteryRequired()) {
            u = "§7" + u + " [requires " + ult.getMasteryRequired() + " mastery]";
        }
        lines.add(u);

        for (String line : lines) {
            g.drawString(this.font, line, cx - 160, y, 0xE0E0E0, false);
            y += 12;
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    private String label(String key) {
        return Component.translatable(key).getString();
    }

    private String keyName(int slot) {
        return switch (slot) {
            case 0 -> KeyBindings.TECH_1.getKey().getDisplayName().getString();
            case 1 -> KeyBindings.TECH_2.getKey().getDisplayName().getString();
            case 2 -> KeyBindings.TECH_3.getKey().getDisplayName().getString();
            default -> KeyBindings.TECH_4.getKey().getDisplayName().getString();
        };
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
