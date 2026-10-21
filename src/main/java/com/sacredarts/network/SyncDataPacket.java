package com.sacredarts.network;

import com.sacredarts.client.ClientPacketHandler;
import com.sacredarts.data.ISacredArtist;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

/** Server -> client: full sacred artist state for the HUD and menus. */
public class SyncDataPacket {
    private final boolean enrolled;
    private final String pathId;
    private final String stageId;
    private final double madra;
    private final double maxMadra;
    private final int mastery;
    private final int kills;
    private final double cycledTotal;
    private final String ironBodyId;
    private final boolean goldsign;
    private final boolean cycling;
    private final int level;
    private final int levelXp;
    private final int xpForNext;
    private final int stageMaxLevel;

    public SyncDataPacket(ISacredArtist data) {
        this.enrolled = data.isEnrolled();
        this.pathId = data.getPath().getId();
        this.stageId = data.getStage().getId();
        this.madra = data.getMadra();
        this.maxMadra = data.getMaxMadra();
        this.mastery = data.getMastery();
        this.kills = data.getKills();
        this.cycledTotal = data.getCycledTotal();
        this.ironBodyId = data.getIronBody().getId();
        this.goldsign = data.hasGoldsign();
        this.cycling = data.isCycling();
        this.level = data.getLevel();
        this.levelXp = data.getLevelXp();
        this.xpForNext = com.sacredarts.config.SacredArtsConfig.LEVEL_XP_PER_LEVEL.get();
        this.stageMaxLevel = data.getStage().getMaxLevel();
    }

    public SyncDataPacket(FriendlyByteBuf buf) {
        this.enrolled = buf.readBoolean();
        this.pathId = buf.readUtf();
        this.stageId = buf.readUtf();
        this.madra = buf.readDouble();
        this.maxMadra = buf.readDouble();
        this.mastery = buf.readInt();
        this.kills = buf.readInt();
        this.cycledTotal = buf.readDouble();
        this.ironBodyId = buf.readUtf();
        this.goldsign = buf.readBoolean();
        this.cycling = buf.readBoolean();
        this.level = buf.readInt();
        this.levelXp = buf.readInt();
        this.xpForNext = buf.readInt();
        this.stageMaxLevel = buf.readInt();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeBoolean(enrolled);
        buf.writeUtf(pathId);
        buf.writeUtf(stageId);
        buf.writeDouble(madra);
        buf.writeDouble(maxMadra);
        buf.writeInt(mastery);
        buf.writeInt(kills);
        buf.writeDouble(cycledTotal);
        buf.writeUtf(ironBodyId);
        buf.writeBoolean(goldsign);
        buf.writeBoolean(cycling);
        buf.writeInt(level);
        buf.writeInt(levelXp);
        buf.writeInt(xpForNext);
        buf.writeInt(stageMaxLevel);
    }

    public static void handle(SyncDataPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> ClientPacketHandler.syncData(msg));
        ctx.get().setPacketHandled(true);
    }

    public boolean isEnrolled() { return enrolled; }
    public String getPathId() { return pathId; }
    public String getStageId() { return stageId; }
    public double getMadra() { return madra; }
    public double getMaxMadra() { return maxMadra; }
    public int getMastery() { return mastery; }
    public int getKills() { return kills; }
    public double getCycledTotal() { return cycledTotal; }
    public String getIronBodyId() { return ironBodyId; }
    public boolean hasGoldsign() { return goldsign; }
    public boolean isCycling() { return cycling; }
    public int getLevel() { return level; }
    public int getLevelXp() { return levelXp; }
    public int getXpForNext() { return xpForNext; }
    public int getStageMaxLevel() { return stageMaxLevel; }
}
