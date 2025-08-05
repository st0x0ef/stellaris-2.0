package org.exodusstudio.stellaris.common.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

import java.util.HashSet;
import java.util.function.Function;

public class CommandBuilder {

    public String commandName;
    public int permissionLevel = 0;
    public Function<CommandContext<CommandSourceStack>, Integer> commandFunction = (c) -> 1;

    public HashSet<CommandBuilder> subCommands = new HashSet<>();

    public final CommandDispatcher<CommandSourceStack> dispatcher;

    private CommandBuilder(CommandDispatcher<CommandSourceStack> dispatcher, String commandName) {
        this.dispatcher = dispatcher;
        this.commandName = commandName;
    }

    public CommandBuilder command(String name) {
        this.commandName = name;
        return this;
    }

    public CommandBuilder permission(int level) {
        this.permissionLevel = level;
        return this;
    }

    public CommandBuilder execute(Function<CommandContext<CommandSourceStack>, Integer> function) {
        this.commandFunction = function;
        return this;
    }

    public CommandBuilder addSubCommand(CommandBuilder command) {
        this.subCommands.add(command);
        return this;
    }

    public CommandBuilder createSubCommand(String name) {
        return new CommandBuilder(dispatcher, name);
    }

    public static CommandBuilder of(CommandDispatcher<CommandSourceStack> dispatcher, String commandName) {
        return new CommandBuilder(dispatcher, commandName);
    }

    public void register() {
        dispatcher.register(this.build());
    }

    public LiteralArgumentBuilder<CommandSourceStack> build() {
        if (commandFunction == null) {
            throw new IllegalStateException("Command function must be set before building the command.");
        }

        LiteralArgumentBuilder<CommandSourceStack> commandBuilder = Commands.literal(commandName)
            .requires(source -> source.hasPermission(permissionLevel))
            .executes(commandFunction::apply);

        for (CommandBuilder subCommand : subCommands) {
            commandBuilder.then(subCommand.build());
        }
        return commandBuilder;
    }

}
