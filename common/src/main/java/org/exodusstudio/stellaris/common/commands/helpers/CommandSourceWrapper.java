package org.exodusstudio.stellaris.common.commands.helpers;

import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

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

    public boolean runByPlayer() {
        if(getPlayer() == null) {
            this.sendFailure(Component.literal("This command need to be run by a player"));
            return false;
        }
        return true;
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
