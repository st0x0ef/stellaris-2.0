package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import dev.architectury.networking.NetworkManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.exodusstudio.stellaris.Stellaris;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {

        CommandBuilder builder = CommandBuilder.of(dispatcher, "stellaris");
        builder.addSubCommand(
                        builder.createSubCommand("screen")
                                .permission(2)
                                .addSubCommand(builder.createSubCommand("tablet")
                                        .addArgument(builder.createArgument("int", IntegerArgumentType.integer())
                                                .addArgument(builder.createArgument("string", StringArgumentType.string()))).execute((context) -> {
                                            Stellaris.LOG.error("Value: {} {}", context.getArgument("int", Integer.class), context.getArgument("string", String.class) );
                                            NetworkManager.sendToPlayer(context.getPlayer(), new OpenScreenPacket("test"));
                                            return 0;
                                        })
                                )
                ).addSubCommand(
                        builder.createSubCommand("gui")
                                .execute((c) -> {
                                    Stellaris.LOG.error("ee");
                                    return 1;
                                })
                )
                .register();


    }

}
