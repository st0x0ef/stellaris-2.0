package org.exodusstudio.stellaris.common.commands.helpers;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ArgumentBuilder<T> {

    private final String argumentName;
    private final ArgumentType<T> argumentType;

    private ArgumentBuilder<?> subArgumentBuilder;

    private ArgumentBuilder(String argumentName, ArgumentType<T> argumentType) {
        this.argumentName = argumentName;
        this.argumentType = argumentType;
    }

    public static <T> ArgumentBuilder<T> of(String argumentName, ArgumentType<T> argumentType) {
        return new ArgumentBuilder<>(argumentName, argumentType);
    }

    public ArgumentBuilder<?> addArgument(ArgumentBuilder<?> subArgumentBuilder) {
        this.subArgumentBuilder = subArgumentBuilder;
        return this;
    }

    public RequiredArgumentBuilder<CommandSourceStack, ?> build(Function<CommandSourceWrapper, Integer> commandFunction, @Nullable RequiredArgumentBuilder<CommandSourceStack, ?> parentArgument) {
        RequiredArgumentBuilder<CommandSourceStack, ?> builder = RequiredArgumentBuilder.argument(argumentName, argumentType);

        if (subArgumentBuilder != null) {
            builder.then(subArgumentBuilder.build(commandFunction, null)); // Chaînage des sous-arguments
        }

        if (parentArgument != null) {
            parentArgument.then(builder); // Attachement à l'argument parent
            return parentArgument;
        }

        return builder.executes((c) ->  commandFunction.apply(new CommandSourceWrapper(c))); // Ajout de l'exécution si aucun parent
    }


}
