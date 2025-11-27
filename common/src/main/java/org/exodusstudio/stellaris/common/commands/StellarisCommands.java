package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
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
                )
                .register();

    }

}
