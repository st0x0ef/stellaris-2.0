package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import dev.architectury.networking.NetworkManager;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import org.exodusstudio.stellaris.common.network.packets.OpenScreenPacket;

public class StellarisCommands {

    public StellarisCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext registry, Commands.CommandSelection selection) {
        dispatcher.register(Commands.literal("stellaris")
                .then(Commands.literal("tablet")
                        .executes((context) -> {
                            NetworkManager.sendToPlayer(context.getSource().getPlayer(), new OpenScreenPacket("test"));
                            //Minecraft.getInstance().setScreen(new TestScreen());
                            return 0;
                        })
                )
        );

    }

}
