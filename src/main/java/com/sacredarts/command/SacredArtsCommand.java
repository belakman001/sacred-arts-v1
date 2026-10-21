package com.sacredarts.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
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
                                }))));
    }
}
