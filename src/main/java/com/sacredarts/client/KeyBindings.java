package com.sacredarts.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.lwjgl.glfw.GLFW;

public class KeyBindings {
    public static final String CATEGORY = "key.categories.sacredarts";

    public static KeyMapping TECH_1;
    public static KeyMapping TECH_2;
    public static KeyMapping TECH_3;
    public static KeyMapping TECH_4;
    public static KeyMapping CYCLE;
    public static KeyMapping MENU;
    public static KeyMapping COPPER_SIGHT;

    public static void registerAll(RegisterKeyMappingsEvent event) {
        TECH_1 = new KeyMapping("key.sacredarts.technique_1", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_V, CATEGORY);
        TECH_2 = new KeyMapping("key.sacredarts.technique_2", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_B, CATEGORY);
        TECH_3 = new KeyMapping("key.sacredarts.technique_3", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_N, CATEGORY);
        TECH_4 = new KeyMapping("key.sacredarts.technique_4", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_M, CATEGORY);
        CYCLE = new KeyMapping("key.sacredarts.cycle", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_C, CATEGORY);
        MENU = new KeyMapping("key.sacredarts.menu", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_K, CATEGORY);
        COPPER_SIGHT = new KeyMapping("key.sacredarts.copper_sight", KeyConflictContext.IN_GAME,
                InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_G, CATEGORY);
        event.register(TECH_1);
        event.register(TECH_2);
        event.register(TECH_3);
        event.register(TECH_4);
        event.register(CYCLE);
        event.register(MENU);
        event.register(COPPER_SIGHT);
    }
}
