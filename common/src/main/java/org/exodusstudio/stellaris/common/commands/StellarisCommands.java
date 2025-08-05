package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.networking.NetworkManager;
import dev.architectury.registry.menu.MenuRegistry;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.exodusstudio.stellaris.common.commands.helpers.CommandBuilder;
import org.exodusstudio.stellaris.common.menu.MainTabletMenu;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {

        CommandBuilder builder = CommandBuilder.of(dispatcher, "stellaris");
        builder.addSubCommand(
                        builder.createSubCommand("screen")
                                .permission(2)
                                .addSubCommand(builder.createSubCommand("test")
                                        .execute((context) -> {
                                            NetworkManager.sendToPlayer(context.getPlayer(), new OpenScreenPacket("test"));
                                            return context.failure();
                                        })
                                )
                                .addSubCommand(builder.createSubCommand("tablet")
                                        .execute((context) -> {
                                            MenuRegistry.openExtendedMenu(context.getPlayer(), MainTabletMenu.createProvider());
                                            return context.success();
                                        })
                                )
                )
                .register();


    }

}
