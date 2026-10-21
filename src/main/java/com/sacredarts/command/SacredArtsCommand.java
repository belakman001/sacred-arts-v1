package com.sacredarts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sacredarts.advancement.Stage;
import com.sacredarts.data.ModCapabilities;
import com.sacredarts.network.ModNetwork;
import com.sacredarts.path.Path;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

import java.util.Arrays;

/**
 * Debug/utility commands.
 * /sacredarts status — print capability state (diagnoses "nothing happens" reports).
 * /sacredarts enroll <path> — force-enroll (playtest fallback).
 */
public class SacredArtsCommand {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("sacredarts")
                .then(Commands.literal("status").executes(ctx -> {
                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                    player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                        player.sendSystemMessage(Component.literal(
                                "[SacredArts] enrolled=" + data.isEnrolled()
                                        + " path=" + data.getPath().getId()
                                        + " stage=" + data.getStage().getId()
                                        + " madra=" + (int) data.getMadra() + "/" + (int) data.getMaxMadra()
                                        + " mastery=" + data.getMastery()
                                        + " kills=" + data.getKills()
                                        + " ironBody=" + data.getIronBody().getId()));
                    });
                    return 1;
                }))
                .then(Commands.literal("enroll")
                        .then(Commands.argument("path", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(Path.values()).map(Path::getId), b))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "path");
                                    player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                                        data.enroll(Path.byId(id));
                                        ModNetwork.syncPlayer(player);
                                        player.sendSystemMessage(Component.translatable(
                                                "message.sacredarts.enrolled", data.getPath().getDisplayName()));
                                    });
                                    return 1;
                                })))
                .then(Commands.literal("setlevel")
                        .then(Commands.argument("level", IntegerArgumentType.integer(1, 100))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    int lvl = IntegerArgumentType.getInteger(ctx, "level");
                                    player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                                        data.setLevel(lvl);
                                        ModNetwork.syncPlayer(player);
                                        player.sendSystemMessage(Component.literal(
                                                "[SacredArts] Level set to " + data.getLevel()));
                                    });
                                    return 1;
                                })))
                .then(Commands.literal("setstage")
                        .then(Commands.argument("stage", StringArgumentType.word())
                                .suggests((c, b) -> SharedSuggestionProvider.suggest(
                                        Arrays.stream(Stage.values()).map(Stage::getId), b))
                                .executes(ctx -> {
                                    ServerPlayer player = ctx.getSource().getPlayerOrException();
                                    String id = StringArgumentType.getString(ctx, "stage");
                                    player.getCapability(ModCapabilities.SACRED_ARTIST).ifPresent(data -> {
                                        Stage s = Stage.byId(id);
                                        data.setStage(s);
                                        if (data.getLevel() > s.getMaxLevel()) {
                                            data.setLevel(s.getMaxLevel());
                                        }
                                        data.setMadra(data.getMaxMadra());
                                        ModNetwork.syncPlayer(player);
                                        player.sendSystemMessage(Component.translatable(
                                                "message.sacredarts.stage_up", s.getDisplayName()));
                                    });
                                    return 1;
                                }))));
    }
}
