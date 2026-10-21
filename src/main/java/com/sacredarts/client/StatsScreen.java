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
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

/**
 * K menu, toggled with K. Two tabs:
 * Stats — Path, stage, level progression, advancement progress.
 * Techniques — every technique with its key, cost and description.
 */
public class StatsScreen extends Screen {
    private int tab = 0; // 0 = stats, 1 = techniques

    protected StatsScreen() {
        super(Component.translatable("screen.sacredarts.stats"));
    }

    @Override
    protected void init() {
        int cx = this.width / 2;
        this.addRenderableWidget(Button.builder(Component.translatable("screen.sacredarts.tab_stats"),
                        b -> tab = 0)
                .bounds(cx - 105, 44, 100, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("screen.sacredarts.tab_techniques"),
                        b -> tab = 1)
                .bounds(cx + 5, 44, 100, 20)
                .build());
        this.addRenderableWidget(Button.builder(Component.translatable("screen.sacredarts.done"),
                        b -> this.minecraft.setScreen(null))
                .bounds(cx - 50, this.height - 32, 100, 20)
                .build());
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(g);
        int cx = this.width / 2;
        g.drawCenteredString(this.font, this.title, cx, 24, 0xFFFFFF);
        if (tab == 0) {
            renderStatsTab(g, cx);
        } else {
            renderTechniquesTab(g, cx);
        }
        super.render(g, mouseX, mouseY, partialTick);
    }

    private void renderStatsTab(GuiGraphics g, int cx) {
        int x = cx - 160;
        int y = 76;
        Path path = Path.byId(ClientData.pathId);
        Stage stage = Stage.byId(ClientData.stageId);

        y = line(g, x, y, label("stats.sacredarts.path") + ": " + path.getDisplayName().getString()
                + " §7(" + path.getAspects().getString() + ")");
        y = line(g, x, y, label("stats.sacredarts.stage") + ": " + stage.getDisplayName().getString());
        y += 2;

        // Level with XP progress bar.
        String levelLine = label("stats.sacredarts.level") + ": " + ClientData.level
                + " / " + ClientData.stageMaxLevel;
        y = line(g, x, y, levelLine);
        int barW = 200, barH = 6;
        g.fill(x, y, x + barW, y + barH, 0xFF0B0B18);
        float pct = ClientData.xpForNext <= 0 ? 0
                : Math.min(1f, (float) ClientData.levelXp / ClientData.xpForNext);
        g.fill(x, y, x + (int) (barW * pct), y + barH, 0xFFA855F7);
        String xpNums = ClientData.levelXp + " / " + ClientData.xpForNext + " XP";
        g.drawString(this.font, xpNums, x + barW + 6, y - 2, 0xA855F7, false);
        y += 14;

        y = line(g, x, y, label("stats.sacredarts.madra") + ": "
                + (int) ClientData.madra + " / " + (int) ClientData.maxMadra);
        y = line(g, x, y, label("stats.sacredarts.mastery") + ": " + ClientData.mastery
                + "    " + label("stats.sacredarts.kills") + ": " + ClientData.kills);
        y = line(g, x, y, label("stats.sacredarts.iron_body") + ": "
                + IronBody.byId(ClientData.ironBodyId).getDisplayName().getString());
        y = line(g, x, y, label("stats.sacredarts.goldsign") + ": "
                + label(ClientData.goldsign ? "stats.sacredarts.yes" : "stats.sacredarts.no"));
        y = line(g, x, y, label("stats.sacredarts.copper_sight") + ": "
                + label(ClientData.copperSightEnabled ? "stats.sacredarts.yes" : "stats.sacredarts.no")
                + " §7(G)");
        y += 8;

        y = line(g, x, y, "§l" + label("stats.sacredarts.to_advance"));
        String hint = Component.translatable(
                "advancement.sacredarts." + ClientData.stageId + ".hint").getString();
        String progress = advancementProgress();
        y = line(g, x, y, "§7" + hint + (progress.isEmpty() ? "" : " §f(" + progress + ")"));
    }

    private void renderTechniquesTab(GuiGraphics g, int cx) {
        int x = cx - 160;
        int y = 76;
        Path path = Path.byId(ClientData.pathId);
        y = line(g, x, y, "§l" + label("stats.sacredarts.techniques") + " — "
                + path.getDisplayName().getString());
        y += 4;

        List<Technique> techs = Techniques.forPath(path);
        for (int i = 0; i < techs.size(); i++) {
            Technique t = techs.get(i);
            y = techniqueEntry(g, x, y, keyName(i), t);
            y += 4;
        }
        Technique ult = Techniques.ultimateFor(path);
        techniqueEntry(g, x, y, "Sneak+" + keyName(3), ult);
    }

    private int techniqueEntry(GuiGraphics g, int x, int y, String key, Technique t) {
        y = line(g, x, y, "§e[" + key + "]§f " + t.getDisplayName().getString()
                + " §7(" + t.getMadraCost() + " " + label("stats.sacredarts.madra_unit") + ")");
        for (FormattedCharSequence seq : this.font.split(t.getDescription(), 320)) {
            g.drawString(this.font, seq, x + 12, y, 0xAAAAAA, false);
            y += 11;
        }
        return y;
    }

    private int line(GuiGraphics g, int x, int y, String text) {
        g.drawString(this.font, text, x, y, 0xE0E0E0, false);
        return y + 13;
    }

    private String label(String key) {
        return Component.translatable(key).getString();
    }

    private String advancementProgress() {
        return switch (ClientData.stageId) {
            case "foundation" -> (int) ClientData.cycledTotal + "/500";
            case "copper" -> ClientData.kills + "/20";
            case "iron" -> ClientData.mastery + "/25";
            case "jade" -> ClientData.mastery + "/50, " + (int) ClientData.cycledTotal + "/5000";
            case "lowgold" -> ClientData.mastery + "/100";
            case "highgold" -> ClientData.mastery + "/150";
            default -> "";
        };
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        // K toggles the menu closed. (The IN_GAME keybind can't fire while a
        // screen is open, so the screen handles its own close key.)
        if (keyCode == KeyBindings.MENU.getKey().getValue()) {
            this.onClose();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
