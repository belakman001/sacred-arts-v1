package com.sacredarts.client;

import com.sacredarts.network.SyncDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;

/** Client-only packet targets, kept out of common classes to avoid classloading issues. */
public class ClientPacketHandler {
    public static void openIronBodyScreen() {
        Minecraft.getInstance().setScreen(new IronBodyScreen());
    }

    public static void syncData(SyncDataPacket msg) {
        ClientData.update(msg.isEnrolled(), msg.getPathId(), msg.getStageId(),
                msg.getMadra(), msg.getMaxMadra(), msg.getMastery(), msg.getKills(),
                msg.getCycledTotal(), msg.getIronBodyId(), msg.hasGoldsign(),
                msg.isCycling(), msg.getLevel(), msg.getLevelXp(), msg.getXpForNext(),
                msg.getStageMaxLevel(), msg.isCopperSightEnabled());
    }

    /** Right-click with the Foundation Manual: choose a Path, or review stats if enrolled. */
    public static void openManualScreen(InteractionHand hand) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        if (ClientData.enrolled) {
            mc.setScreen(new StatsScreen());
        } else {
            mc.setScreen(new PathSelectScreen(hand));
        }
    }

    public static void openStatsScreen() {
        if (ClientData.enrolled) {
            Minecraft.getInstance().setScreen(new StatsScreen());
        }
    }
}
