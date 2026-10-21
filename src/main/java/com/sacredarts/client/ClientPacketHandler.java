package com.sacredarts.client;

import net.minecraft.client.Minecraft;

/** Client-only packet targets, kept out of common classes to avoid classloading issues. */
public class ClientPacketHandler {
    public static void openIronBodyScreen() {
        Minecraft.getInstance().setScreen(new IronBodyScreen());
    }
}
