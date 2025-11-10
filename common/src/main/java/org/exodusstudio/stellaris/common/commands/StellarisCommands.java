package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.client.utils.WikiEntryTextRenderer;
import org.exodusstudio.stellaris.common.commands.helpers.ArgumentBuilder;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.menus.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

import java.util.ArrayList;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {

        CommandBuilder builder = CommandBuilder.of(dispatcher, "stellaris");
        builder.permission(2).addSubCommand(
                        builder.createSubCommand("screen")
                                .addSubCommand(builder.createSubCommand("tablet")
                                        .execute((context) -> {
                                            MenuRegistry.openExtendedMenu(context.getPlayer(), MainTabletMenu.createProvider());
                                            return context.success();
                                        })
                                )
                ).addSubCommand(builder.createSubCommand("test")
                        .addSubCommand(builder.createSubCommand("wikiLines")
                                .addArgument(ArgumentBuilder.of("lines", StringArgumentType.string())
                                        .addArgument(ArgumentBuilder.of("width", IntegerArgumentType.integer(0))))
                                .execute((context) -> {
                                    String lines = context.getArgument("lines", String.class);
                                    Integer width = context.getArgument("width", Integer.class);
                                    WikiEntryTextRenderer renderer = new WikiEntryTextRenderer(lines, width);

                                    for (ArrayList<WikiEntryTextRenderer.Word> line : renderer.lines) {
                                        StringBuilder sb = new StringBuilder();
                                        for (WikiEntryTextRenderer.Word word : line) sb.append(word.toString()).append(" ");
                                        Stellaris.LOG.error("Line: {}", sb);
                                    }

                                    return context.success();
                                })
                        ).addSubCommand(builder.createSubCommand("test")
                                .execute((context) -> {
                                    NetworkManager.sendToPlayer(context.getPlayer(), new OpenScreenPacket("test"));
                                    return context.failure();
                                })
                        )
                ).addSubCommand(builder.createSubCommand("oil")
                        .addSubCommand(builder.createSubCommand("get").execute((context -> {
                            if(!context.runByPlayer()) {
                                return context.failure();
                            }
                            ChunkAccess access = context.getPlayer().level().getChunk(context.getPlayer().getOnPos());
                            context.sendSuccess(Component.literal("Oil Level : " + access.stellaris$getChunkOilLevel()), true);

                            return 0;
                        })))
                        .addSubCommand(builder.createSubCommand("set")
                                .addArgument(ArgumentBuilder.of("quantity", IntegerArgumentType.integer(0, Stellaris.CONFIG.oilConfig.maxOil)))
                                .execute((context -> {

                                    ServerPlayer player = context.getPlayer();
                                    if(!context.runByPlayer()) {
                                        return context.failure();
                                    }

                                    int quantity = IntegerArgumentType.getInteger(context.context, "quantity");
                                    ChunkAccess access = player.level().getChunk(context.getPlayer().getOnPos());
                                    access.stellaris$setChunkOilLevel(quantity);
                                    context.sendSuccess(Component.literal("Oil Level : " + access.stellaris$getChunkOilLevel()), true);

                                    return 0;
                                })))
                )
                .register();


    }

}
