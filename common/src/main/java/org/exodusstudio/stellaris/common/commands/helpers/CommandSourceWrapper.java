package org.exodusstudio.stellaris.common.commands.helpers;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public class CommandSourceWrapper {

    public final CommandContext<CommandSourceStack> context;

    public CommandSourceWrapper(CommandContext<CommandSourceStack> context) {
        this.context = context;
    }

    public ServerPlayer getPlayer() {
        return context.getSource().getPlayer();
    }

    public void sendFailure(Component component) {
        context.getSource().sendFailure(component);
    }

    public void sendSuccess(Component component, boolean logging) {
        context.getSource().sendSuccess(() -> component, logging);
    }

    public MinecraftServer getServer() {
        return context.getSource().getServer();
    }

    public <T> T getArgument(String name, Class<T> type) {
        return context.getArgument(name, type);
    }

    public int success() {
        return 1;
    }

    public int failure() {
        return 0;
    }

}
